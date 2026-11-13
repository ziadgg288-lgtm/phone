package com.example.data.model

enum class CompatibilityLevel(
    val titleEn: String,
    val titleAr: String,
    val summaryEn: String,
    val summaryAr: String
) {
    EXCELLENT(
        "Excellent Compatibility",
        "توافق ممتاز",
        "Your device meets or exceeds the game's recommended specifications. Expected to deliver smooth gameplay.",
        "جهازك يلبي أو يتجاوز متطلبات التشغيل الموصى بها. يتوقع تشغيل سلس وتجربة ممتعة."
    ),
    POSSIBLE(
        "Possible Compatibility",
        "توافق محتمل",
        "Your device meets minimum requirements, but falls below recommended specs. Playable on low-to-medium settings.",
        "جهازك يلبي الحد الأدنى، لكنه أقل من الموصى به. يمكنك اللعب بإعدادات متوسطة أو منخفضة."
    ),
    NOT_RECOMMENDED(
        "Not Recommended",
        "غير موصى به",
        "Your device does not meet the minimum requirements (RAM, Android version, storage, or CPU). May crash or lag severely.",
        "جهازك لا يلبي الحد الأدنى من المتطلبات (الذاكرة، إصدار أندرويد، أو المساحة). قد تواجه بطئاً أو توقفاً مفاجئاً."
    )
}

enum class SpecStatus {
    EXCEEDS_RECOMMENDED,
    MEETS_RECOMMENDED,
    MEETS_MINIMUM,
    BELOW_MINIMUM
}

data class SpecItemComparison(
    val nameEn: String,
    val nameAr: String,
    val deviceValue: String,
    val minValue: String,
    val recommendedValue: String,
    val status: SpecStatus,
    val messageEn: String,
    val messageAr: String
)

data class CompatibilityResult(
    val level: CompatibilityLevel,
    val score: Int, // 0 - 100
    val ramCheck: SpecItemComparison,
    val storageCheck: SpecItemComparison,
    val osCheck: SpecItemComparison,
    val cpuCheck: SpecItemComparison,
    val architectureCheck: SpecItemComparison,
    val adviceEn: String,
    val adviceAr: String
)
