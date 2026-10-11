package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncFlowTopBar(
    isSyncing: Boolean,
    isDarkTheme: Boolean,
    userProfile: com.example.data.auth.UserProfile? = null,
    onSyncAll: () -> Unit,
    onToggleDark: () -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showProfileDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    TopAppBar(
        modifier = modifier.testTag("syncflow_topbar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Brand emblem badge
                Surface(
                    color = ElectricIndigo.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "SyncFlow Logo",
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SyncFlow",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    LivePulseIndicator(isSyncing = isSyncing)
                }
            }
        },
        actions = {
            // Quick Sync All button
            FilledTonalButton(
                onClick = onSyncAll,
                enabled = !isSyncing,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("sync_all_button")
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Syncing...", fontSize = 12.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync All",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sync All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Dark Mode toggle
            IconButton(
                onClick = onToggleDark,
                modifier = Modifier.testTag("toggle_dark_theme")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // User Profile Avatar button
            if (userProfile != null) {
                IconButton(
                    onClick = { showProfileDialog = true },
                    modifier = Modifier.testTag("user_profile_button")
                ) {
                    Surface(
                        color = ElectricIndigo.copy(alpha = 0.35f),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.5.dp, ElectricCyan, CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userProfile.displayName.split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .joinToString("")
                                    .take(2)
                                    .uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }
                }
            }
        }
    )

    if (showProfileDialog && userProfile != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = "Authenticated Workspace Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = ElectricIndigo.copy(alpha = 0.3f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userProfile.displayName.split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .joinToString("")
                                        .take(2)
                                        .uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = userProfile.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = userProfile.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Role: ${userProfile.workspaceRole}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Org: ${userProfile.workspaceOrg}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SSO: Google Workspace Authenticated",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showProfileDialog = false
                        onSignOut()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFF43F5E)
                    ),
                    modifier = Modifier.testTag("sign_out_button")
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SyncFlowBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("syncflow_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val screens = listOf(
            AppScreen.DASHBOARD to Icons.Default.Dashboard,
            AppScreen.WORKFLOWS to Icons.Default.Bolt,
            AppScreen.TASKS to Icons.Default.Checklist,
            AppScreen.INTEGRATIONS to Icons.Default.CloudSync,
            AppScreen.AI_ORCHESTRATOR to Icons.Default.Psychology
        )

        screens.forEach { (screen, icon) ->
            val selected = screen == currentScreen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricCyan,
                    selectedTextColor = ElectricCyan,
                    indicatorColor = ElectricIndigo.copy(alpha = 0.25f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}

@Composable
fun SyncFlowNavigationRail(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier.testTag("syncflow_nav_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Surface(
                color = ElectricIndigo.copy(alpha = 0.25f),
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .padding(4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "SyncFlow",
                        tint = ElectricCyan
                    )
                }
            }
        }
    ) {
        val screens = listOf(
            AppScreen.DASHBOARD to Icons.Default.Dashboard,
            AppScreen.WORKFLOWS to Icons.Default.Bolt,
            AppScreen.TASKS to Icons.Default.Checklist,
            AppScreen.INTEGRATIONS to Icons.Default.CloudSync,
            AppScreen.AI_ORCHESTRATOR to Icons.Default.Psychology
        )

        screens.forEach { (screen, icon) ->
            NavigationRailItem(
                selected = screen == currentScreen,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ElectricCyan,
                    selectedTextColor = ElectricCyan,
                    indicatorColor = ElectricIndigo.copy(alpha = 0.25f)
                ),
                modifier = Modifier.testTag("rail_item_${screen.name.lowercase()}")
            )
        }
    }
}
