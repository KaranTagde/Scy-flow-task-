package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthUiState
import com.example.data.auth.UserProfile
import com.example.data.model.CloudIntegration
import com.example.data.model.ServiceType
import com.example.data.model.SyncEventLog
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.TeamProject
import com.example.data.model.ThinkingAnalysisResult
import com.example.data.model.WorkflowRule
import com.example.data.repository.SyncFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val iconKey: String) {
    DASHBOARD("Overview", "dashboard"),
    WORKFLOWS("Automations", "workflow"),
    TASKS("Team Tasks", "task"),
    INTEGRATIONS("Cloud Sync", "cloud"),
    AI_ORCHESTRATOR("AI Reasoning", "psychology")
}

class SyncFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SyncFlowRepository(viewModelScope, application.applicationContext)
    private val authManager = AuthManager(application.applicationContext)

    val currentUser: StateFlow<UserProfile?> = authManager.currentUser
    val authUiState: StateFlow<AuthUiState> = authManager.authUiState

    val integrations: StateFlow<List<CloudIntegration>> = repository.integrations
    val workflows: StateFlow<List<WorkflowRule>> = repository.workflows
    val eventLogs: StateFlow<List<SyncEventLog>> = repository.eventLogs
    val teamProjects: StateFlow<List<TeamProject>> = repository.teamProjects
    val aiResult: StateFlow<ThinkingAnalysisResult?> = repository.aiResult
    val isGlobalSyncing: StateFlow<Boolean> = repository.isGlobalSyncing

    // Room Database Offline Cache States
    val cachedGitHubCount: StateFlow<Int> = repository.cachedGitHubCount
    val cachedJiraCount: StateFlow<Int> = repository.cachedJiraCount
    val cachedTaskCount: StateFlow<Int> = repository.cachedTaskCount
    val isOfflineModeActive: StateFlow<Boolean> = repository.isOfflineModeActive

    // GitHub Retrofit States
    val gitHubItems: StateFlow<List<com.example.data.network.github.GitHubWorkItem>> = repository.gitHubItems
    val selectedRepo: StateFlow<String> = repository.selectedRepo
    val isGitHubLoading: StateFlow<Boolean> = repository.isGitHubLoading
    val gitHubError: StateFlow<String?> = repository.gitHubError

    fun refreshGitHubRepo(repoString: String? = null) {
        val repo = repoString ?: selectedRepo.value
        repository.fetchGitHubRepositoryData(repo)
    }

    fun importGitHubWorkItem(item: com.example.data.network.github.GitHubWorkItem) {
        repository.importGitHubItemAsTask(item)
    }

    // UI state
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true) // Dark mode default as requested
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedServiceFilter = MutableStateFlow<ServiceType?>(null)
    val selectedServiceFilter: StateFlow<ServiceType?> = _selectedServiceFilter.asStateFlow()

    private val _selectedProjectFilter = MutableStateFlow<String?>(null)
    val selectedProjectFilter: StateFlow<String?> = _selectedProjectFilter.asStateFlow()

    // Filtered tasks flow
    val filteredTasks: StateFlow<List<TaskItem>> = combine(
        repository.tasks,
        _selectedServiceFilter,
        _selectedProjectFilter
    ) { tasks, serviceFilter, projFilter ->
        tasks.filter { task ->
            (serviceFilter == null || task.primarySource == serviceFilter) &&
            (projFilter == null || task.projectKey == projFilter)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Authentication Actions ---

    fun signInWithGoogle() {
        viewModelScope.launch {
            authManager.signInWithGoogle()
        }
    }

    fun signInAsDevAdmin() {
        authManager.signInAsDevAdmin()
    }

    fun signOut() {
        authManager.signOut()
    }

    // --- Navigation & Theming ---

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setServiceFilter(service: ServiceType?) {
        _selectedServiceFilter.value = service
    }

    fun setProjectFilter(projectKey: String?) {
        _selectedProjectFilter.value = projectKey
    }

    // --- Repository Actions ---

    fun syncAllServices() {
        repository.syncAllServices()
    }

    fun toggleAutoSync(service: ServiceType) {
        repository.toggleAutoSync(service)
    }

    fun setSyncInterval(service: ServiceType, seconds: Int) {
        repository.setSyncInterval(service, seconds)
    }

    fun triggerSimulatedEvent(service: ServiceType) {
        repository.triggerSimulatedEvent(service)
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        repository.updateTaskStatus(taskId, newStatus)
    }

    fun addTask(
        title: String,
        description: String,
        projectKey: String,
        priority: TaskPriority,
        primarySource: ServiceType,
        sourceRef: String,
        assignee: String,
        syncSlack: Boolean,
        syncGitHub: Boolean,
        syncJira: Boolean
    ) {
        repository.addTask(
            title = title,
            description = description,
            projectKey = projectKey,
            priority = priority,
            primarySource = primarySource,
            sourceRef = sourceRef,
            assignee = assignee,
            syncSlack = syncSlack,
            syncGitHub = syncGitHub,
            syncJira = syncJira
        )
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }

    fun toggleWorkflow(workflowId: String) {
        repository.toggleWorkflow(workflowId)
    }

    fun triggerWorkflow(workflowId: String) {
        repository.triggerWorkflowById(workflowId)
    }

    fun addWorkflow(
        name: String,
        triggerService: ServiceType,
        triggerDescription: String,
        targetServices: List<ServiceType>,
        actionDescription: String
    ) {
        repository.addWorkflow(
            name = name,
            triggerService = triggerService,
            triggerDescription = triggerDescription,
            targetServices = targetServices,
            actionDescription = actionDescription
        )
    }

    fun runHighThinkingQuery(query: String) {
        repository.runHighThinkingAnalysis(query)
    }

    fun clearLogs() {
        repository.clearLogs()
    }
}
