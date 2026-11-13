package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.device.DeviceAnalyzer
import com.example.data.model.CompatibilityLevel
import com.example.data.model.DeviceInfo
import com.example.data.network.GameSyncManager
import com.example.data.repository.GameRepository
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: GameRepository
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var syncManager: GameSyncManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        networkMonitor = NetworkMonitor(context)
        syncManager = GameSyncManager(context, networkMonitor)
        repository = GameRepository(
            database.gameDao(),
            database.favoriteDao(),
            database.syncMetadataDao(),
            syncManager
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context matches Game Finder`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Game Finder", appName)
    }

    @Test
    fun `device analyzer returns non-null hardware telemetry`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val analyzer = DeviceAnalyzer(context)
        val info = analyzer.getDeviceInfo()

        assertNotNull(info.model)
        assertTrue(info.cpuCores >= 1)
        assertTrue(info.totalRamBytes > 0)
        assertTrue(info.totalStorageBytes > 0)
    }

    @Test
    fun `offline repository initializes cache without internet`() = runBlocking {
        // Must never show blank screen: initializes with default verified games
        repository.initializeOfflineCacheIfNeeded()
        val cachedGames = repository.gamesStream.first()
        assertTrue(cachedGames.isNotEmpty())
        assertTrue(cachedGames.any { it.id == "genshin_impact" })
        assertTrue(cachedGames.any { it.id == "subway_surfers" })
    }

    @Test
    fun `smart sync manager contains verified new releases`() {
        val newReleases = syncManager.latestOnlineCatalog
        assertTrue(newReleases.isNotEmpty())
        assertTrue(newReleases.any { it.isNewlyReleased })
        assertTrue(newReleases.any { it.id == "zenless_zone_zero" })
    }

    @Test
    fun `compatibility evaluation works for low end and high end games`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val analyzer = DeviceAnalyzer(context)

        // Simulate a flagship device
        val flagshipDevice = DeviceInfo(
            model = "Pixel 8 Pro",
            manufacturer = "Google",
            brand = "Google",
            board = "husky",
            androidVersion = "14",
            sdkInt = 34,
            securityPatch = "2024-05-01",
            totalRamBytes = 12L * 1024 * 1024 * 1024,
            availableRamBytes = 8L * 1024 * 1024 * 1024,
            totalStorageBytes = 256L * 1024 * 1024 * 1024,
            availableStorageBytes = 150L * 1024 * 1024 * 1024,
            cpuCores = 8,
            cpuArch = "arm64-v8a",
            is64Bit = true,
            glEsVersion = "3.2",
            socModel = "Tensor G3"
        )

        // Simulate an entry level device
        val entryDevice = DeviceInfo(
            model = "Budget Phone",
            manufacturer = "Generic",
            brand = "Generic",
            board = "budget",
            androidVersion = "8.1",
            sdkInt = 27,
            securityPatch = "2018-01-01",
            totalRamBytes = 2L * 1024 * 1024 * 1024,
            availableRamBytes = 800L * 1024 * 1024,
            totalStorageBytes = 16L * 1024 * 1024 * 1024,
            availableStorageBytes = 2L * 1024 * 1024 * 1024,
            cpuCores = 4,
            cpuArch = "armeabi-v7a",
            is64Bit = false,
            glEsVersion = "3.0",
            socModel = "Entry Chip"
        )

        val genshin = repository.defaultInitialGames.first { it.id == "genshin_impact" }
        val subwaySurfers = repository.defaultInitialGames.first { it.id == "subway_surfers" }

        val flagshipCompatGenshin = analyzer.evaluateCompatibility(flagshipDevice, genshin)
        assertEquals(CompatibilityLevel.EXCELLENT, flagshipCompatGenshin.level)

        val entryCompatGenshin = analyzer.evaluateCompatibility(entryDevice, genshin)
        assertEquals(CompatibilityLevel.NOT_RECOMMENDED, entryCompatGenshin.level)

        val entryCompatSubway = analyzer.evaluateCompatibility(entryDevice, subwaySurfers)
        assertTrue(entryCompatSubway.level != CompatibilityLevel.NOT_RECOMMENDED)
    }
}
