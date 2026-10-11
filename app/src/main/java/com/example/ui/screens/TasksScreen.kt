package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceType
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.TeamProject
import com.example.ui.components.ServiceBadge
import com.example.ui.components.TaskPriorityBadge
import com.example.ui.components.TaskStatusBadge
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GitHubBrand
import com.example.ui.theme.JiraBrandLight
import com.example.ui.theme.N8nBrand
import com.example.ui.theme.SlackBrand

@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    projects: List<TeamProject>,
    selectedServiceFilter: ServiceType?,
    selectedProjectFilter: String?,
    onSelectServiceFilter: (ServiceType?) -> Unit,
    onSelectProjectFilter: (String?) -> Unit,
    onUpdateTaskStatus: (taskId: String, newStatus: TaskStatus) -> Unit,
    onAddTask: (title: String, description: String, projectKey: String, priority: TaskPriority, primarySource: ServiceType, sourceRef: String, assignee: String, syncSlack: Boolean, syncGitHub: Boolean, syncJira: Boolean) -> Unit,
    onDeleteTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ElectricIndigo,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("tasks_screen")
        ) {
            // Filters Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Synchronized Team Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Live changes propagate in real time to Jira Cloud, GitHub PRs, Slack & n8n.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Service Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedServiceFilter == null,
                        onClick = { onSelectServiceFilter(null) },
                        label = { Text("All Services (${tasks.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedServiceFilter == ServiceType.JIRA,
                        onClick = { onSelectServiceFilter(if (selectedServiceFilter == ServiceType.JIRA) null else ServiceType.JIRA) },
                        label = { Text("Jira", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedServiceFilter == ServiceType.GITHUB,
                        onClick = { onSelectServiceFilter(if (selectedServiceFilter == ServiceType.GITHUB) null else ServiceType.GITHUB) },
                        label = { Text("GitHub", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedServiceFilter == ServiceType.SLACK,
                        onClick = { onSelectServiceFilter(if (selectedServiceFilter == ServiceType.SLACK) null else ServiceType.SLACK) },
                        label = { Text("Slack", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Project Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedProjectFilter == null,
                        onClick = { onSelectProjectFilter(null) },
                        label = { Text("All Projects", fontSize = 11.sp) }
                    )
                    projects.forEach { p ->
                        FilterChip(
                            selected = selectedProjectFilter == p.key,
                            onClick = { onSelectProjectFilter(if (selectedProjectFilter == p.key) null else p.key) },
                            label = { Text(p.key, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Task List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCardItem(
                        task = task,
                        onUpdateStatus = { status -> onUpdateTaskStatus(task.id, status) },
                        onDelete = { onDeleteTask(task.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTaskDialog(
            projects = projects,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, projKey, priority, source, ref, assignee, sSlack, sGH, sJira ->
                onAddTask(title, desc, projKey, priority, source, ref, assignee, sSlack, sGH, sJira)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun TaskCardItem(
    task: TaskItem,
    onUpdateStatus: (TaskStatus) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top row: Project Key, Source Ref, Priority, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ElectricIndigo.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = task.projectKey,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricIndigo,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = when (task.primarySource) {
                            ServiceType.JIRA -> JiraBrandLight.copy(alpha = 0.15f)
                            ServiceType.GITHUB -> GitHubBrand.copy(alpha = 0.15f)
                            ServiceType.SLACK -> SlackBrand.copy(alpha = 0.15f)
                            else -> ElectricCyan.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = task.sourceRef,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (task.primarySource) {
                                ServiceType.JIRA -> JiraBrandLight
                                ServiceType.GITHUB -> GitHubBrand
                                ServiceType.SLACK -> SlackBrand
                                else -> ElectricCyan
                            },
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TaskPriorityBadge(priority = task.priority)
                    Spacer(modifier = Modifier.width(6.dp))
                    TaskStatusBadge(status = task.status)
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Move to Backlog") },
                                onClick = {
                                    onUpdateStatus(TaskStatus.BACKLOG)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Move to In Progress") },
                                onClick = {
                                    onUpdateStatus(TaskStatus.IN_PROGRESS)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Move to In Review") },
                                onClick = {
                                    onUpdateStatus(TaskStatus.IN_REVIEW)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mark Completed") },
                                onClick = {
                                    onUpdateStatus(TaskStatus.DONE)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Task", color = Color(0xFFF43F5E)) },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Assignee avatar & Multi-Service Sync Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Assignee
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ElectricIndigo.copy(alpha = 0.3f),
                        shape = CircleShape,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = task.assigneeAvatar,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.assignee,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Cross-Platform Sync Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Sync:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SyncChipMini(name = "Slack", active = task.syncWithSlack, activeColor = SlackBrand)
                    SyncChipMini(name = "GH", active = task.syncWithGitHub, activeColor = GitHubBrand)
                    SyncChipMini(name = "Jira", active = task.syncWithJira, activeColor = JiraBrandLight)
                    SyncChipMini(name = "n8n", active = task.syncWithN8n, activeColor = N8nBrand)
                }
            }

            // Quick Status Advance Buttons
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (task.status != TaskStatus.IN_PROGRESS && task.status != TaskStatus.DONE) {
                    OutlinedButton(
                        onClick = { onUpdateStatus(TaskStatus.IN_PROGRESS) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("➔ In Progress", fontSize = 10.sp, maxLines = 1)
                    }
                }
                if (task.status != TaskStatus.IN_REVIEW && task.status != TaskStatus.DONE) {
                    OutlinedButton(
                        onClick = { onUpdateStatus(TaskStatus.IN_REVIEW) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("➔ Review", fontSize = 10.sp, maxLines = 1)
                    }
                }
                if (task.status != TaskStatus.DONE) {
                    Button(
                        onClick = { onUpdateStatus(TaskStatus.DONE) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("✔ Done", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun SyncChipMini(name: String, active: Boolean, activeColor: Color) {
    Surface(
        color = if (active) activeColor.copy(alpha = 0.2f) else Color(0xFF1E293B),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) activeColor else Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}

@Composable
fun CreateTaskDialog(
    projects: List<TeamProject>,
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String, projectKey: String, priority: TaskPriority, primarySource: ServiceType, sourceRef: String, assignee: String, syncSlack: Boolean, syncGitHub: Boolean, syncJira: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var projectKey by remember { mutableStateOf(projects.firstOrNull()?.key ?: "CORE") }
    var priority by remember { mutableStateOf(TaskPriority.HIGH) }
    var primarySource by remember { mutableStateOf(ServiceType.JIRA) }
    var sourceRef by remember { mutableStateOf("CORE-") }
    var assignee by remember { mutableStateOf("Elena Rostova") }
    var syncSlack by remember { mutableStateOf(true) }
    var syncGitHub by remember { mutableStateOf(true) }
    var syncJira by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Multi-Cloud Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Project:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    projects.forEach { p ->
                        FilterChip(
                            selected = projectKey == p.key,
                            onClick = { projectKey = p.key },
                            label = { Text(p.key, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sourceRef,
                        onValueChange = { sourceRef = it },
                        label = { Text("Ref (Ticket/PR)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = assignee,
                        onValueChange = { assignee = it },
                        label = { Text("Assignee") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Synchronize to:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = syncSlack,
                        onClick = { syncSlack = !syncSlack },
                        label = { Text("Slack", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = syncGitHub,
                        onClick = { syncGitHub = !syncGitHub },
                        label = { Text("GitHub", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = syncJira,
                        onClick = { syncJira = !syncJira },
                        label = { Text("Jira", fontSize = 11.sp) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, description, projectKey, priority, primarySource, sourceRef, assignee, syncSlack, syncGitHub, syncJira)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create & Sync")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
