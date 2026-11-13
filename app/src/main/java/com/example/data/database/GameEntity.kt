package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Game
import com.example.data.model.GameCategory

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val developer: String,
    val categoriesCsv: String,
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
    fun toDomainModel(): Game {
        val parsedCategories = categoriesCsv.split(",")
            .mapNotNull { catName ->
                try {
                    GameCategory.valueOf(catName.trim())
                } catch (_: Exception) {
                    null
                }
            }

        return Game(
            id = id,
            name = name,
            developer = developer,
            categories = if (parsedCategories.isNotEmpty()) parsedCategories else listOf(GameCategory.ACTION),
            rating = rating,
            downloadSizeFormatted = downloadSizeFormatted,
            storageRequiredBytes = storageRequiredBytes,
            minRamMb = minRamMb,
            recommendedRamMb = recommendedRamMb,
            minAndroidVersion = minAndroidVersion,
            recommendedAndroidVersion = recommendedAndroidVersion,
            minCpuCores = minCpuCores,
            recommendedCpuCores = recommendedCpuCores,
            requires64Bit = requires64Bit,
            isOfflinePlayable = isOfflinePlayable,
            descriptionEn = descriptionEn,
            descriptionAr = descriptionAr,
            minGpuTierEn = minGpuTierEn,
            minGpuTierAr = minGpuTierAr,
            recommendedGpuTierEn = recommendedGpuTierEn,
            recommendedGpuTierAr = recommendedGpuTierAr,
            playStorePackage = playStorePackage,
            badgeLabelEn = badgeLabelEn,
            badgeLabelAr = badgeLabelAr,
            isFeatured = isFeatured,
            isPopular = isPopular,
            isLightweight = isLightweight,
            isNewlyReleased = isNewlyReleased,
            releaseDate = releaseDate,
            lastUpdatedTimestamp = lastUpdatedTimestamp,
            accentGradientStart = accentGradientStart,
            accentGradientEnd = accentGradientEnd,
            taglineEn = taglineEn,
            taglineAr = taglineAr
        )
    }

    companion object {
        fun fromDomainModel(game: Game): GameEntity {
            return GameEntity(
                id = game.id,
                name = game.name,
                developer = game.developer,
                categoriesCsv = game.categories.joinToString(",") { it.name },
                rating = game.rating,
                downloadSizeFormatted = game.downloadSizeFormatted,
                storageRequiredBytes = game.storageRequiredBytes,
                minRamMb = game.minRamMb,
                recommendedRamMb = game.recommendedRamMb,
                minAndroidVersion = game.minAndroidVersion,
                recommendedAndroidVersion = game.recommendedAndroidVersion,
                minCpuCores = game.minCpuCores,
                recommendedCpuCores = game.recommendedCpuCores,
                requires64Bit = game.requires64Bit,
                isOfflinePlayable = game.isOfflinePlayable,
                descriptionEn = game.descriptionEn,
                descriptionAr = game.descriptionAr,
                minGpuTierEn = game.minGpuTierEn,
                minGpuTierAr = game.minGpuTierAr,
                recommendedGpuTierEn = game.recommendedGpuTierEn,
                recommendedGpuTierAr = game.recommendedGpuTierAr,
                playStorePackage = game.playStorePackage,
                badgeLabelEn = game.badgeLabelEn,
                badgeLabelAr = game.badgeLabelAr,
                isFeatured = game.isFeatured,
                isPopular = game.isPopular,
                isLightweight = game.isLightweight,
                isNewlyReleased = game.isNewlyReleased,
                releaseDate = game.releaseDate,
                lastUpdatedTimestamp = game.lastUpdatedTimestamp,
                accentGradientStart = game.accentGradientStart,
                accentGradientEnd = game.accentGradientEnd,
                taglineEn = game.taglineEn,
                taglineAr = game.taglineAr
            )
        }
    }
}
