package com.example.data.model

enum class GameCategory(val titleEn: String, val titleAr: String) {
    ALL("All Games", "جميع الألعاب"),
    ACTION("Action", "أكشن"),
    ADVENTURE("Adventure", "مغامرات"),
    RACING("Racing", "سباقات"),
    OPEN_WORLD("Open World", "عالم مفتوح"),
    OFFLINE("Offline", "بدون إنترنت"),
    MULTIPLAYER("Multiplayer", "متعدد اللاعبين"),
    LOW_END("Low-End Phones", "للهواتف الضعيفة")
}
