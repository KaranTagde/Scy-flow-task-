package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.N8nNodeType
import com.example.data.model.N8nWorkflowNode
import com.example.data.model.ServiceType
import com.example.data.model.WorkflowRule
import com.example.ui.components.ServiceBadge
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.N8nBrand
import com.example.ui.theme.PurpleAI
import com.example.ui.theme.RoseRed

@Composable
fun WorkflowsScreen(
    workflows: List<WorkflowRule>,
    onToggleWorkflow: (String) -> Unit,
    onTriggerWorkflow: (String) -> Unit,
    onAddWorkflow: (name: String, triggerService: ServiceType, triggerDescription: String, targetServices: List<ServiceType>, actionDescription: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: n8n Node Pipelines, 2: High Thinking AI

    val filteredList = when (selectedTab) {
        1 -> workflows.filter { it.isN8nModel }
        2 -> workflows.filter { it.targetServices.contains(ServiceType.GEMINI) || it.triggerService == ServiceType.GEMINI }
        else -> workflows
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("workflows_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card with n8n Model Architecture badge
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = N8nBrand.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "n8n Workflow Model",
                                        color = N8nBrand,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Node Pipelines",
                                    fontSize = 12.sp,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Automated Team Workflows",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("new_workflow_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Flow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Orchestrates real-time events across Slack channels, GitHub pull requests, and Jira sprints using asynchronous n8n node execution graph.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("All Automations (${workflows.size})", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("n8n Pipelines", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("AI Reasoning Nodes", fontSize = 12.sp) }
                )
            }
        }

        // Workflows list
        items(filteredList) { workflow ->
            WorkflowCard(
                workflow = workflow,
                onToggle = { onToggleWorkflow(workflow.id) },
                onTrigger = { onTriggerWorkflow(workflow.id) }
            )
        }
    }

    if (showCreateDialog) {
        CreateWorkflowDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, triggerService, triggerDesc, targetServices, actionDesc ->
                onAddWorkflow(name, triggerService, triggerDesc, targetServices, actionDesc)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun WorkflowCard(
    workflow: WorkflowRule,
    onToggle: () -> Unit,
    onTrigger: () -> Unit
) {
    var expandedNodes by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workflow_item_${workflow.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and enable switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = if (workflow.isEnabled) ElectricIndigo.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Workflow",
                                tint = if (workflow.isEnabled) ElectricCyan else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = workflow.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (workflow.isN8nModel) {
                                Surface(
                                    color = N8nBrand.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "n8n Flow",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = N8nBrand,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = "Last run: ${workflow.lastRunTime}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Switch(
                    checked = workflow.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ElectricIndigo,
                        uncheckedTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.testTag("switch_${workflow.id}")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Pipeline Diagram (n8n Node representation)
            if (workflow.n8nNodes.isNotEmpty()) {
                Text(
                    text = "n8n Execution Nodes Pipeline:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    workflow.n8nNodes.forEachIndexed { index, node ->
                        N8nNodeCard(node = node)
                        if (index < workflow.n8nNodes.size - 1) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Node",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(14.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                // Classic Trigger -> Action representation
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ServiceBadge(service = workflow.triggerService)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = workflow.triggerDescription,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "➔ Dispatches to:",
                                fontSize = 11.sp,
                                color = ElectricCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            workflow.targetServices.forEach { s ->
                                ServiceBadge(service = s, modifier = Modifier.padding(end = 4.dp))
                            }
                        }
                        Text(
                            text = workflow.actionDescription,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Stats & Run Action footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${workflow.executionCount} runs",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${workflow.successRatePercent}% ok",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "${workflow.averageLatencyMs}ms latency",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onTrigger,
                    enabled = workflow.isEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan.copy(alpha = 0.2f),
                        contentColor = ElectricCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("test_run_${workflow.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Run",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun N8nNodeCard(node: N8nWorkflowNode) {
    val nodeBorderColor = when (node.service) {
        ServiceType.SLACK -> Color(0xFFFF7A9E)
        ServiceType.GITHUB -> Color(0xFF56D364)
        ServiceType.JIRA -> Color(0xFF6B9CFF)
        ServiceType.N8N -> N8nBrand
        ServiceType.GEMINI -> PurpleAI
        else -> ElectricIndigo
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .border(1.dp, nodeBorderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .width(170.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.type.category.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = nodeBorderColor
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = node.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = node.parametersSummary,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun CreateWorkflowDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, triggerService: ServiceType, triggerDescription: String, targetServices: List<ServiceType>, actionDescription: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var triggerService by remember { mutableStateOf(ServiceType.GITHUB) }
    var triggerDesc by remember { mutableStateOf("") }
    var actionDesc by remember { mutableStateOf("") }
    var syncSlack by remember { mutableStateOf(true) }
    var syncGitHub by remember { mutableStateOf(false) }
    var syncJira by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create n8n Automation Rule", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Workflow Name") },
                    placeholder = { Text("e.g. Sync Hotfix to Jira & Slack") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Trigger Service (Node 1):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(ServiceType.GITHUB, ServiceType.SLACK, ServiceType.JIRA, ServiceType.N8N).forEach { s ->
                        FilterChip(
                            selected = triggerService == s,
                            onClick = { triggerService = s },
                            label = { Text(s.displayName, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = triggerDesc,
                    onValueChange = { triggerDesc = it },
                    label = { Text("Trigger Event Criteria") },
                    placeholder = { Text("e.g. PR merged or Jira issue closed") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Target Action Services:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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

                OutlinedTextField(
                    value = actionDesc,
                    onValueChange = { actionDesc = it },
                    label = { Text("Actions to Execute") },
                    placeholder = { Text("e.g. Broadcast message & transition ticket") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && triggerDesc.isNotBlank() && actionDesc.isNotBlank()) {
                        val targets = mutableListOf<ServiceType>()
                        if (syncSlack) targets.add(ServiceType.SLACK)
                        if (syncGitHub) targets.add(ServiceType.GITHUB)
                        if (syncJira) targets.add(ServiceType.JIRA)
                        onCreate(name, triggerService, triggerDesc, targets.ifEmpty { listOf(ServiceType.SLACK) }, actionDesc)
                    }
                },
                enabled = name.isNotBlank() && triggerDesc.isNotBlank() && actionDesc.isNotBlank()
            ) {
                Text("Register Workflow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
