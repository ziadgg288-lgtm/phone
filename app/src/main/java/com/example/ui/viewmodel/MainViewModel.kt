package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.SyncMetadataEntity
import com.example.data.device.DeviceAnalyzer
import com.example.data.model.CompatibilityLevel
import com.example.data.model.CompatibilityResult
import com.example.data.model.DeviceInfo
import com.example.data.model.Game
import com.example.data.model.GameCategory
import com.example.data.network.GameSyncManager
import com.example.data.network.SyncState
import com.example.data.repository.GameRepository
import com.example.util.AppLanguage
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CompatibilityFilter(val titleEn: String, val titleAr: String) {
    ALL("All Levels", "كل المستويات"),
    EXCELLENT_ONLY("Excellent Only", "الممتاز فقط"),
    PLAYABLE("Playable on my phone", "قابل للتشغيل على هاتفي")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val deviceAnalyzer = DeviceAnalyzer(application)
    private val networkMonitor = NetworkMonitor(application)
    private val syncManager = GameSyncManager(application, networkMonitor)
    private val database = AppDatabase.getInstance(application)
    private val repository = GameRepository(
        database.gameDao(),
        database.favoriteDao(),
        database.syncMetadataDao(),
        syncManager
    )

    private val _deviceInfo = MutableStateFlow(deviceAnalyzer.getDeviceInfo())
    val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _selectedGame = MutableStateFlow<Game?>(null)
    val selectedGame: StateFlow<Game?> = _selectedGame.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(GameCategory.ALL)
    val selectedCategory: StateFlow<GameCategory> = _selectedCategory.asStateFlow()

    private val _compatibilityFilter = MutableStateFlow(CompatibilityFilter.ALL)
    val compatibilityFilter: StateFlow<CompatibilityFilter> = _compatibilityFilter.asStateFlow()

    private val _offlineOnly = MutableStateFlow(false)
    val offlineOnly: StateFlow<Boolean> = _offlineOnly.asStateFlow()

    // Live network connectivity stream
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = networkMonitor.isCurrentlyConnected()
    )

    val syncState: StateFlow<SyncState> = repository.syncState

    val syncMetadata: StateFlow<SyncMetadataEntity?> = repository.syncMetadataStream.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteIds: StateFlow<Set<String>> = repository.favorites
        .combine(_deviceInfo) { favs, _ ->
            favs.map { it.gameId }.toSet()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Offline-first games stream directly backed by Room database
    val allGames: StateFlow<List<Game>> = repository.gamesStream.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.defaultInitialGames
    )

    val featuredGames: StateFlow<List<Game>> = allGames.combine(_deviceInfo) { games, _ ->
        games.filter { it.isFeatured }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.defaultInitialGames.filter { it.isFeatured }
    )

    val newlyReleasedGames: StateFlow<List<Game>> = allGames.combine(_deviceInfo) { games, _ ->
        games.filter { it.isNewlyReleased }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val popularGames: StateFlow<List<Game>> = allGames.combine(_deviceInfo) { games, _ ->
        games.filter { it.isPopular }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.defaultInitialGames.filter { it.isPopular }
    )

    val lightweightGames: StateFlow<List<Game>> = allGames.combine(_deviceInfo) { games, _ ->
        games.filter { it.isLightweight }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.defaultInitialGames.filter { it.isLightweight }
    )

    val offlineGames: StateFlow<List<Game>> = allGames.combine(_deviceInfo) { games, _ ->
        games.filter { it.isOfflinePlayable }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.defaultInitialGames.filter { it.isOfflinePlayable }
    )

    // Filtered games stream for Library (searches across local offline cache)
    val filteredGames: StateFlow<List<Game>> = combine(
        allGames,
        _searchQuery,
        _selectedCategory,
        _compatibilityFilter,
        _offlineOnly
    ) { games, query, category, compatFilter, offline ->
        val devInfo = _deviceInfo.value
        games.filter { game ->
            val matchesQuery = query.isBlank() ||
                    game.name.contains(query, ignoreCase = true) ||
                    game.developer.contains(query, ignoreCase = true) ||
                    game.taglineEn.contains(query, ignoreCase = true) ||
                    game.taglineAr.contains(query, ignoreCase = true)

            val matchesCategory = category == GameCategory.ALL ||
                    (category == GameCategory.LOW_END && game.isLightweight) ||
                    (category == GameCategory.OFFLINE && game.isOfflinePlayable) ||
                    game.categories.contains(category)

            val matchesOffline = !offline || game.isOfflinePlayable

            val compat = deviceAnalyzer.evaluateCompatibility(devInfo, game)
            val matchesCompat = when (compatFilter) {
                CompatibilityFilter.ALL -> true
                CompatibilityFilter.EXCELLENT_ONLY -> compat.level == CompatibilityLevel.EXCELLENT
                CompatibilityFilter.PLAYABLE -> compat.level != CompatibilityLevel.NOT_RECOMMENDED
            }

            matchesQuery && matchesCategory && matchesOffline && matchesCompat
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.defaultInitialGames
    )

    init {
        // Initialize offline Room database and smart sync
        viewModelScope.launch {
            repository.initializeOfflineCacheIfNeeded()
            if (networkMonitor.isCurrentlyConnected()) {
                repository.syncOnline(force = false)
            }
        }

        // Smart sync when internet connectivity returns
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    repository.syncOnline(force = false)
                }
            }
        }
    }

    fun syncDatabase(force: Boolean = true) {
        viewModelScope.launch {
            repository.syncOnline(force = force)
        }
    }

    fun formatLastUpdated(timestamp: Long?): String {
        val ts = timestamp ?: return "Never"
        return SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.US).format(Date(ts))
    }

    fun getCompatibility(game: Game): CompatibilityResult {
        return deviceAnalyzer.evaluateCompatibility(_deviceInfo.value, game)
    }

    fun refreshDeviceInfo() {
        _deviceInfo.value = deviceAnalyzer.getDeviceInfo()
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun selectGame(game: Game?) {
        _selectedGame.value = game
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: GameCategory) {
        _selectedCategory.value = category
    }

    fun setCompatibilityFilter(filter: CompatibilityFilter) {
        _compatibilityFilter.value = filter
    }

    fun setOfflineOnly(enabled: Boolean) {
        _offlineOnly.value = enabled
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = GameCategory.ALL
        _compatibilityFilter.value = CompatibilityFilter.ALL
        _offlineOnly.value = false
    }

    fun toggleFavorite(gameId: String) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(gameId)
            repository.toggleFavorite(gameId, isFav)
        }
    }
}
