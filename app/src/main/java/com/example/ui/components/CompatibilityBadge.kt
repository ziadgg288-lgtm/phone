package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompatibilityLevel
import com.example.ui.theme.CompatExcellent
import com.example.ui.theme.CompatExcellentBg
import com.example.ui.theme.CompatNotRecommended
import com.example.ui.theme.CompatNotRecommendedBg
import com.example.ui.theme.CompatPossible
import com.example.ui.theme.CompatPossibleBg
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun CompatibilityBadge(
    level: CompatibilityLevel,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (badgeBg, borderColor, textColor, icon) = when (level) {
        CompatibilityLevel.EXCELLENT -> Quad(
            CompatExcellentBg,
            CompatExcellent,
            CompatExcellent,
            Icons.Default.CheckCircle
        )
        CompatibilityLevel.POSSIBLE -> Quad(
            CompatPossibleBg,
            CompatPossible,
            CompatPossible,
            Icons.Default.Warning
        )
        CompatibilityLevel.NOT_RECOMMENDED -> Quad(
            CompatNotRecommendedBg,
            CompatNotRecommended,
            CompatNotRecommended,
            Icons.Default.Close
        )
    }

    val text = when (level) {
        CompatibilityLevel.EXCELLENT -> Strings.excellent(language)
        CompatibilityLevel.POSSIBLE -> Strings.possible(language)
        CompatibilityLevel.NOT_RECOMMENDED -> Strings.notRecommended(language)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeBg)
            .border(1.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 4.dp else 6.dp)
            .testTag("compatibility_badge_${level.name.lowercase()}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(if (compact) 14.dp else 16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
