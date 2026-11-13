package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.GameCategory
import com.example.ui.screens.AnalyzerScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.GameDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.Strings

enum class AppDestination(val route: String) {
    HOME("home"),
    LIBRARY("library"),
    ANALYZER("analyzer"),
    FAVORITES("favorites"),
    SETTINGS("settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel = viewModel()) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val selectedGame by viewModel.selectedGame.collectAsStateWithLifecycle()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.HOME) }

    CompositionLocalProvider(LocalLayoutDirection provides language.layoutDirection) {
        if (selectedGame != null) {
            GameDetailScreen(
                game = selectedGame!!,
                viewModel = viewModel,
                onBack = { viewModel.selectGame(null) }
            )
        } else {
            Scaffold(
                containerColor = DarkBackground,
                bottomBar = {
                    NavigationBar(
                        containerColor = SurfaceCard,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .border(androidx.compose.foundation.BorderStroke(0.5.dp, SurfaceBorder))
                            .testTag("bottom_nav_bar")
                    ) {
                        NavDestinationItem(
                            selected = currentDestination == AppDestination.HOME,
                            onClick = { currentDestination = AppDestination.HOME },
                            selectedIcon = Icons.Default.Home,
                            unselectedIcon = Icons.Outlined.Home,
                            label = Strings.navHome(language),
                            tag = "nav_home"
                        )
                        NavDestinationItem(
                            selected = currentDestination == AppDestination.LIBRARY,
                            onClick = { currentDestination = AppDestination.LIBRARY },
                            selectedIcon = Icons.Default.SportsEsports,
                            unselectedIcon = Icons.Outlined.SportsEsports,
                            label = Strings.navLibrary(language),
                            tag = "nav_library"
                        )
                        NavDestinationItem(
                            selected = currentDestination == AppDestination.ANALYZER,
                            onClick = { currentDestination = AppDestination.ANALYZER },
                            selectedIcon = Icons.Default.Speed,
                            unselectedIcon = Icons.Outlined.Speed,
                            label = Strings.navAnalyzer(language),
                            tag = "nav_analyzer"
                        )
                        NavDestinationItem(
                            selected = currentDestination == AppDestination.FAVORITES,
                            onClick = { currentDestination = AppDestination.FAVORITES },
                            selectedIcon = Icons.Default.Favorite,
                            unselectedIcon = Icons.Outlined.FavoriteBorder,
                            label = Strings.navFavorites(language),
                            tag = "nav_favorites"
                        )
                        NavDestinationItem(
                            selected = currentDestination == AppDestination.SETTINGS,
                            onClick = { currentDestination = AppDestination.SETTINGS },
                            selectedIcon = Icons.Default.Settings,
                            unselectedIcon = Icons.Outlined.Settings,
                            label = Strings.navSettings(language),
                            tag = "nav_settings"
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition",
                    modifier = Modifier.padding(innerPadding)
                ) { destination ->
                    when (destination) {
                        AppDestination.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToGame = { viewModel.selectGame(it) },
                            onNavigateToAnalyzer = { currentDestination = AppDestination.ANALYZER },
                            onNavigateToLibraryCategory = { cat ->
                                viewModel.setCategory(cat)
                                currentDestination = AppDestination.LIBRARY
                            }
                        )
                        AppDestination.LIBRARY -> LibraryScreen(
                            viewModel = viewModel,
                            onNavigateToGame = { viewModel.selectGame(it) }
                        )
                        AppDestination.ANALYZER -> AnalyzerScreen(
                            viewModel = viewModel
                        )
                        AppDestination.FAVORITES -> FavoritesScreen(
                            viewModel = viewModel,
                            onNavigateToGame = { viewModel.selectGame(it) },
                            onExploreLibrary = {
                                viewModel.setCategory(GameCategory.ALL)
                                currentDestination = AppDestination.LIBRARY
                            }
                        )
                        AppDestination.SETTINGS -> SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavDestinationItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    tag: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                modifier = Modifier.size(22.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ElectricCyan,
            selectedTextColor = ElectricCyan,
            indicatorColor = NeonPurple.copy(alpha = 0.25f),
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextSecondary
        ),
        modifier = Modifier.testTag(tag)
    )
}
