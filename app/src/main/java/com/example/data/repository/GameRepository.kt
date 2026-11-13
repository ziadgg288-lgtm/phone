package com.example.data.repository

import com.example.data.database.FavoriteDao
import com.example.data.database.FavoriteGameEntity
import com.example.data.database.GameDao
import com.example.data.database.GameEntity
import com.example.data.database.SyncMetadataDao
import com.example.data.database.SyncMetadataEntity
import com.example.data.model.Game
import com.example.data.model.GameCategory
import com.example.data.network.GameSyncManager
import com.example.data.network.SyncState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameRepository(
    private val gameDao: GameDao,
    private val favoriteDao: FavoriteDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val syncManager: GameSyncManager
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Base verified initial catalog bundled locally (ensures 100% offline functionality from first launch)
    val defaultInitialGames: List<Game> = listOf(
        Game(
            id = "genshin_impact",
            name = "Genshin Impact",
            developer = "HoYoverse",
            categories = listOf(GameCategory.ACTION, GameCategory.ADVENTURE, GameCategory.OPEN_WORLD, GameCategory.MULTIPLAYER),
            rating = 4.4f,
            downloadSizeFormatted = "~25 GB",
            storageRequiredBytes = 26_843_545_600L,
            minRamMb = 4096,
            recommendedRamMb = 6144,
            minAndroidVersion = 27, // Android 8.1
            recommendedAndroidVersion = 30, // Android 11+
            minCpuCores = 8,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = false,
            descriptionEn = "Venture into Teyvat, a vast world teeming with life and elemental energy. Explore seven nations, recruit diverse characters, and unravel the world's mysteries.",
            descriptionAr = "انطلق في عالم تيفات الشاسع المليء بالطاقة والحياة. استكشف سبع ممالك، وقم بتجنيد شخصيات مميزة، واكتشف أسرار العالم في تجربة آر بي جي مذهلة.",
            minGpuTierEn = "Adreno 618 / Mali-G52 MC2",
            minGpuTierAr = "Adreno 618 أو Mali-G52 أو ما يعادلها",
            recommendedGpuTierEn = "Snapdragon 845 / Dimensity 1200 or newer",
            recommendedGpuTierAr = "Snapdragon 845 أو Dimensity 1200 أو أحدث",
            playStorePackage = "com.miHoYo.GenshinImpact",
            badgeLabelEn = "Triple-A Visuals",
            badgeLabelAr = "رسوميات قوية",
            isFeatured = true,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFF5B34DA,
            accentGradientEnd = 0xFF28C2FF,
            taglineEn = "Step into a vast magical world of adventure",
            taglineAr = "ادخل إلى عالم سحري شاسع مليء بالمغامرات"
        ),
        Game(
            id = "cod_mobile",
            name = "Call of Duty: Mobile",
            developer = "Activision Publishing, Inc.",
            categories = listOf(GameCategory.ACTION, GameCategory.MULTIPLAYER),
            rating = 4.5f,
            downloadSizeFormatted = "~4.5 GB",
            storageRequiredBytes = 5_368_709_120L,
            minRamMb = 2048,
            recommendedRamMb = 4096,
            minAndroidVersion = 22, // Android 5.1
            recommendedAndroidVersion = 29, // Android 10+
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = false,
            descriptionEn = "Iconic FPS multiplayer and 100-player Battle Royale combat on mobile with customizable loadouts and multiplayer maps like Nuketown.",
            descriptionAr = "تجربة إطلاق نار أيقونية من منظور الشخص الأول مع أطوار لعب جماعية وحرب باتل رويال ضخمة لـ 100 لاعب، مع خرائط شهيرة مثل Nuketown.",
            minGpuTierEn = "Adreno 506 / Mali-T830",
            minGpuTierAr = "Adreno 506 أو Mali-T830",
            recommendedGpuTierEn = "Snapdragon 720G / Dimensity 800 or newer",
            recommendedGpuTierAr = "Snapdragon 720G أو Dimensity 800 أو أحدث",
            playStorePackage = "com.activision.callofduty.shooter",
            badgeLabelEn = "Tactical FPS",
            badgeLabelAr = "قتال تكتيكي",
            isFeatured = true,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFFBF5700,
            accentGradientEnd = 0xFF178BFB,
            taglineEn = "Definitive mobile FPS experience",
            taglineAr = "تجربة إطلاق النار الأولى على الهواتف"
        ),
        Game(
            id = "pubg_mobile",
            name = "PUBG Mobile",
            developer = "Level Infinite",
            categories = listOf(GameCategory.ACTION, GameCategory.OPEN_WORLD, GameCategory.MULTIPLAYER),
            rating = 4.3f,
            downloadSizeFormatted = "~4.0 GB",
            storageRequiredBytes = 4_831_838_208L,
            minRamMb = 2048,
            recommendedRamMb = 4096,
            minAndroidVersion = 22,
            recommendedAndroidVersion = 29,
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = false,
            isOfflinePlayable = false,
            descriptionEn = "Drop onto massive 8x8 km battlegrounds, gear up with weapons and vehicles, and survive against 99 other players in epic survival gameplay.",
            descriptionAr = "اهبط في ساحات معارك شاسعة مساحتها 8×8 كم، وتسلح بالعتاد والمركبات، ونافس 99 لاعباً لتكون الناجي الأخير في أقوى تجربة باتل رويال.",
            minGpuTierEn = "Adreno 505 / Mali-T720",
            minGpuTierAr = "Adreno 505 أو Mali-T720",
            recommendedGpuTierEn = "Snapdragon 680 / Helio G95 or higher",
            recommendedGpuTierAr = "Snapdragon 680 أو Helio G95 أو أعلى",
            playStorePackage = "com.tencent.ig",
            badgeLabelEn = "Top Battle Royale",
            badgeLabelAr = "أشهر باتل رويال",
            isFeatured = false,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFFFF7B00,
            accentGradientEnd = 0xFFFFC300,
            taglineEn = "Winner Winner Chicken Dinner",
            taglineAr = "انتصر وكن الناجي الأخير في الساحة"
        ),
        Game(
            id = "asphalt_9",
            name = "Asphalt 9: Legends",
            developer = "Gameloft SE",
            categories = listOf(GameCategory.RACING, GameCategory.ACTION, GameCategory.MULTIPLAYER),
            rating = 4.4f,
            downloadSizeFormatted = "~3.8 GB",
            storageRequiredBytes = 4_294_967_296L,
            minRamMb = 3072,
            recommendedRamMb = 4096,
            minAndroidVersion = 24, // Android 7.0
            recommendedAndroidVersion = 29, // Android 10+
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = false,
            descriptionEn = "Race high-performance hypercars across breathtaking real-world locations. Features HDR graphics, nitro boosts, and multiplayer clubs.",
            descriptionAr = "تسابق بأسرع السيارات الخارقة في مواقع حقيقية مذهلة حول العالم. تتضمن رسوميات HDR سينمائية، وتعزيزات نيترو، ونوادي سباق جماعية.",
            minGpuTierEn = "Adreno 508 / Mali-G71",
            minGpuTierAr = "Adreno 508 أو Mali-G71",
            recommendedGpuTierEn = "Snapdragon 730G / Exynos 9810 or higher",
            recommendedGpuTierAr = "Snapdragon 730G أو Exynos 9810 أو أعلى",
            playStorePackage = "com.gameloft.android.ANMP.GloftA9HM",
            badgeLabelEn = "Hypercar Racing",
            badgeLabelAr = "سباقات خارقة",
            isFeatured = true,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFFD81159,
            accentGradientEnd = 0xFF00E5FF,
            taglineEn = "Tear up the asphalt at lightning speeds",
            taglineAr = "مزّق الأسفلت وتصدر السباق بأقصى سرعة"
        ),
        Game(
            id = "minecraft",
            name = "Minecraft",
            developer = "Mojang",
            categories = listOf(GameCategory.ADVENTURE, GameCategory.OPEN_WORLD, GameCategory.OFFLINE, GameCategory.MULTIPLAYER),
            rating = 4.6f,
            downloadSizeFormatted = "~1.2 GB",
            storageRequiredBytes = 1_610_612_736L,
            minRamMb = 2048,
            recommendedRamMb = 4096,
            minAndroidVersion = 26, // Android 8.0
            recommendedAndroidVersion = 29,
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Build anything you can imagine in creative mode, or embark on grand expeditions in survival mode, journeying across mysterious infinite lands.",
            descriptionAr = "ابنِ كل ما يمكنك تخيله في طور الإبداع، أو انطلق في رحلات استكشافية مثيرة في طور البقاء في عوالم لانهائية ومولدّة عشوائياً.",
            minGpuTierEn = "OpenGL ES 3.0 Compatible",
            minGpuTierAr = "متوافق مع OpenGL ES 3.0",
            recommendedGpuTierEn = "Modern Octa-Core Chipset",
            recommendedGpuTierAr = "معالج ثماني الأنوية حديث",
            playStorePackage = "com.mojang.minecraftpe",
            badgeLabelEn = "Endless Sandbox",
            badgeLabelAr = "إبداع لا نهائي",
            isFeatured = true,
            isPopular = true,
            isLightweight = true,
            accentGradientStart = 0xFF2D6A4F,
            accentGradientEnd = 0xFF52B788,
            taglineEn = "Explore infinite worlds and craft your story",
            taglineAr = "استكشف عوالم لا نهائية واصنع قصتك"
        ),
        Game(
            id = "subway_surfers",
            name = "Subway Surfers",
            developer = "SYBO Games",
            categories = listOf(GameCategory.ACTION, GameCategory.OFFLINE, GameCategory.LOW_END),
            rating = 4.5f,
            downloadSizeFormatted = "~280 MB",
            storageRequiredBytes = 367_001_600L,
            minRamMb = 1024,
            recommendedRamMb = 2048,
            minAndroidVersion = 21, // Android 5.0
            recommendedAndroidVersion = 26,
            minCpuCores = 2,
            recommendedCpuCores = 4,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Dash as fast as you can through subway tracks, dodge oncoming trains, and collect coins in the legendary endless runner.",
            descriptionAr = "اركض بأقصى سرعة بين مسارات القطارات وتفادَ العوائق واجمع الذهب في أشهر لعبة ركض كلاسيكية ومسلية بدون إنترنت.",
            minGpuTierEn = "Any OpenGL ES 2.0 GPU",
            minGpuTierAr = "أي معالج رسومي يدعم OpenGL ES 2.0",
            recommendedGpuTierEn = "Basic Adreno / Mali GPU",
            recommendedGpuTierAr = "معالج رسومي أساسي من Adreno أو Mali",
            playStorePackage = "com.kiloo.subwaysurf",
            badgeLabelEn = "Ultra Lightweight",
            badgeLabelAr = "خفيفة جداً",
            isFeatured = false,
            isPopular = true,
            isLightweight = true,
            accentGradientStart = 0xFF0077B6,
            accentGradientEnd = 0xFF00B4D8,
            taglineEn = "Endless fun, runs on virtually every phone",
            taglineAr = "متعة لا نهائية وتعمل على أي هاتف تقريباً"
        ),
        Game(
            id = "stardew_valley",
            name = "Stardew Valley",
            developer = "ConcernedApe",
            categories = listOf(GameCategory.ADVENTURE, GameCategory.OPEN_WORLD, GameCategory.OFFLINE, GameCategory.LOW_END),
            rating = 4.8f,
            downloadSizeFormatted = "~450 MB",
            storageRequiredBytes = 524_288_000L,
            minRamMb = 2048,
            recommendedRamMb = 3072,
            minAndroidVersion = 21,
            recommendedAndroidVersion = 28,
            minCpuCores = 4,
            recommendedCpuCores = 4,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "You've inherited your grandfather's old farm plot. Armed with hand-me-down tools, can you learn to live off the land and restore the valley to greatness?",
            descriptionAr = "لقد ورثت مزرعة جدك القديمة. هل يمكنك تعلم زراعة الأرض وتربية المواشي وبناء مجتمع مزدهر في هذا الوادي الجميل؟",
            minGpuTierEn = "Standard Mobile GPU",
            minGpuTierAr = "معالج رسومي هاتف قياسي",
            recommendedGpuTierEn = "Adreno 506 or higher",
            recommendedGpuTierAr = "Adreno 506 أو أعلى",
            playStorePackage = "com.chucklefish.stardewvalley",
            badgeLabelEn = "Relaxing Offline Gem",
            badgeLabelAr = "تحفة مريحة أوفلاين",
            isFeatured = false,
            isPopular = false,
            isLightweight = true,
            accentGradientStart = 0xFF606C38,
            accentGradientEnd = 0xFFDDA15E,
            taglineEn = "Peaceful country life on the go",
            taglineAr = "حياة ريفية ممتعة وهادئة في جيبك"
        ),
        Game(
            id = "dead_cells",
            name = "Dead Cells",
            developer = "Playdigious",
            categories = listOf(GameCategory.ACTION, GameCategory.ADVENTURE, GameCategory.OFFLINE),
            rating = 4.7f,
            downloadSizeFormatted = "~1.8 GB",
            storageRequiredBytes = 2_147_483_648L,
            minRamMb = 3072,
            recommendedRamMb = 4096,
            minAndroidVersion = 24,
            recommendedAndroidVersion = 29,
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = true,
            descriptionEn = "Roguevania action-platformer requiring mastery of frantic 2D combat with a wide variety of weapons and skills against merciless bosses.",
            descriptionAr = "لعبة روجفانيا حركية سريعة تتطلب إتقان القتال ثنائي الأبعاد بمجموعة واسعة من الأسلحة والمهارات ضد زعماء أذكياء وأشداء.",
            minGpuTierEn = "Mali-G72 / Adreno 512",
            minGpuTierAr = "Mali-G72 أو Adreno 512",
            recommendedGpuTierEn = "Snapdragon 765G or equivalent",
            recommendedGpuTierAr = "Snapdragon 765G أو ما يعادله",
            playStorePackage = "com.playdigious.deadcells.mobile",
            badgeLabelEn = "Hardcore Action",
            badgeLabelAr = "أكشن احترافي",
            isFeatured = false,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFF9E2A2B,
            accentGradientEnd = 0xFFFF5400,
            taglineEn = "Kill, die, learn, repeat",
            taglineAr = "قاتل، تعلّم، وكرر المحاولة بإتقان"
        ),
        Game(
            id = "real_racing_3",
            name = "Real Racing 3",
            developer = "Electronic Arts",
            categories = listOf(GameCategory.RACING, GameCategory.OFFLINE),
            rating = 4.3f,
            downloadSizeFormatted = "~3.2 GB",
            storageRequiredBytes = 3_758_096_384L,
            minRamMb = 2048,
            recommendedRamMb = 3072,
            minAndroidVersion = 21,
            recommendedAndroidVersion = 28,
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Features officially licensed tracks with 40 circuits at 19 real-world locations, a 43-car grid, and over 250 meticulously detailed cars from Porsche, Bugatti, and more.",
            descriptionAr = "حلبات سباق مرخصة رسمياً في 19 موقعاً حقيقياً، وشبكة تضم 43 سيارة وأكثر من 250 سيارة فائقة الدقة من بورش وبوجاتي وفيراري.",
            minGpuTierEn = "Adreno 405 / Mali-T760",
            minGpuTierAr = "Adreno 405 أو Mali-T760",
            recommendedGpuTierEn = "Adreno 618 or higher",
            recommendedGpuTierAr = "Adreno 618 أو أعلى",
            playStorePackage = "com.ea.games.r3_row",
            badgeLabelEn = "Sim Motorsport",
            badgeLabelAr = "محاكاة واقعية",
            isFeatured = false,
            isPopular = false,
            isLightweight = false,
            accentGradientStart = 0xFF14213D,
            accentGradientEnd = 0xFFFCA311,
            taglineEn = "Realistic motorsports on official tracks",
            taglineAr = "سباقات سيارات واقعية على حلبات رسمية"
        ),
        Game(
            id = "free_fire_max",
            name = "Free Fire MAX",
            developer = "Garena International I",
            categories = listOf(GameCategory.ACTION, GameCategory.MULTIPLAYER, GameCategory.LOW_END),
            rating = 4.2f,
            downloadSizeFormatted = "~2.2 GB",
            storageRequiredBytes = 2_684_354_560L,
            minRamMb = 2048,
            recommendedRamMb = 4096,
            minAndroidVersion = 19, // Android 4.4+
            recommendedAndroidVersion = 28,
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = false,
            isOfflinePlayable = false,
            descriptionEn = "Designed exclusively to deliver premium Battle Royale gameplay with Firelink technology, Ultra HD resolutions, and breathtaking effects.",
            descriptionAr = "مصممة خصيصاً لتقديم تجربة باتل رويال محسّنة مع دعم دقة Ultra HD ومؤثرات صوتية وبصرية محسنة، ومتوافقة مع مختلف الهواتف.",
            minGpuTierEn = "Mali-400 / Adreno 306",
            minGpuTierAr = "Mali-400 أو Adreno 306",
            recommendedGpuTierEn = "Snapdragon 665 / Helio G85",
            recommendedGpuTierAr = "Snapdragon 665 أو Helio G85",
            playStorePackage = "com.dts.freefiremax",
            badgeLabelEn = "Fast Paced BR",
            badgeLabelAr = "إيقاع سريع",
            isFeatured = false,
            isPopular = true,
            isLightweight = true,
            accentGradientStart = 0xFFD00000,
            accentGradientEnd = 0xFFFFBA08,
            taglineEn = "High adrenaline 10-minute survival matches",
            taglineAr = "مباريات بقاء سريعة ومثيرة في 10 دقائق"
        ),
        Game(
            id = "altos_odyssey",
            name = "Alto's Odyssey",
            developer = "Noodlecake",
            categories = listOf(GameCategory.ADVENTURE, GameCategory.OFFLINE, GameCategory.LOW_END),
            rating = 4.5f,
            downloadSizeFormatted = "~140 MB",
            storageRequiredBytes = 167_772_160L,
            minRamMb = 1024,
            recommendedRamMb = 2048,
            minAndroidVersion = 22,
            recommendedAndroidVersion = 28,
            minCpuCores = 2,
            recommendedCpuCores = 4,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Soar above windswept dunes, traverse thrilling canyons, and explore hidden temple cities in a fantastical desert far away from home.",
            descriptionAr = "حلّق فوق الكثبان الرملية وعبر الأخاديد الشاهقة واستكشف المعابد المخفية في صحراء ساحرة بموسيقى هادئة وتأثيرات ضوئية خلابة.",
            minGpuTierEn = "Basic 2D/3D GPU",
            minGpuTierAr = "معالج رسومي بسيط",
            recommendedGpuTierEn = "Any standard modern GPU",
            recommendedGpuTierAr = "أي معالج رسومي حديث",
            playStorePackage = "com.noodlecake.altosodyssey",
            badgeLabelEn = "Artistic Journey",
            badgeLabelAr = "رحلة فنية ساحرة",
            isFeatured = false,
            isPopular = false,
            isLightweight = true,
            accentGradientStart = 0xFF7209B7,
            accentGradientEnd = 0xFFF72585,
            taglineEn = "Serene sandboarding through mystical dunes",
            taglineAr = "تزلج هادئ ومريح عبر رمال الصحراء الذهبية"
        ),
        Game(
            id = "honkai_star_rail",
            name = "Honkai: Star Rail",
            developer = "HoYoverse",
            categories = listOf(GameCategory.ADVENTURE, GameCategory.OPEN_WORLD, GameCategory.MULTIPLAYER),
            rating = 4.3f,
            downloadSizeFormatted = "~18 GB",
            storageRequiredBytes = 20_401_094_656L,
            minRamMb = 4096,
            recommendedRamMb = 6144,
            minAndroidVersion = 26,
            recommendedAndroidVersion = 31,
            minCpuCores = 8,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = false,
            descriptionEn = "Board the Astral Express and travel across distinct worlds in this cinematic space fantasy RPG with strategic turn-based tactical combat.",
            descriptionAr = "اصعد على متن قطار النجوم وتجول عبر عوالم فريدة في هذه المغامرة الفضائية السينمائية بنظام قتال تكتيكي استراتيجي مبهر.",
            minGpuTierEn = "Adreno 618 / Mali-G57",
            minGpuTierAr = "Adreno 618 أو Mali-G57",
            recommendedGpuTierEn = "Snapdragon 865 / Dimensity 8100",
            recommendedGpuTierAr = "Snapdragon 865 أو Dimensity 8100",
            playStorePackage = "com.HoYoverse.hkrpgoversea",
            badgeLabelEn = "Space Sci-Fi RPG",
            badgeLabelAr = "خيال علمي ملحمي",
            isFeatured = true,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFF1D3557,
            accentGradientEnd = 0xFF457B9D,
            taglineEn = "Embark on an interstellar fantasy odyssey",
            taglineAr = "انطلق في رحلة ملحمية بين الكواكب والنجوم"
        ),
        Game(
            id = "grid_autosport",
            name = "GRID Autosport",
            developer = "Feral Interactive",
            categories = listOf(GameCategory.RACING, GameCategory.OFFLINE),
            rating = 4.2f,
            downloadSizeFormatted = "~4.5 GB",
            storageRequiredBytes = 5_368_709_120L,
            minRamMb = 4096,
            recommendedRamMb = 6144,
            minAndroidVersion = 28,
            recommendedAndroidVersion = 31,
            minCpuCores = 8,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = true,
            descriptionEn = "Console-quality pro racing on Android. Compete across 100 cars and 100 circuits with fully offline single player and realistic damage simulation.",
            descriptionAr = "سباقات احترافية بجودة أجهزة الكونسول. تنافس بأكثر من 100 سيارة و100 حلبة مع لعب فردي كامل بدون إنترنت ومحاكاة أضرار دقيقة.",
            minGpuTierEn = "Adreno 630 / Mali-G76",
            minGpuTierAr = "Adreno 630 أو Mali-G76",
            recommendedGpuTierEn = "Snapdragon 888 / Exynos 2100",
            recommendedGpuTierAr = "Snapdragon 888 أو Exynos 2100",
            playStorePackage = "com.feralinteractive.gridas",
            badgeLabelEn = "Console Quality",
            badgeLabelAr = "جودة كونسول",
            isFeatured = false,
            isPopular = false,
            isLightweight = false,
            accentGradientStart = 0xFF000000,
            accentGradientEnd = 0xFFE63946,
            taglineEn = "Uncompromised AAA racing simulator",
            taglineAr = "محاكاة سباقات حقيقية بلا مساومة"
        ),
        Game(
            id = "shadow_fight_2",
            name = "Shadow Fight 2",
            developer = "NEKKI",
            categories = listOf(GameCategory.ACTION, GameCategory.OFFLINE, GameCategory.LOW_END),
            rating = 4.6f,
            downloadSizeFormatted = "~200 MB",
            storageRequiredBytes = 262_144_000L,
            minRamMb = 1024,
            recommendedRamMb = 2048,
            minAndroidVersion = 21,
            recommendedAndroidVersion = 26,
            minCpuCores = 2,
            recommendedCpuCores = 4,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Classic mix of RPG and classical fighting. Equip countless lethal weapons and rare armor sets, and crush demon bosses to close the Gate of Shadows.",
            descriptionAr = "مزيج كلاسيكي رائع بين ألعاب الآر بي جي والقتال الواقعي. تسلّح بأسلحة ودروع فريدة واهزم زعماء الشياطين لإغلاق بوابة الظلال.",
            minGpuTierEn = "Any standard GPU",
            minGpuTierAr = "أي معالج رسومي قياسي",
            recommendedGpuTierEn = "Entry-level smartphone GPU",
            recommendedGpuTierAr = "معالج رسومي للهواتف الاقتصادية",
            playStorePackage = "com.nekki.shadowfight",
            badgeLabelEn = "Martial Arts",
            badgeLabelAr = "فنون قتالية",
            isFeatured = false,
            isPopular = false,
            isLightweight = true,
            accentGradientStart = 0xFF240046,
            accentGradientEnd = 0xFF5A189A,
            taglineEn = "Master shadow martial arts anywhere",
            taglineAr = "أتقن فنون قتال الظل بدون إنترنت"
        ),
        Game(
            id = "wild_rift",
            name = "League of Legends: Wild Rift",
            developer = "Riot Games, Inc",
            categories = listOf(GameCategory.ACTION, GameCategory.MULTIPLAYER),
            rating = 4.2f,
            downloadSizeFormatted = "~4.5 GB",
            storageRequiredBytes = 5_905_580_032L,
            minRamMb = 2048,
            recommendedRamMb = 4096,
            minAndroidVersion = 22,
            recommendedAndroidVersion = 29,
            minCpuCores = 4,
            recommendedCpuCores = 8,
            requires64Bit = false,
            isOfflinePlayable = false,
            descriptionEn = "The skill-and-strategy 5v5 MOBA experience of LoL built from the ground up for mobile with smooth dual-stick controls and snappy matches.",
            descriptionAr = "تجربة موبا الاستراتيجية 5 ضد 5 الشهيرة المصممة خصيصاً للهاتف مع تحكم سلس باللمس ومباريات سريعة وحماسية.",
            minGpuTierEn = "Adreno 306 / Mali-T628",
            minGpuTierAr = "Adreno 306 أو Mali-T628",
            recommendedGpuTierEn = "Snapdragon 675 / Helio G90T",
            recommendedGpuTierAr = "Snapdragon 675 أو Helio G90T",
            playStorePackage = "com.riotgames.league.wildrift",
            badgeLabelEn = "Strategic MOBA",
            badgeLabelAr = "موبا استراتيجية",
            isFeatured = false,
            isPopular = true,
            isLightweight = false,
            accentGradientStart = 0xFF003566,
            accentGradientEnd = 0xFFFFC300,
            taglineEn = "Team up with friends for 5v5 action",
            taglineAr = "شكل فريقك وتنافس في مباريات 5 ضد 5"
        ),
        Game(
            id = "monument_valley_2",
            name = "Monument Valley 2",
            developer = "ustwo games",
            categories = listOf(GameCategory.ADVENTURE, GameCategory.OFFLINE, GameCategory.LOW_END),
            rating = 4.8f,
            downloadSizeFormatted = "~320 MB",
            storageRequiredBytes = 419_430_400L,
            minRamMb = 1536,
            recommendedRamMb = 2048,
            minAndroidVersion = 21,
            recommendedAndroidVersion = 28,
            minCpuCores = 2,
            recommendedCpuCores = 4,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Guide a mother and her child as they embark on a journey through illusory architecture, discovering illusory pathways and delightful puzzles.",
            descriptionAr = "وجّه أماً وطفلتها في رحلة ساحرة عبر عمارة سريالية مستحيلة، واكتشف ممرات وهمية وألغازاً بصرية تبعث على الاسترخاء.",
            minGpuTierEn = "Standard Mobile GPU",
            minGpuTierAr = "معالج رسومي قياسي",
            recommendedGpuTierEn = "Modern Octa-Core",
            recommendedGpuTierAr = "معالج حديث متعدد الأنوية",
            playStorePackage = "com.ustwo.monumentvalley2",
            badgeLabelEn = "Mind-Bending Puzzle",
            badgeLabelAr = "ألغاز هندسية",
            isFeatured = false,
            isPopular = false,
            isLightweight = true,
            accentGradientStart = 0xFF2A9D8F,
            accentGradientEnd = 0xFFE76F51,
            taglineEn = "A stunning journey through illusory structures",
            taglineAr = "رحلة خلابة عبر أبنية خيالية وألغاز هندسية"
        ),
        Game(
            id = "carx_street",
            name = "CarX Street",
            developer = "CarX Technologies, LLC",
            categories = listOf(GameCategory.RACING, GameCategory.OPEN_WORLD, GameCategory.MULTIPLAYER),
            rating = 4.1f,
            downloadSizeFormatted = "~5.8 GB",
            storageRequiredBytes = 7_516_192_768L,
            minRamMb = 4096,
            recommendedRamMb = 6144,
            minAndroidVersion = 28,
            recommendedAndroidVersion = 31,
            minCpuCores = 8,
            recommendedCpuCores = 8,
            requires64Bit = true,
            isOfflinePlayable = false,
            descriptionEn = "Dynamic open world street racing with realistic car physics, deep mechanical tuning, drift battles, and highway night races.",
            descriptionAr = "سباقات شوارع حرة في عالم مفتوح ضخم مع فيزياء سيارات واقعية وتعديل تفصيلي للمحركات وسباقات درفت ليلية حماسية.",
            minGpuTierEn = "Mali-G77 / Adreno 640",
            minGpuTierAr = "Mali-G77 أو Adreno 640",
            recommendedGpuTierEn = "Snapdragon 8 Gen 1 / Dimensity 9000",
            recommendedGpuTierAr = "Snapdragon 8 Gen 1 أو Dimensity 9000",
            playStorePackage = "com.carxtech.sr",
            badgeLabelEn = "Open World Racing",
            badgeLabelAr = "عالم مفتوح للسيارات",
            isFeatured = false,
            isPopular = false,
            isLightweight = false,
            accentGradientStart = 0xFF3D0066,
            accentGradientEnd = 0xFFFF0055,
            taglineEn = "High speed street racing and drift physics",
            taglineAr = "سباقات شوارع سريعة ومحاكاة تفحيط متقدمة"
        ),
        Game(
            id = "candy_crush",
            name = "Candy Crush Saga",
            developer = "King",
            categories = listOf(GameCategory.OFFLINE, GameCategory.LOW_END),
            rating = 4.6f,
            downloadSizeFormatted = "~160 MB",
            storageRequiredBytes = 209_715_200L,
            minRamMb = 1024,
            recommendedRamMb = 2048,
            minAndroidVersion = 21,
            recommendedAndroidVersion = 26,
            minCpuCores = 2,
            recommendedCpuCores = 4,
            requires64Bit = false,
            isOfflinePlayable = true,
            descriptionEn = "Match candies in thousands of puzzle levels in the world-famous matching game. Play offline anywhere anytime with zero performance load.",
            descriptionAr = "طابق الحلوى الملونة في آلاف المستويات في اللعبة الشهيرة عالمياً. تعمل بسلاسة فائقة وبدون اتصال بالإنترنت في أي مكان.",
            minGpuTierEn = "Any basic graphics chip",
            minGpuTierAr = "أي معالج رسومي أساسي",
            recommendedGpuTierEn = "Standard Android chip",
            recommendedGpuTierAr = "معالج أندرويد قياسي",
            playStorePackage = "com.king.candycrushsaga",
            badgeLabelEn = "Casual Classic",
            badgeLabelAr = "كلاسيكية خفيفة",
            isFeatured = false,
            isPopular = true,
            isLightweight = true,
            accentGradientStart = 0xFFFF4D6D,
            accentGradientEnd = 0xFFFFB703,
            taglineEn = "Relaxing sweet puzzles on any phone",
            taglineAr = "ألغاز حلوى مسلية ومريحة تناسب كل الأجهزة"
        )
    )

    // Flow of games directly from local SQLite Room database (persisted offline)
    val gamesStream: Flow<List<Game>> = gameDao.getAllGames().map { entities ->
        if (entities.isEmpty()) {
            defaultInitialGames
        } else {
            entities.map { it.toDomainModel() }
        }
    }

    val syncMetadataStream: Flow<SyncMetadataEntity?> = syncMetadataDao.getSyncMetadata()

    val favorites: Flow<List<FavoriteGameEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(gameId: String): Flow<Boolean> = favoriteDao.isFavorite(gameId)

    suspend fun toggleFavorite(gameId: String, currentIsFavorite: Boolean) {
        if (currentIsFavorite) {
            favoriteDao.removeFavorite(gameId)
        } else {
            favoriteDao.addFavorite(FavoriteGameEntity(gameId = gameId))
        }
    }

    suspend fun initializeOfflineCacheIfNeeded() = withContext(Dispatchers.IO) {
        val existingCount = gameDao.getGamesCount()
        if (existingCount == 0) {
            val entities = defaultInitialGames.map { GameEntity.fromDomainModel(it) }
            gameDao.insertOrUpdateGames(entities)
            syncMetadataDao.setSyncMetadata(
                SyncMetadataEntity(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    syncStatus = "OFFLINE_INITIALIZED",
                    itemsCount = entities.size
                )
            )
        }
    }

    suspend fun syncOnline(force: Boolean = false) = withContext(Dispatchers.IO) {
        // Ensure initial offline cache exists
        initializeOfflineCacheIfNeeded()

        val existingMeta = syncMetadataDao.getSyncMetadata().firstOrNull()
        val now = System.currentTimeMillis()
        val fifteenMinutes = 15 * 60 * 1000L

        // Do not spam downloads repeatedly unless forced or 15m passed
        if (!force && existingMeta != null && (now - existingMeta.lastSyncTimestamp) < fifteenMinutes && existingMeta.syncStatus == "SUCCESS") {
            val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.US).format(Date(existingMeta.lastSyncTimestamp))
            _syncState.value = SyncState.Success("Database is up to date.", dateStr, 0)
            return@withContext
        }

        val updatedEntities = syncManager.performSync(force = force) { state ->
            _syncState.value = state
        }

        if (updatedEntities != null && updatedEntities.isNotEmpty()) {
            gameDao.insertOrUpdateGames(updatedEntities)
            val totalCount = gameDao.getGamesCount()
            syncMetadataDao.setSyncMetadata(
                SyncMetadataEntity(
                    lastSyncTimestamp = now,
                    syncStatus = "SUCCESS",
                    itemsCount = totalCount
                )
            )
        } else if (_syncState.value is SyncState.Error || _syncState.value is SyncState.Offline) {
            // Keep existing offline data untouched, only update status
            syncMetadataDao.setSyncMetadata(
                SyncMetadataEntity(
                    lastSyncTimestamp = existingMeta?.lastSyncTimestamp ?: now,
                    syncStatus = if (_syncState.value is SyncState.Offline) "OFFLINE" else "FAILED",
                    itemsCount = gameDao.getGamesCount(),
                    lastErrorMessage = (_syncState.value as? SyncState.Error)?.message
                )
            )
        }
    }
}
