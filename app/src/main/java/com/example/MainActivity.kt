package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.auth.UserProfile
import com.example.ui.components.SyncFlowBottomNavigation
import com.example.ui.components.SyncFlowNavigationRail
import com.example.ui.components.SyncFlowTopBar
import com.example.ui.screens.AiOrchestratorScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IntegrationsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WorkflowsScreen
import com.example.ui.theme.SyncFlowTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SyncFlowViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SyncFlowViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()

            SyncFlowTheme(darkTheme = isDarkTheme) {
                if (currentUser == null) {
                    LoginScreen(
                        authState = authUiState,
                        onSignInWithGoogle = { viewModel.signInWithGoogle() },
                        onSignInAsDevAdmin = { viewModel.signInAsDevAdmin() }
                    )
                } else {
                    SyncFlowApp(
                        viewModel = viewModel,
                        currentUser = currentUser!!
                    )
                }
            }
        }
    }
}

@Composable
fun SyncFlowApp(
    viewModel: SyncFlowViewModel,
    currentUser: UserProfile
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isGlobalSyncing by viewModel.isGlobalSyncing.collectAsStateWithLifecycle()
    val integrations by viewModel.integrations.collectAsStateWithLifecycle()
    val workflows by viewModel.workflows.collectAsStateWithLifecycle()
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val projects by viewModel.teamProjects.collectAsStateWithLifecycle()
    val eventLogs by viewModel.eventLogs.collectAsStateWithLifecycle()
    val aiResult by viewModel.aiResult.collectAsStateWithLifecycle()
    val selectedServiceFilter by viewModel.selectedServiceFilter.collectAsStateWithLifecycle()
    val selectedProjectFilter by viewModel.selectedProjectFilter.collectAsStateWithLifecycle()
    val gitHubItems by viewModel.gitHubItems.collectAsStateWithLifecycle()
    val selectedRepo by viewModel.selectedRepo.collectAsStateWithLifecycle()
    val isGitHubLoading by viewModel.isGitHubLoading.collectAsStateWithLifecycle()
    val gitHubError by viewModel.gitHubError.collectAsStateWithLifecycle()
    val cachedGitHubCount by viewModel.cachedGitHubCount.collectAsStateWithLifecycle()
    val cachedJiraCount by viewModel.cachedJiraCount.collectAsStateWithLifecycle()
    val cachedTaskCount by viewModel.cachedTaskCount.collectAsStateWithLifecycle()
    val isOfflineModeActive by viewModel.isOfflineModeActive.collectAsStateWithLifecycle()

    // Handle back button to return to Dashboard if on sub-screen
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SyncFlowTopBar(
                    isSyncing = isGlobalSyncing,
                    isDarkTheme = isDarkTheme,
                    userProfile = currentUser,
                    onSyncAll = { viewModel.syncAllServices() },
                    onToggleDark = { viewModel.toggleDarkMode() },
                    onSignOut = { viewModel.signOut() }
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    SyncFlowBottomNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    SyncFlowNavigationRail(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }

                when (currentScreen) {
                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            integrations = integrations,
                            projects = projects,
                            eventLogs = eventLogs,
                            activeWorkflowsCount = workflows.count { it.isEnabled },
                            syncedTasksCount = tasks.size,
                            gitHubItems = gitHubItems,
                            selectedRepo = selectedRepo,
                            isGitHubLoading = isGitHubLoading,
                            gitHubError = gitHubError,
                            cachedGitHubCount = cachedGitHubCount,
                            cachedJiraCount = cachedJiraCount,
                            cachedTaskCount = cachedTaskCount,
                            isOfflineMode = isOfflineModeActive,
                            onRefreshGitHub = { viewModel.refreshGitHubRepo(it) },
                            onImportGitHubItem = { viewModel.importGitHubWorkItem(it) },
                            onNavigate = { viewModel.navigateTo(it) },
                            onTriggerEvent = { viewModel.triggerSimulatedEvent(it) }
                        )
                    }
                    AppScreen.WORKFLOWS -> {
                        WorkflowsScreen(
                            workflows = workflows,
                            onToggleWorkflow = { viewModel.toggleWorkflow(it) },
                            onTriggerWorkflow = { viewModel.triggerWorkflow(it) },
                            onAddWorkflow = { name, triggerService, triggerDesc, targetServices, actionDesc ->
                                viewModel.addWorkflow(name, triggerService, triggerDesc, targetServices, actionDesc)
                            }
                        )
                    }
                    AppScreen.TASKS -> {
                        TasksScreen(
                            tasks = tasks,
                            projects = projects,
                            selectedServiceFilter = selectedServiceFilter,
                            selectedProjectFilter = selectedProjectFilter,
                            onSelectServiceFilter = { viewModel.setServiceFilter(it) },
                            onSelectProjectFilter = { viewModel.setProjectFilter(it) },
                            onUpdateTaskStatus = { taskId, status -> viewModel.updateTaskStatus(taskId, status) },
                            onAddTask = { title, desc, projKey, priority, source, ref, assignee, sSlack, sGH, sJira ->
                                viewModel.addTask(title, desc, projKey, priority, source, ref, assignee, sSlack, sGH, sJira)
                            },
                            onDeleteTask = { viewModel.deleteTask(it) }
                        )
                    }
                    AppScreen.INTEGRATIONS -> {
                        IntegrationsScreen(
                            integrations = integrations,
                            onToggleAutoSync = { viewModel.toggleAutoSync(it) },
                            onSetSyncInterval = { s, sec -> viewModel.setSyncInterval(s, sec) },
                            onTriggerEvent = { viewModel.triggerSimulatedEvent(it) },
                            onSyncAll = { viewModel.syncAllServices() }
                        )
                    }
                    AppScreen.AI_ORCHESTRATOR -> {
                        AiOrchestratorScreen(
                            aiResult = aiResult,
                            onRunQuery = { viewModel.runHighThinkingQuery(it) },
                            onDeploySuggestedWorkflow = { suggestedRule ->
                                viewModel.addWorkflow(
                                    name = suggestedRule.name,
                                    triggerService = suggestedRule.triggerService,
                                    triggerDescription = suggestedRule.triggerDescription,
                                    targetServices = suggestedRule.targetServices,
                                    actionDescription = suggestedRule.actionDescription
                                )
                                viewModel.navigateTo(AppScreen.WORKFLOWS)
                            }
                        )
                    }
                }
            }
        }
    }
}
