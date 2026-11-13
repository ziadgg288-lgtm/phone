package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DeviceInfo
import com.example.data.model.Game
import com.example.data.model.GameCategory
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GameCard
import com.example.ui.components.SyncStatusBar
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
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToGame: (Game) -> Unit,
    onNavigateToAnalyzer: () -> Unit,
    onNavigateToLibraryCategory: (GameCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val syncMetadata by viewModel.syncMetadata.collectAsStateWithLifecycle()

    val featuredGames by viewModel.featuredGames.collectAsStateWithLifecycle()
    val popularGames by viewModel.popularGames.collectAsStateWithLifecycle()
    val lightweightGames by viewModel.lightweightGames.collectAsStateWithLifecycle()
    val offlineGames by viewModel.offlineGames.collectAsStateWithLifecycle()
    val newlyReleasedGames by viewModel.newlyReleasedGames.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top App Bar / Header
        item {
            HomeHeader(
                deviceInfo = deviceInfo,
                language = language,
                onLanguageToggle = {
                    val nextLang = if (language == AppLanguage.ENGLISH) AppLanguage.ARABIC else AppLanguage.ENGLISH
                    viewModel.setLanguage(nextLang)
                }
            )
        }

        // Live Smart Sync Status Bar
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                SyncStatusBar(
                    isOnline = isOnline,
                    syncState = syncState,
                    syncMetadata = syncMetadata,
                    language = language,
                    onManualSync = { viewModel.syncDatabase(force = true) }
                )
            }
        }

        // Quick Phone Hardware Summary Card
        item {
            DeviceSummaryBanner(
                deviceInfo = deviceInfo,
                language = language,
                onInspectClick = onNavigateToAnalyzer
            )
        }

        // Newly Released Games (Visible when available from online updates)
        if (newlyReleasedGames.isNotEmpty()) {
            item {
                SectionHeader(
                    title = Strings.newGamesSection(language),
                    subtitle = if (language == AppLanguage.ARABIC) "أحدث العناوين التي تمت إضافتها من التحديث المباشر" else "Fresh titles retrieved via online catalog sync",
                    onSeeAllClick = { onNavigateToLibraryCategory(GameCategory.ALL) },
                    language = language
                )
            }

            items(newlyReleasedGames, key = { it.id }) { game ->
                GameCard(
                    game = game,
                    compatibility = viewModel.getCompatibility(game),
                    isFavorite = favoriteIds.contains(game.id),
                    language = language,
                    onGameClick = { onNavigateToGame(game) },
                    onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(14.dp)) }
        }

        // Featured Games Section
        item {
            SectionHeader(
                title = Strings.featuredGames(language),
                subtitle = if (language == AppLanguage.ARABIC) "أفضل الألعاب ذات الشعبية العالية" else "Top-tier curated titles",
                onSeeAllClick = { onNavigateToLibraryCategory(GameCategory.ALL) },
                language = language
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(featuredGames, key = { it.id }) { game ->
                    FeaturedGameBanner(
                        game = game,
                        compatibility = viewModel.getCompatibility(game),
                        language = language,
                        onClick = { onNavigateToGame(game) }
                    )
                }
            }
        }

        // Quick Category Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                val categories = listOf(
                    GameCategory.ALL,
                    GameCategory.LOW_END,
                    GameCategory.OFFLINE,
                    GameCategory.ACTION,
                    GameCategory.RACING,
                    GameCategory.OPEN_WORLD,
                    GameCategory.ADVENTURE,
                    GameCategory.MULTIPLAYER
                )
                items(categories) { cat ->
                    val isSelected = cat == GameCategory.ALL
                    val title = if (language == AppLanguage.ARABIC) cat.titleAr else cat.titleEn
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) NeonPurple else SurfaceCard)
                            .border(1.dp, if (isSelected) NeonPurple else SurfaceBorder, RoundedCornerShape(20.dp))
                            .clickable { onNavigateToLibraryCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("category_chip_${cat.name.lowercase()}")
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Popular Games Section
        item {
            SectionHeader(
                title = Strings.popularGames(language),
                subtitle = if (language == AppLanguage.ARABIC) "ألعاب عالمية بملايين التنزيلات" else "Most played mobile blockbusters",
                onSeeAllClick = { onNavigateToLibraryCategory(GameCategory.ALL) },
                language = language
            )
        }

        items(popularGames.take(3), key = { it.id }) { game ->
            GameCard(
                game = game,
                compatibility = viewModel.getCompatibility(game),
                isFavorite = favoriteIds.contains(game.id),
                language = language,
                onGameClick = { onNavigateToGame(game) },
                onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Lightweight Games for Low-end phones
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = Strings.lightweightGames(language),
                subtitle = if (language == AppLanguage.ARABIC) "تعمل بسلاسة على الهواتف ذات 2GB إلى 3GB رام" else "Smooth performance on 2GB-3GB RAM phones",
                onSeeAllClick = { onNavigateToLibraryCategory(GameCategory.LOW_END) },
                language = language
            )
        }

        items(lightweightGames.take(3), key = { it.id }) { game ->
            GameCard(
                game = game,
                compatibility = viewModel.getCompatibility(game),
                isFavorite = favoriteIds.contains(game.id),
                language = language,
                onGameClick = { onNavigateToGame(game) },
                onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Offline Games Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = Strings.offlineGames(language),
                subtitle = if (language == AppLanguage.ARABIC) "العب في أي مكان بدون اتصال بالإنترنت" else "No Wi-Fi or mobile data needed",
                onSeeAllClick = { onNavigateToLibraryCategory(GameCategory.OFFLINE) },
                language = language
            )
        }

        items(offlineGames.take(3), key = { it.id }) { game ->
            GameCard(
                game = game,
                compatibility = viewModel.getCompatibility(game),
                isFavorite = favoriteIds.contains(game.id),
                language = language,
                onGameClick = { onNavigateToGame(game) },
                onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun HomeHeader(
    deviceInfo: DeviceInfo,
    language: AppLanguage,
    onLanguageToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = Strings.appTitle(language),
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = Strings.appSubtitle(language),
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Language toggle pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCardElevated)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
                .clickable(onClick = onLanguageToggle)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("language_toggle_button")
        ) {
            Text(
                text = if (language == AppLanguage.ENGLISH) "العربية" else "English",
                color = ElectricCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeviceSummaryBanner(
    deviceInfo: DeviceInfo,
    language: AppLanguage,
    onInspectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onInspectClick)
            .testTag("device_summary_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = deviceInfo.formattedModel,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Android ${deviceInfo.androidVersion} • ${deviceInfo.cpuCores} Cores",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Performance Tier Pill
                val tierTitle = if (language == AppLanguage.ARABIC) deviceInfo.performanceTier.titleAr else deviceInfo.performanceTier.titleEn
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricCyan.copy(alpha = 0.15f))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tierTitle,
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Spec Micro-Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpecChip(
                    icon = Icons.Default.Memory,
                    label = "RAM",
                    value = String.format(Locale.US, "%.1f GB", deviceInfo.totalRamGb),
                    modifier = Modifier.weight(1f)
                )
                SpecChip(
                    icon = Icons.Default.Storage,
                    label = if (language == AppLanguage.ARABIC) "المساحة الحرة" else "Free Space",
                    value = String.format(Locale.US, "%.1f GB", deviceInfo.availableStorageGb),
                    modifier = Modifier.weight(1f)
                )
                SpecChip(
                    icon = Icons.Default.Speed,
                    label = if (language == AppLanguage.ARABIC) "المعمارية" else "Arch",
                    value = if (deviceInfo.is64Bit) "64-bit" else "32-bit",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.tapToInspect(language),
                    color = ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SpecChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCardElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = label, color = TextMuted, fontSize = 9.sp)
            Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FeaturedGameBanner(
    game: Game,
    compatibility: com.example.data.model.CompatibilityResult,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("featured_game_${game.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(game.accentGradientStart),
                            Color(game.accentGradientEnd).copy(alpha = 0.85f),
                            Color.Black
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val badgeText = if (language == AppLanguage.ARABIC) game.badgeLabelAr ?: game.name else game.badgeLabelEn ?: game.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    CompatibilityBadge(
                        level = compatibility.level,
                        language = language,
                        compact = true
                    )
                }

                Column {
                    Text(
                        text = game.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (language == AppLanguage.ARABIC) game.taglineAr else game.taglineEn,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${game.downloadSizeFormatted} • ${game.developer}",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    onSeeAllClick: () -> Unit,
    language: AppLanguage
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Text(
            text = Strings.seeAll(language),
            color = NeonPurple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(onClick = onSeeAllClick)
                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}
