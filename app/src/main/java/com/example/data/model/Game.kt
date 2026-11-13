package com.example.data.model

data class Game(
    val id: String,
    val name: String,
    val developer: String,
    val categories: List<GameCategory>,
    val rating: Float,
    val downloadSizeFormatted: String,
    val storageRequiredBytes: Long,
    val minRamMb: Int,
    val recommendedRamMb: Int,
    val minAndroidVersion: Int,
    val recommendedAndroidVersion: Int,
    val minCpuCores: Int,
    val recommendedCpuCores: Int,
    val requires64Bit: Boolean,
    val isOfflinePlayable: Boolean,
    val descriptionEn: String,
    val descriptionAr: String,
    val minGpuTierEn: String,
    val minGpuTierAr: String,
    val recommendedGpuTierEn: String,
    val recommendedGpuTierAr: String,
    val playStorePackage: String,
    val badgeLabelEn: String? = null,
    val badgeLabelAr: String? = null,
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    val isLightweight: Boolean = false,
    val isNewlyReleased: Boolean = false,
    val releaseDate: String = "",
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val accentGradientStart: Long = 0xFF8A3FFC,
    val accentGradientEnd: Long = 0xFF00D2FF,
    val taglineEn: String = "",
    val taglineAr: String = ""
) {
    val minRamGb: Float
        get() = minRamMb / 1024f

    val recommendedRamGb: Float
        get() = recommendedRamMb / 1024f

    val storageRequiredGb: Float
        get() = storageRequiredBytes / (1024f * 1024f * 1024f)
}
