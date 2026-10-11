package com.example

import com.example.data.local.entity.CachedGitHubItemEntity
import com.example.data.local.entity.CachedJiraTicketEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.model.ServiceType
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.network.github.GitHubItemType
import com.example.data.network.github.GitHubWorkItem
import com.example.data.repository.SyncFlowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun gitHubWorkItem_modelValidation() {
        val item = GitHubWorkItem(
            id = "gh_pr_101",
            number = 101,
            title = "feat: Automated Slack and Jira sync",
            type = GitHubItemType.PULL_REQUEST,
            state = "OPEN",
            author = "dev-lead",
            authorAvatar = null,
            branchInfo = "main ← feature/sync",
            htmlUrl = "https://github.com/google/accompanist/pull/101",
            updatedAt = "2026-10-10",
            isDraft = false,
            labels = listOf("enhancement", "PR")
        )

        assertEquals("gh_pr_101", item.id)
        assertEquals(101, item.number)
        assertEquals(GitHubItemType.PULL_REQUEST, item.type)
        assertEquals("OPEN", item.state)
        assertTrue(item.labels.contains("enhancement"))
    }

    @Test
    fun cachedGitHubItemEntity_roundTripConversion() {
        val domain = GitHubWorkItem(
            id = "gh_pr_301",
            number = 301,
            title = "Add Room database caching layer",
            type = GitHubItemType.PULL_REQUEST,
            state = "OPEN",
            author = "karantagde",
            authorAvatar = "https://avatars.githubusercontent.com/u/1",
            branchInfo = "main ← feat/room-db",
            htmlUrl = "https://github.com/google/accompanist/pull/301",
            updatedAt = "2026-10-10",
            isDraft = false,
            labels = listOf("database", "room", "offline")
        )

        val entity = CachedGitHubItemEntity.fromDomain("google/accompanist", domain)
        assertEquals("gh_pr_301", entity.id)
        assertEquals("google/accompanist", entity.repo)
        assertEquals(301, entity.number)
        assertEquals("PULL_REQUEST", entity.type)
        assertEquals("database,room,offline", entity.labelsJoined)

        val convertedBack = entity.toDomain()
        assertEquals(domain.id, convertedBack.id)
        assertEquals(domain.title, convertedBack.title)
        assertEquals(domain.labels, convertedBack.labels)
    }

    @Test
    fun cachedJiraTicketEntity_roundTripConversion() {
        val task = TaskItem(
            id = "task_jira_892",
            title = "WebSocket sync for Jira Cloud",
            description = "Real-time updates without polling",
            projectKey = "CORE",
            projectName = "Cloud Infra",
            priority = TaskPriority.HIGH,
            status = TaskStatus.IN_PROGRESS,
            primarySource = ServiceType.JIRA,
            sourceRef = "CORE-892",
            assignee = "Elena Rostova",
            assigneeAvatar = "ER",
            updatedAt = "Just now"
        )

        val entity = CachedJiraTicketEntity.fromTaskItem(task)
        assertEquals("task_jira_892", entity.id)
        assertEquals("CORE-892", entity.ticketKey)
        assertEquals("HIGH", entity.priority)
        assertEquals("IN_PROGRESS", entity.status)

        val convertedTask = entity.toDomainTask()
        assertEquals(task.id, convertedTask.id)
        assertEquals(task.title, convertedTask.title)
        assertEquals(TaskPriority.HIGH, convertedTask.priority)
        assertEquals(TaskStatus.IN_PROGRESS, convertedTask.status)
        assertEquals(ServiceType.JIRA, convertedTask.primarySource)
    }

    @Test
    fun taskEntity_roundTripConversion_gitHubAndJira() {
        // GitHub-originated synchronized task
        val ghTask = TaskItem(
            id = "task_gh_412",
            title = "[GH #412] Multi-region failover automation",
            description = "PR branch review and webhook integration",
            projectKey = "CORE",
            projectName = "Cloud Infrastructure & Data Lake",
            priority = TaskPriority.HIGH,
            status = TaskStatus.IN_REVIEW,
            primarySource = ServiceType.GITHUB,
            sourceRef = "GH #412",
            assignee = "Sarah Chen",
            assigneeAvatar = "SC",
            syncWithSlack = true,
            syncWithGitHub = true,
            syncWithJira = true,
            syncWithN8n = true,
            updatedAt = "Just now"
        )

        val ghEntity = TaskEntity.fromTaskItem(ghTask)
        assertEquals("task_gh_412", ghEntity.id)
        assertEquals("GITHUB", ghEntity.primarySource)
        assertEquals("GH #412", ghEntity.sourceRef)
        assertEquals("IN_REVIEW", ghEntity.status)

        val ghRestored = ghEntity.toTaskItem()
        assertEquals(ghTask.id, ghRestored.id)
        assertEquals(ghTask.title, ghRestored.title)
        assertEquals(ServiceType.GITHUB, ghRestored.primarySource)
        assertEquals(TaskPriority.HIGH, ghRestored.priority)
        assertEquals(TaskStatus.IN_REVIEW, ghRestored.status)

        // Jira-originated synchronized task
        val jiraTask = TaskItem(
            id = "task_jira_905",
            title = "Jira Ticket CORE-905: Zero-trust service mesh authentication",
            description = "Enforce mutual TLS across all microservice ingress endpoints",
            projectKey = "CORE",
            projectName = "Cloud Infrastructure & Data Lake",
            priority = TaskPriority.CRITICAL,
            status = TaskStatus.IN_PROGRESS,
            primarySource = ServiceType.JIRA,
            sourceRef = "CORE-905",
            assignee = "Marcus Vance",
            assigneeAvatar = "MV",
            syncWithSlack = true,
            syncWithGitHub = true,
            syncWithJira = true,
            syncWithN8n = true,
            updatedAt = "3h ago"
        )

        val jiraEntity = TaskEntity.fromTaskItem(jiraTask)
        assertEquals("task_jira_905", jiraEntity.id)
        assertEquals("JIRA", jiraEntity.primarySource)
        assertEquals("CORE-905", jiraEntity.sourceRef)
        assertEquals("CRITICAL", jiraEntity.priority)

        val jiraRestored = jiraEntity.toTaskItem()
        assertEquals(jiraTask.id, jiraRestored.id)
        assertEquals(ServiceType.JIRA, jiraRestored.primarySource)
        assertEquals(TaskPriority.CRITICAL, jiraRestored.priority)
        assertEquals(TaskStatus.IN_PROGRESS, jiraRestored.status)
    }

    @Test
    fun syncFlowRepository_importGitHubWorkItem() {
        val testScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        val repository = SyncFlowRepository(testScope)
        val initialTaskCount = repository.tasks.value.size

        val gitHubItem = GitHubWorkItem(
            id = "gh_issue_55",
            number = 55,
            title = "Fix connection latency probe for Jira Cloud",
            type = GitHubItemType.ISSUE,
            state = "OPEN",
            author = "qa-engineer",
            authorAvatar = null,
            branchInfo = "Issue #55",
            htmlUrl = "https://github.com/google/accompanist/issues/55",
            updatedAt = "2026-10-10",
            isDraft = false,
            labels = listOf("bug", "jira")
        )

        repository.importGitHubItemAsTask(gitHubItem)

        val updatedTasks = repository.tasks.value
        assertEquals(initialTaskCount + 1, updatedTasks.size)

        val imported = updatedTasks.first()
        assertTrue(imported.title.contains("[GH #55]"))
        assertEquals(ServiceType.GITHUB, imported.primarySource)
        assertEquals("GH #55", imported.sourceRef)
        assertEquals(TaskPriority.MEDIUM, imported.priority)
    }

    @Test
    fun syncFlowRepository_seedIntegrationsContainCoreServices() {
        val testScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        val repository = SyncFlowRepository(testScope)
        val integrations = repository.integrations.value

        assertNotNull(integrations.find { it.serviceType == ServiceType.SLACK })
        assertNotNull(integrations.find { it.serviceType == ServiceType.GITHUB })
        assertNotNull(integrations.find { it.serviceType == ServiceType.JIRA })
        assertNotNull(integrations.find { it.serviceType == ServiceType.N8N })
    }
}
