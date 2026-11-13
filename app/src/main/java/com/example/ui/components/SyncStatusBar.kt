package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SyncMetadataEntity
import com.example.data.network.SyncState
import com.example.ui.theme.CompatExcellent
import com.example.ui.theme.CompatPossible
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun SyncStatusBar(
    isOnline: Boolean,
    syncState: SyncState,
    syncMetadata: SyncMetadataEntity?,
    language: AppLanguage,
    onManualSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSyncing = syncState is SyncState.Syncing

    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val (statusColor, statusIcon, statusText) = when {
        isSyncing -> Triple(
            NeonPurple,
            Icons.Default.Sync,
            Strings.syncStatusSyncing(language)
        )
        isOnline -> Triple(
            CompatExcellent,
            Icons.Default.CloudDone,
            Strings.syncStatusOnline(language)
        )
        else -> Triple(
            CompatPossible,
            Icons.Default.CloudOff,
            Strings.syncStatusOffline(language)
        )
    }

    val lastUpdateDate = syncMetadata?.lastSyncTimestamp?.let {
        java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.US).format(java.util.Date(it))
    } ?: "Initialized"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("sync_status_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier
                            .size(17.dp)
                            .then(if (isSyncing) Modifier.rotate(rotation) else Modifier)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = statusText,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${Strings.lastUpdatedPrefix(language)}$lastUpdateDate",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Manual Update / Refresh Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonPurple.copy(alpha = 0.15f))
                    .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable(enabled = !isSyncing, onClick = onManualSync)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("manual_sync_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = Strings.syncNow(language),
                        tint = if (isSyncing) TextMuted else ElectricCyan,
                        modifier = Modifier
                            .size(14.dp)
                            .then(if (isSyncing) Modifier.rotate(rotation) else Modifier)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Strings.syncNow(language),
                        color = if (isSyncing) TextMuted else ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
