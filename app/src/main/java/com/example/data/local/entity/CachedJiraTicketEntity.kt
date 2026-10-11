package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ServiceType
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus

@Entity(tableName = "cached_jira_tickets")
data class CachedJiraTicketEntity(
    @PrimaryKey val id: String, // e.g. "CORE-892" or "task_01"
    val ticketKey: String, // e.g. "CORE-892"
    val summary: String,
    val description: String,
    val projectKey: String,
    val projectName: String,
    val priority: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val status: String, // "BACKLOG", "IN_PROGRESS", "IN_REVIEW", "DONE"
    val assigneeName: String,
    val assigneeAvatar: String,
    val reporter: String = "Automated Sync",
    val syncWithSlack: Boolean = true,
    val syncWithGitHub: Boolean = true,
    val syncWithN8n: Boolean = true,
    val updatedAt: String,
    val cachedAtTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomainTask(): TaskItem {
        val mappedPriority = try {
            TaskPriority.valueOf(priority)
        } catch (_: Exception) {
            TaskPriority.MEDIUM
        }
        val mappedStatus = try {
            TaskStatus.valueOf(status)
        } catch (_: Exception) {
            TaskStatus.IN_PROGRESS
        }

        return TaskItem(
            id = id,
            title = summary,
            description = description,
            projectKey = projectKey,
            projectName = projectName,
            priority = mappedPriority,
            status = mappedStatus,
            primarySource = ServiceType.JIRA,
            sourceRef = ticketKey,
            assignee = assigneeName,
            assigneeAvatar = assigneeAvatar,
            syncWithSlack = syncWithSlack,
            syncWithGitHub = syncWithGitHub,
            syncWithJira = true,
            syncWithN8n = syncWithN8n,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromTaskItem(task: TaskItem): CachedJiraTicketEntity {
            return CachedJiraTicketEntity(
                id = task.id,
                ticketKey = if (task.sourceRef.isNotBlank()) task.sourceRef else "JIRA-${task.id.takeLast(4)}",
                summary = task.title,
                description = task.description,
                projectKey = task.projectKey,
                projectName = task.projectName,
                priority = task.priority.name,
                status = task.status.name,
                assigneeName = task.assignee,
                assigneeAvatar = task.assigneeAvatar,
                syncWithSlack = task.syncWithSlack,
                syncWithGitHub = task.syncWithGitHub,
                syncWithN8n = task.syncWithN8n,
                updatedAt = task.updatedAt,
                cachedAtTimestamp = System.currentTimeMillis()
            )
        }
    }
}
