package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ServiceType
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus

/**
 * Room database entity for locally persisting project synchronization data
 * from Jira, GitHub, Slack, and automation workflows.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val projectKey: String,
    val projectName: String,
    val priority: String,
    val status: String,
    val primarySource: String,
    val sourceRef: String,
    val assignee: String,
    val assigneeAvatar: String,
    val syncWithSlack: Boolean = true,
    val syncWithGitHub: Boolean = true,
    val syncWithJira: Boolean = true,
    val syncWithN8n: Boolean = true,
    val updatedAt: String,
    val syncedAtTimestamp: Long = System.currentTimeMillis()
) {
    fun toTaskItem(): TaskItem {
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

        val mappedSource = try {
            ServiceType.valueOf(primarySource)
        } catch (_: Exception) {
            ServiceType.GITHUB
        }

        return TaskItem(
            id = id,
            title = title,
            description = description,
            projectKey = projectKey,
            projectName = projectName,
            priority = mappedPriority,
            status = mappedStatus,
            primarySource = mappedSource,
            sourceRef = sourceRef,
            assignee = assignee,
            assigneeAvatar = assigneeAvatar,
            syncWithSlack = syncWithSlack,
            syncWithGitHub = syncWithGitHub,
            syncWithJira = syncWithJira,
            syncWithN8n = syncWithN8n,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromTaskItem(task: TaskItem): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                projectKey = task.projectKey,
                projectName = task.projectName,
                priority = task.priority.name,
                status = task.status.name,
                primarySource = task.primarySource.name,
                sourceRef = task.sourceRef,
                assignee = task.assignee,
                assigneeAvatar = task.assigneeAvatar,
                syncWithSlack = task.syncWithSlack,
                syncWithGitHub = task.syncWithGitHub,
                syncWithJira = task.syncWithJira,
                syncWithN8n = task.syncWithN8n,
                updatedAt = task.updatedAt,
                syncedAtTimestamp = System.currentTimeMillis()
            )
        }
    }
}
