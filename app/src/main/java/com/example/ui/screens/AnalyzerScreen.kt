package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DeviceInfo
import com.example.ui.components.SpecMeterBar
import com.example.ui.theme.CompatExcellent
import com.example.ui.theme.CyberPink
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.Strings
import java.util.Locale

@Composable
fun AnalyzerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analyzer_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = Strings.phoneAnalyzer(language),
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = Strings.analyzerDescription(language),
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Gaming Tier Highlight Card
        item {
            GamingTierCard(deviceInfo = deviceInfo, language = language)
        }

        // Memory (RAM) Card
        item {
            HardwareSectionCard(
                title = Strings.ramMemory(language),
                icon = Icons.Default.Memory,
                accentColor = NeonPurple
            ) {
                val totalGb = String.format(Locale.US, "%.1f GB", deviceInfo.totalRamGb)
                val availGb = String.format(Locale.US, "%.1f GB", deviceInfo.availableRamGb)
                val usedGb = String.format(Locale.US, "%.1f GB", deviceInfo.totalRamGb - deviceInfo.availableRamGb)

                SpecMeterBar(
                    title = Strings.ramUsage(language),
                    valueLabel = "$usedGb / $totalGb (${deviceInfo.ramUsagePercent}%)",
                    fraction = deviceInfo.ramUsagePercent / 100f,
                    gradientColors = listOf(NeonPurple, CyberPink)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecMetricBox(
                        label = if (language == AppLanguage.ARABIC) "إجمالي الرام" else "Total RAM",
                        value = totalGb,
                        color = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SpecMetricBox(
                        label = if (language == AppLanguage.ARABIC) "الرام المتوفر" else "Available RAM",
                        value = availGb,
                        color = CompatExcellent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Storage Card
        item {
            HardwareSectionCard(
                title = Strings.storageSpace(language),
                icon = Icons.Default.Storage,
                accentColor = ElectricCyan
            ) {
                val totalGb = String.format(Locale.US, "%.1f GB", deviceInfo.totalStorageGb)
                val freeGb = String.format(Locale.US, "%.1f GB", deviceInfo.availableStorageGb)
                val usedGb = String.format(Locale.US, "%.1f GB", deviceInfo.totalStorageGb - deviceInfo.availableStorageGb)

                SpecMeterBar(
                    title = if (language == AppLanguage.ARABIC) "المساحة المستخدمة" else "Storage Used",
                    valueLabel = "$usedGb / $totalGb (${deviceInfo.storageUsagePercent}%)",
                    fraction = deviceInfo.storageUsagePercent / 100f,
                    gradientColors = listOf(ElectricCyan, ElectricBlue)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecMetricBox(
                        label = if (language == AppLanguage.ARABIC) "المساحة الكلية" else "Total Capacity",
                        value = totalGb,
                        color = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SpecMetricBox(
                        label = Strings.storageFree(language),
                        value = freeGb,
                        color = CompatExcellent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Processor & Graphics Card
        item {
            HardwareSectionCard(
                title = Strings.cpuProcessors(language),
                icon = Icons.Default.DeveloperBoard,
                accentColor = Color(0xFFFFB300)
            ) {
                DetailRow(
                    label = if (language == AppLanguage.ARABIC) "عدد أنوية المعالج" else "Active CPU Cores",
                    value = "${deviceInfo.cpuCores} Cores"
                )
                DetailRow(
                    label = Strings.architecture(language),
                    value = if (deviceInfo.is64Bit) "64-bit Architecture" else "32-bit Architecture"
                )
                DetailRow(
                    label = if (language == AppLanguage.ARABIC) "المعمارية المدعومة (ABI)" else "Primary ABI",
                    value = deviceInfo.cpuArch.substringBefore(',')
                )
                DetailRow(
                    label = Strings.graphicsEngine(language),
                    value = "OpenGL ES ${deviceInfo.glEsVersion}"
                )
                DetailRow(
                    label = if (language == AppLanguage.ARABIC) "طراز الشريحة (SoC)" else "SoC Platform",
                    value = deviceInfo.socModel
                )
            }
        }

        // System & Model Card
        item {
            HardwareSectionCard(
                title = Strings.deviceModel(language),
                icon = Icons.Default.PhoneAndroid,
                accentColor = NeonPurple
            ) {
                DetailRow(
                    label = Strings.deviceModel(language),
                    value = deviceInfo.formattedModel
                )
                DetailRow(
                    label = if (language == AppLanguage.ARABIC) "الشركة المصنعة" else "Manufacturer",
                    value = deviceInfo.manufacturer
                )
                DetailRow(
                    label = Strings.androidVersion(language),
                    value = "Android ${deviceInfo.androidVersion} (API ${deviceInfo.sdkInt})"
                )
                DetailRow(
                    label = Strings.securityPatch(language),
                    value = deviceInfo.securityPatch
                )
                DetailRow(
                    label = if (language == AppLanguage.ARABIC) "اللوحة الأم" else "Motherboard",
                    value = deviceInfo.board
                )
            }
        }

        // Refresh Action
        item {
            Button(
                onClick = { viewModel.refreshDeviceInfo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("refresh_specs_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.refreshSpecs(language),
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun GamingTierCard(deviceInfo: DeviceInfo, language: AppLanguage) {
    val tier = deviceInfo.performanceTier
    val tierTitle = if (language == AppLanguage.ARABIC) tier.titleAr else tier.titleEn
    val tierDesc = if (language == AppLanguage.ARABIC) tier.descriptionAr else tier.descriptionEn

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2E134D),
                            Color(0xFF132A4A),
                            Color(0xFF0F1E38)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideogameAsset,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Strings.gamingTier(language),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = tierTitle,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = tierDesc,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun HardwareSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }
    }
}

@Composable
private fun SpecMetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCardElevated)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = label, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
