package com.example.data.model

import java.util.Locale

enum class DevicePerformanceTier(
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String
) {
    ENTRY_LEVEL(
        "Entry-Level Gamer",
        "مستوى أساسي",
        "Best suited for casual 2D and lightweight 3D games.",
        "مناسب للألعاب الخفيفة وثنائية الأبعاد."
    ),
    MID_RANGE(
        "Mid-Range Gaming",
        "فئة متوسطة للألعاب",
        "Capable of running most popular 3D games with medium settings.",
        "قادر على تشغيل معظم الألعاب ثلاثية الأبعاد بإعدادات متوسطة."
    ),
    HIGH_PERFORMANCE(
        "High Performance",
        "أداء عالي للألعاب",
        "Handles demanding 3D and competitive open-world games smoothly.",
        "يشغل الألعاب الثقيلة والعوالم المفتوحة بسلاسة ممتازة."
    ),
    FLAGSHIP_GAMING(
        "Flagship Gaming Beast",
        "فئة النخبة الجبارة",
        "Ready for top-tier graphics, high refresh rates, and heavy shaders.",
        "جاهز لأعلى دقة رسومية، وأعلى معدل إطارات وعوالم متقدمة."
    )
}

data class DeviceInfo(
    val model: String,
    val manufacturer: String,
    val brand: String,
    val board: String,
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val totalStorageBytes: Long,
    val availableStorageBytes: Long,
    val cpuCores: Int,
    val cpuArch: String,
    val is64Bit: Boolean,
    val glEsVersion: String,
    val socModel: String
) {
    val totalRamMb: Int
        get() = (totalRamBytes / (1024 * 1024)).toInt()

    val availableRamMb: Int
        get() = (availableRamBytes / (1024 * 1024)).toInt()

    val totalRamGb: Float
        get() = totalRamBytes / (1024f * 1024f * 1024f)

    val availableRamGb: Float
        get() = availableRamBytes / (1024f * 1024f * 1024f)

    val totalStorageGb: Float
        get() = totalStorageBytes / (1024f * 1024f * 1024f)

    val availableStorageGb: Float
        get() = availableStorageBytes / (1024f * 1024f * 1024f)

    val ramUsagePercent: Int
        get() {
            if (totalRamBytes <= 0) return 0
            val used = totalRamBytes - availableRamBytes
            return ((used.toDouble() / totalRamBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
        }

    val storageUsagePercent: Int
        get() {
            if (totalStorageBytes <= 0) return 0
            val used = totalStorageBytes - availableStorageBytes
            return ((used.toDouble() / totalStorageBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
        }

    val formattedModel: String
        get() = "${manufacturer.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} $model".trim()

    val performanceTier: DevicePerformanceTier
        get() {
            val ramGb = totalRamGb
            return when {
                ramGb >= 7.5f && cpuCores >= 8 && is64Bit -> DevicePerformanceTier.FLAGSHIP_GAMING
                ramGb >= 5.5f && cpuCores >= 8 -> DevicePerformanceTier.HIGH_PERFORMANCE
                ramGb >= 3.5f && cpuCores >= 6 -> DevicePerformanceTier.MID_RANGE
                else -> DevicePerformanceTier.ENTRY_LEVEL
            }
        }
}
