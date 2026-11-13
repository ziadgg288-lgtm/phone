package com.example.util

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val layoutDirection: LayoutDirection) {
    ENGLISH("en", "English", LayoutDirection.Ltr),
    ARABIC("ar", "العربية (RTL)", LayoutDirection.Rtl)
}

object Strings {
    // Navigation
    fun navHome(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الرئيسية" else "Home"
    fun navLibrary(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المكتبة" else "Library"
    fun navAnalyzer(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "فاحص الهاتف" else "Analyzer"
    fun navFavorites(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المفضلة" else "Favorites"
    fun navSettings(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإعدادات" else "Settings"

    // Home
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مستكشف الألعاب" else "Game Finder"
    fun appSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اعرف الألعاب المتوافقة مع جهازك" else "Find games tailored to your phone"
    fun yourPhoneSpecs(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عتاد هاتفك" else "Your Phone Specs"
    fun tapToInspect(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اضغط للتحليل الشامل" else "Tap for full hardware report"
    fun featuredGames(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ألعاب مميزة" else "Featured Games"
    fun popularGames(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الأكثر شهرة" else "Popular Games"
    fun lightweightGames(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ألعاب خفيفة (للهواتف الاقتصادية)" else "Lightweight Games"
    fun offlineGames(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ألعاب تعمل بدون إنترنت" else "Offline Playable"
    fun seeAll(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عرض الكل" else "See All"

    // Compatibility Badges
    fun excellent(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "توافق ممتاز" else "Excellent"
    fun possible(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "توافق محتمل" else "Possible"
    fun notRecommended(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "غير موصى به" else "Not Recommended"

    // Analyzer
    fun phoneAnalyzer(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحليل مواصفات الجهاز" else "Phone Analyzer"
    fun analyzerDescription(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "قراءة بيانات العتاد الفعلية من نظام أندرويد دون أي أرقام وهمية." else "Live hardware inspection read directly from Android APIs."
    fun gamingTier(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تصنيف الهاتف للألعاب" else "Gaming Hardware Tier"
    fun hardwareDetails(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تفاصيل العتاد" else "Hardware Breakdown"
    fun deviceModel(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "طراز الجهاز" else "Device Model"
    fun androidVersion(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "نظام أندرويد" else "Android OS"
    fun ramMemory(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الذاكرة العشوائية (RAM)" else "RAM Memory"
    fun ramUsage(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المستخدم من الرام" else "RAM in Use"
    fun storageSpace(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المساحة التخزينية" else "Internal Storage"
    fun storageFree(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المساحة المتوفرة" else "Free Space"
    fun cpuProcessors(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المعالج والأنوية" else "CPU Processor"
    fun architecture(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المعمارية" else "Architecture"
    fun graphicsEngine(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "محرك الرسوميات" else "Graphics Engine"
    fun securityPatch(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحديث الأمان" else "Security Patch"
    fun refreshSpecs(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحديث فحص المواصفات" else "Refresh Hardware Telemetry"

    // Library & Search
    fun searchPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ابحث عن لعبة أو فئة..." else "Search games by name or genre..."
    fun filterCategory(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "التصنيف" else "Category"
    fun filterCompatibility(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مستوى التوافق" else "Compatibility"
    fun filterAll(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الكل" else "All"
    fun filterExcellentOnly(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ممتاز فقط" else "Excellent Only"
    fun filterPlayable(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "قابل للتشغيل" else "Playable"
    fun filterOfflineOnly(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "أوفلاين فقط" else "Offline Only"
    fun noGamesFound(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لم يتم العثور على ألعاب مطابقة." else "No games match your search criteria."
    fun clearFilters(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إعادة ضبط التصفية" else "Clear Filters"

    // Game Details
    fun compatibilityAnalysis(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحليل التوافق مع هاتفك" else "Compatibility Analysis"
    fun specComparison(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مقارنة مواصفات هاتفك بالمتطلبات" else "Device vs Requirements"
    fun specItem(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المكون" else "Component"
    fun yourPhone(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "هاتفك" else "Your Phone"
    fun minimumReq(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الحد الأدنى" else "Minimum"
    fun recommendedReq(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الموصى به" else "Recommended"
    fun internetStatus(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الاتصال بالإنترنت" else "Network Requirement"
    fun requiresInternet(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "يتطلب اتصال دائم بالإنترنت" else "Requires active internet"
    fun offlinePlayable(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "يمكن اللعب بدون إنترنت (أوفلاين)" else "Playable offline without internet"
    fun downloadOnPlayStore(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "صفحة التحميل الرسمية (Google Play)" else "Official Store Page (Google Play)"
    fun addToFavorites(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إضافة للمفضلة" else "Save to Favorites"
    fun removeFromFavorites(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إزالة من المفضلة" else "Remove Favorite"
    fun officialReqNotes(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "متطلبات التشغيل الرسمية المنشورة" else "Published Official Specifications"
    fun gpuRequirements(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "كارت الشاشة والرسوميات" else "GPU / Chipset Target"
    fun fpsBenchmarkNotice(lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "تنويه معايير الأداء: لا نزعم معدل إطارات (FPS) دقيق بدون بيانات اختبار معيارية فعلية. يعتمد الأداء الحقيقي على إعدادات الرسوميات ودرجة حرارة الهاتف."
    else
        "Benchmark Notice: Never claim exact FPS without verified benchmark testing. Actual real-world performance depends on chosen resolution, shaders, and thermal throttling."

    // Favorites
    fun noFavoritesTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لا توجد ألعاب مفضلة حتى الآن" else "No Favorite Games Yet"
    fun noFavoritesSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تصفح المكتبة واضغط على أيقونة القلب لحفظ ألعابك المفضلة محلياً." else "Browse the catalog and tap the heart icon on any game to save it locally."
    fun exploreLibrary(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استكشف الألعاب" else "Explore Library"

    // Settings
    fun settings(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإعدادات" else "Settings"
    fun languageSelection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لغة التطبيق" else "App Language"
    fun appTheme(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المظهر والتصميم" else "Appearance"
    fun darkThemeActive(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الوضع الداكن الاحترافي للألعاب (نشط)" else "Pro Gaming Dark Theme (Active)"
    fun sampleDataNotice(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "شفافية البيانات التجريبية" else "Sample Data Transparency"
    fun sampleDataNoticeBody(lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "يتضمن التطبيق مكتبة ألعاب تجريبية مصنفة بدقة ومعلومات موثوقة مأخوذة من المتطلبات الرسمية المنشورة للمطورين وحزم Google Play الرسمية."
    else
        "This app includes sample games clearly labeled as sample data with verified official publisher requirements and Google Play package IDs."
    fun aboutApp(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عن تطبيق مستكشف الألعاب" else "About Game Finder"
    fun aboutAppBody(lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "مستكشف الألعاب هو تطبيق أندرويد أصلي يساعد اللاعبين على معرفة توافق الألعاب مع عتاد هواتفهم بدقة واحترافية."
    else
        "Game Finder is a native Android application designed to help gamers discover which mobile titles run smoothly on their specific device hardware."
    fun version(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإصدار 1.1.0" else "Version 1.1.0"

    // Online & Offline Mode / Smart Sync
    fun onlineMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "وضع الاتصال (أونلاين)" else "Online Mode"
    fun offlineMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "وضع عدم الاتصال (أوفلاين)" else "Offline Mode"
    fun syncStatusOnline(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "متصل • قاعدة البيانات محدثة" else "Online • Catalog Synced"
    fun syncStatusOffline(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "أوفلاين • استخدام البيانات المحلية" else "Offline • Local Cache Active"
    fun syncStatusSyncing(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "جارٍ مزامنة وتحديث الألعاب..." else "Syncing latest game data..."
    fun syncNow(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحديث قاعدة البيانات" else "Update Database"
    fun lastUpdatedPrefix(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "آخر تحديث: " else "Last updated: "
    fun newlyReleased(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إصدار حديث" else "Newly Released"
    fun newGamesSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ألعاب صدرت حديثاً" else "Newly Released Games"
    fun smartSyncTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المزامنة الذكية والوضع غير المتصل" else "Smart Sync & Offline Mode"
    fun smartSyncBody(lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "يقوم التطبيق بالعمل بكفاءة تامة بدون اتصال، مع مزامنة أحدث الألعاب تلقائياً فور توفر الإنترنت والحفاظ على البيانات المحلية عند انقطاعه."
    else
        "The app functions seamlessly offline. When internet is detected, it automatically checks for new game releases and updated specifications while preserving local records."
}
