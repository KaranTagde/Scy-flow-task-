package com.example.data.model

enum class ServiceType(val displayName: String, val iconKey: String) {
    SLACK("Slack", "slack"),
    GITHUB("GitHub", "github"),
    JIRA("Jira", "jira"),
    N8N("n8n Workflow", "n8n"),
    WORKFLOW("Workflow Engine", "workflow"),
    GEMINI("Gemini AI", "gemini")
}

enum class ConnectionStatus {
    CONNECTED,
    SYNCING,
    ERROR,
    DISCONNECTED
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class TaskStatus(val displayName: String) {
    BACKLOG("Backlog"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    DONE("Completed")
}

enum class N8nNodeType(val displayName: String, val category: String) {
    WEBHOOK_TRIGGER("Webhook Trigger", "Trigger"),
    GITHUB_TRIGGER("GitHub Event Trigger", "Trigger"),
    JIRA_TRIGGER("Jira Event Trigger", "Trigger"),
    SLACK_TRIGGER("Slack Command Trigger", "Trigger"),
    CRON_TRIGGER("Schedule / Polling Trigger", "Trigger"),
    IF_CONDITION("IF Filter Condition", "Logic"),
    CODE_TRANSFORM("Data Transformer", "Transform"),
    GEMINI_THINKING("Gemini High-Thinking AI", "Intelligence"),
    SLACK_ACTION("Slack Post Message", "Action"),
    JIRA_ACTION("Jira Transition / Update", "Action"),
    GITHUB_ACTION("GitHub Dispatch / Merge", "Action"),
    HTTP_REQUEST("HTTP Cloud Webhook", "Action")
}

data class N8nWorkflowNode(
    val id: String,
    val name: String,
    val type: N8nNodeType,
    val service: ServiceType,
    val parametersSummary: String,
    val executionStatus: String = "SUCCESS" // "SUCCESS", "RUNNING", "IDLE", "FAILED"
)

data class CloudIntegration(
    val serviceType: ServiceType,
    val name: String,
    val targetWorkspaceOrRepo: String,
    val channelOrBoard: String,
    val status: ConnectionStatus,
    val autoSyncEnabled: Boolean,
    val syncIntervalSeconds: Int, // 5, 15, 30, 60
    val lastSyncTime: String,
    val latencyMs: Long,
    val eventsSyncedCount: Int,
    val webhookActive: Boolean,
    val apiEndpoint: String
)

data class TaskItem(
    val id: String,
    val title: String,
    val description: String,
    val projectKey: String,
    val projectName: String,
    val priority: TaskPriority,
    val status: TaskStatus,
    val primarySource: ServiceType,
    val sourceRef: String, // e.g. "PR #142", "CORE-481", "#eng-lead"
    val assignee: String,
    val assigneeAvatar: String,
    val syncWithSlack: Boolean = true,
    val syncWithGitHub: Boolean = true,
    val syncWithJira: Boolean = true,
    val syncWithN8n: Boolean = true,
    val updatedAt: String
)

data class WorkflowRule(
    val id: String,
    val name: String,
    val triggerService: ServiceType,
    val triggerDescription: String,
    val targetServices: List<ServiceType>,
    val actionDescription: String,
    val isEnabled: Boolean,
    val executionCount: Int,
    val successRatePercent: Float,
    val averageLatencyMs: Long,
    val lastRunTime: String,
    val n8nNodes: List<N8nWorkflowNode> = emptyList(),
    val isN8nModel: Boolean = true
)

enum class LogLevel {
    SUCCESS,
    INFO,
    WARNING,
    ERROR
}

data class SyncEventLog(
    val id: String,
    val timestamp: String,
    val service: ServiceType,
    val title: String,
    val detail: String,
    val level: LogLevel,
    val sourceRef: String? = null
)

data class TeamProject(
    val id: String,
    val key: String,
    val name: String,
    val lead: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val activeSprint: String,
    val sprintProgressPercent: Float,
    val cloudHealth: String // "All Synced", "Syncing...", "1 Disconnected"
)

data class ThinkingAnalysisResult(
    val query: String,
    val model: String = "gemini-3.1-pro-preview",
    val thinkingLevel: String = "HIGH",
    val reasoningChain: List<String> = emptyList(),
    val solutionText: String = "",
    val suggestedWorkflow: WorkflowRule? = null,
    val executionDurationMs: Long = 0,
    val isThinking: Boolean = false,
    val error: String? = null
)
