package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.ServiceType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GitHubBrand
import com.example.ui.theme.JiraBrandLight
import com.example.ui.theme.PurpleAI
import com.example.ui.theme.RoseRed
import com.example.ui.theme.SlackBrand

@Composable
fun ServiceBadge(
    service: ServiceType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (service) {
        ServiceType.SLACK -> Triple(Color(0xFF381427), Color(0xFFFF7A9E), Icons.Default.Hub)
        ServiceType.GITHUB -> Triple(Color(0xFF132D21), Color(0xFF56D364), Icons.Default.Grain)
        ServiceType.JIRA -> Triple(Color(0xFF14244D), Color(0xFF6B9CFF), Icons.Default.AutoMode)
        ServiceType.N8N -> Triple(Color(0xFF3D1620), Color(0xFFFF6D5A), Icons.Default.Bolt)
        ServiceType.WORKFLOW -> Triple(Color(0xFF2B2050), Color(0xFFA78BFA), Icons.Default.Bolt)
        ServiceType.GEMINI -> Triple(Color(0xFF351A52), Color(0xFFC084FC), Icons.Default.Psychology)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = service.displayName,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = service.displayName,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ConnectionStatusChip(
    status: ConnectionStatus,
    latencyMs: Long? = null,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text) = when (status) {
        ConnectionStatus.CONNECTED -> Triple(
            EmeraldGreen.copy(alpha = 0.15f),
            EmeraldGreen,
            if (latencyMs != null) "Connected • ${latencyMs}ms" else "Connected"
        )
        ConnectionStatus.SYNCING -> Triple(
            ElectricCyan.copy(alpha = 0.15f),
            ElectricCyan,
            "Syncing..."
        )
        ConnectionStatus.ERROR -> Triple(
            RoseRed.copy(alpha = 0.15f),
            RoseRed,
            "Sync Error"
        )
        ConnectionStatus.DISCONNECTED -> Triple(
            Color.Gray.copy(alpha = 0.15f),
            Color.LightGray,
            "Disconnected"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TaskPriorityBadge(priority: TaskPriority) {
    val (color, label) = when (priority) {
        TaskPriority.LOW -> Pair(Color(0xFF94A3B8), "LOW")
        TaskPriority.MEDIUM -> Pair(ElectricCyan, "MED")
        TaskPriority.HIGH -> Pair(AmberOrange, "HIGH")
        TaskPriority.CRITICAL -> Pair(RoseRed, "CRIT")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TaskStatusBadge(status: TaskStatus) {
    val (bgColor, textColor) = when (status) {
        TaskStatus.BACKLOG -> Pair(Color(0xFF334155), Color(0xFFCBD5E1))
        TaskStatus.IN_PROGRESS -> Pair(Color(0xFF1E3A8A), Color(0xFF93C5FD))
        TaskStatus.IN_REVIEW -> Pair(Color(0xFF4C1D95), Color(0xFFDDD6FE))
        TaskStatus.DONE -> Pair(Color(0xFF064E3B), Color(0xFFA7F3D0))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = status.displayName,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun LivePulseIndicator(
    isSyncing: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(if (isSyncing) scale else 1.0f)
                    .clip(CircleShape)
                    .background(if (isSyncing) ElectricCyan.copy(alpha = 0.4f) else EmeraldGreen.copy(alpha = 0.35f))
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isSyncing) ElectricCyan else EmeraldGreen)
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = if (isSyncing) "REAL-TIME SYNCING" else "LIVE WEBSOCKET STREAM",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = if (isSyncing) ElectricCyan else EmeraldGreen
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    color = iconTint.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
