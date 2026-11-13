package com.example.data.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.example.data.model.CompatibilityLevel
import com.example.data.model.CompatibilityResult
import com.example.data.model.DeviceInfo
import com.example.data.model.Game
import com.example.data.model.SpecItemComparison
import com.example.data.model.SpecStatus
import java.util.Locale

class DeviceAnalyzer(private val context: Context) {

    fun getDeviceInfo(): DeviceInfo {
        // Real ActivityManager memory stats
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)

        val totalRam = if (memoryInfo.totalMem > 0) memoryInfo.totalMem else 4L * 1024 * 1024 * 1024
        val availRam = if (memoryInfo.availMem > 0) memoryInfo.availMem else (totalRam * 0.4).toLong()

        // Real Storage stats via StatFs
        var totalStorage = 64L * 1024 * 1024 * 1024
        var availStorage = 20L * 1024 * 1024 * 1024
        try {
            val statFs = StatFs(Environment.getDataDirectory().path)
            val blocks = statFs.blockCountLong * statFs.blockSizeLong
            val avail = statFs.availableBlocksLong * statFs.blockSizeLong
            if (blocks > 0) {
                totalStorage = blocks
                availStorage = if (avail > 0) avail else (blocks * 0.3).toLong()
            }
        } catch (_: Exception) {
            // Safe fallback
        }

        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        val supportedAbis = Build.SUPPORTED_ABIS ?: arrayOf("armeabi-v7a")
        val is64Bit = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()
        val archString = supportedAbis.joinToString(", ")

        val glEsVersion = try {
            activityManager?.deviceConfigurationInfo?.glEsVersion ?: "3.0"
        } catch (_: Exception) {
            "3.0"
        }

        val soc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                Build.SOC_MODEL
            } catch (_: Exception) {
                Build.HARDWARE ?: "Standard"
            }
        } else {
            Build.HARDWARE ?: "Standard"
        }

        val secPatch = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A"
        } catch (_: Exception) {
            "N/A"
        }

        return DeviceInfo(
            model = Build.MODEL ?: "Android Device",
            manufacturer = Build.MANUFACTURER ?: "Generic",
            brand = Build.BRAND ?: "Generic",
            board = Build.BOARD ?: "Unknown",
            androidVersion = Build.VERSION.RELEASE ?: "14",
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = secPatch,
            totalRamBytes = totalRam,
            availableRamBytes = availRam,
            totalStorageBytes = totalStorage,
            availableStorageBytes = availStorage,
            cpuCores = cores,
            cpuArch = archString,
            is64Bit = is64Bit,
            glEsVersion = glEsVersion,
            socModel = soc.ifBlank { "Standard" }
        )
    }

    fun evaluateCompatibility(device: DeviceInfo, game: Game): CompatibilityResult {
        // 1. RAM Check
        val deviceRamMb = device.totalRamMb
        val ramStatus = when {
            deviceRamMb >= (game.recommendedRamMb * 1.25).toInt() -> SpecStatus.EXCEEDS_RECOMMENDED
            deviceRamMb >= game.recommendedRamMb -> SpecStatus.MEETS_RECOMMENDED
            deviceRamMb >= game.minRamMb -> SpecStatus.MEETS_MINIMUM
            else -> SpecStatus.BELOW_MINIMUM
        }
        val ramComparison = SpecItemComparison(
            nameEn = "Memory (RAM)",
            nameAr = "الذاكرة العشوائية (RAM)",
            deviceValue = String.format(Locale.US, "%.1f GB", device.totalRamGb),
            minValue = String.format(Locale.US, "%.1f GB", game.minRamGb),
            recommendedValue = String.format(Locale.US, "%.1f GB", game.recommendedRamGb),
            status = ramStatus,
            messageEn = when (ramStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED -> "Excellent RAM headroom for multitasking and high textures."
                SpecStatus.MEETS_RECOMMENDED -> "Meets recommended RAM for smooth gameplay."
                SpecStatus.MEETS_MINIMUM -> "Meets minimum RAM. Background apps should be closed."
                SpecStatus.BELOW_MINIMUM -> "Insufficient RAM. May result in frequent game crashes."
            },
            messageAr = when (ramStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED -> "سعة ذاكرة ممتازة تتيح تشغيل أعلى جودة وتعدد مهام مريح."
                SpecStatus.MEETS_RECOMMENDED -> "تلبي الذاكرة الموصى بها لتجربة لعب سلسة."
                SpecStatus.MEETS_MINIMUM -> "تلبي الحد الأدنى. يُفضل إغلاق التطبيقات الأخرى أثناء اللعب."
                SpecStatus.BELOW_MINIMUM -> "سعة الذاكرة غير كافية، مما قد يؤدي لإغلاق اللعبة قسرياً."
            }
        )

        // 2. Storage Check
        val freeStorageBytes = device.availableStorageBytes
        val neededBytes = game.storageRequiredBytes
        val storageStatus = when {
            freeStorageBytes >= (neededBytes * 2.0).toLong() -> SpecStatus.EXCEEDS_RECOMMENDED
            freeStorageBytes >= (neededBytes * 1.3).toLong() -> SpecStatus.MEETS_RECOMMENDED
            freeStorageBytes >= neededBytes -> SpecStatus.MEETS_MINIMUM
            else -> SpecStatus.BELOW_MINIMUM
        }
        val storageComparison = SpecItemComparison(
            nameEn = "Free Storage",
            nameAr = "المساحة التخزينية الحرة",
            deviceValue = String.format(Locale.US, "%.1f GB", device.availableStorageGb),
            minValue = String.format(Locale.US, "%.1f GB", game.storageRequiredGb),
            recommendedValue = String.format(Locale.US, "%.1f GB", game.storageRequiredGb * 1.3f),
            status = storageStatus,
            messageEn = when (storageStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED, SpecStatus.MEETS_RECOMMENDED -> "Plenty of storage available for updates and shaders."
                SpecStatus.MEETS_MINIMUM -> "Just enough space for installation; consider freeing more."
                SpecStatus.BELOW_MINIMUM -> "Insufficient free space. Please free up space before installing."
            },
            messageAr = when (storageStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED, SpecStatus.MEETS_RECOMMENDED -> "مساحة حرة ممتازة تكفي اللعبة والملفات الإضافية والتحديثات."
                SpecStatus.MEETS_MINIMUM -> "المساحة تكفي بالكاد لتثبيت اللعبة، يُنصح بتوفير مساحة إضافية."
                SpecStatus.BELOW_MINIMUM -> "المساحة غير كافية للتثبيت، يرجى حذف بعض الملفات لتفريغ مساحة."
            }
        )

        // 3. Android OS Check
        val osStatus = when {
            device.sdkInt >= game.recommendedAndroidVersion -> SpecStatus.MEETS_RECOMMENDED
            device.sdkInt >= game.minAndroidVersion -> SpecStatus.MEETS_MINIMUM
            else -> SpecStatus.BELOW_MINIMUM
        }
        val osComparison = SpecItemComparison(
            nameEn = "Android Version",
            nameAr = "إصدار نظام أندرويد",
            deviceValue = "Android ${device.androidVersion} (API ${device.sdkInt})",
            minValue = "API ${game.minAndroidVersion}+",
            recommendedValue = "API ${game.recommendedAndroidVersion}+",
            status = osStatus,
            messageEn = when (osStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED, SpecStatus.MEETS_RECOMMENDED -> "Fully compatible modern Android OS with updated Vulkan/GL support."
                SpecStatus.MEETS_MINIMUM -> "Supported Android version."
                SpecStatus.BELOW_MINIMUM -> "Unsupported Android version. Game requires a newer OS."
            },
            messageAr = when (osStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED, SpecStatus.MEETS_RECOMMENDED -> "إصدار أندرويد حديث ومتوافق تماماً مع محركات الرسوميات الحديثة."
                SpecStatus.MEETS_MINIMUM -> "إصدار مدعوم ويلبي الحد الأدنى المطلوب."
                SpecStatus.BELOW_MINIMUM -> "إصدار الأندرويد قديم وغير مدعوم لهذه اللعبة."
            }
        )

        // 4. CPU Cores Check
        val cpuStatus = when {
            device.cpuCores >= game.recommendedCpuCores -> SpecStatus.MEETS_RECOMMENDED
            device.cpuCores >= game.minCpuCores -> SpecStatus.MEETS_MINIMUM
            else -> SpecStatus.BELOW_MINIMUM
        }
        val cpuComparison = SpecItemComparison(
            nameEn = "CPU Cores",
            nameAr = "أنوية المعالج (CPU)",
            deviceValue = "${device.cpuCores} Cores",
            minValue = "${game.minCpuCores} Cores",
            recommendedValue = "${game.recommendedCpuCores} Cores",
            status = cpuStatus,
            messageEn = when (cpuStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED, SpecStatus.MEETS_RECOMMENDED -> "Multi-core processor ready for game physics and AI loops."
                SpecStatus.MEETS_MINIMUM -> "Meets minimum core count."
                SpecStatus.BELOW_MINIMUM -> "Lower core count than recommended for this title."
            },
            messageAr = when (cpuStatus) {
                SpecStatus.EXCEEDS_RECOMMENDED, SpecStatus.MEETS_RECOMMENDED -> "معالج متعدد الأنوية جاهز لمعالجة فيزياء اللعبة والذكاء الاصطناعي."
                SpecStatus.MEETS_MINIMUM -> "يلبي الحد الأدنى لعدد الأنوية."
                SpecStatus.BELOW_MINIMUM -> "عدد أنوية المعالج أقل من الحد الأدنى."
            }
        )

        // 5. Architecture (64-bit requirement)
        val archStatus = if (game.requires64Bit && !device.is64Bit) {
            SpecStatus.BELOW_MINIMUM
        } else {
            SpecStatus.MEETS_RECOMMENDED
        }
        val archComparison = SpecItemComparison(
            nameEn = "CPU Architecture",
            nameAr = "معمارية المعالج",
            deviceValue = if (device.is64Bit) "64-bit (${device.cpuArch.substringBefore(',')})" else "32-bit",
            minValue = if (game.requires64Bit) "64-bit Required" else "32-bit / 64-bit",
            recommendedValue = "64-bit (arm64-v8a)",
            status = archStatus,
            messageEn = if (archStatus == SpecStatus.BELOW_MINIMUM) {
                "Game requires 64-bit architecture but device is 32-bit."
            } else {
                "Supported instruction set architecture."
            },
            messageAr = if (archStatus == SpecStatus.BELOW_MINIMUM) {
                "اللعبة تتطلب معمارية 64-bit بينما جهازك 32-bit."
            } else {
                "معمارية المعالج متوافقة ومدعومة بالكامل."
            }
        )

        // Overall Level Calculation
        val hasFatalFailure = ramStatus == SpecStatus.BELOW_MINIMUM ||
                osStatus == SpecStatus.BELOW_MINIMUM ||
                storageStatus == SpecStatus.BELOW_MINIMUM ||
                archStatus == SpecStatus.BELOW_MINIMUM

        val meetsAllRecommended = ramStatus in listOf(SpecStatus.MEETS_RECOMMENDED, SpecStatus.EXCEEDS_RECOMMENDED) &&
                osStatus in listOf(SpecStatus.MEETS_RECOMMENDED, SpecStatus.EXCEEDS_RECOMMENDED) &&
                storageStatus in listOf(SpecStatus.MEETS_RECOMMENDED, SpecStatus.EXCEEDS_RECOMMENDED) &&
                cpuStatus in listOf(SpecStatus.MEETS_RECOMMENDED, SpecStatus.EXCEEDS_RECOMMENDED) &&
                archStatus != SpecStatus.BELOW_MINIMUM

        val (level, score) = when {
            hasFatalFailure -> {
                CompatibilityLevel.NOT_RECOMMENDED to 32
            }
            meetsAllRecommended -> {
                CompatibilityLevel.EXCELLENT to 95
            }
            else -> {
                CompatibilityLevel.POSSIBLE to 68
            }
        }

        val adviceEn = when (level) {
            CompatibilityLevel.EXCELLENT -> "Hardware easily handles this title. Recommended settings: High graphics. Note: Actual performance and frame rate depend on resolution and thermal conditions."
            CompatibilityLevel.POSSIBLE -> "Playable experience expected. Suggested settings: Low to Medium graphics, balance resolution to prevent overheating. Note: Never claim exact FPS without benchmark testing."
            CompatibilityLevel.NOT_RECOMMENDED -> "Not recommended for this device. Severe stutter, low responsiveness, or installation failure may occur. Check lightweight alternatives."
        }

        val adviceAr = when (level) {
            CompatibilityLevel.EXCELLENT -> "عتاد جهازك يشغل اللعبة بكل أريحية. الإعدادات المقترحة: رسوميات عالية. ملاحظة: يعتمد الأداء ومعدل الإطارات الفعلي على حرارة الجهاز والدقة."
            CompatibilityLevel.POSSIBLE -> "تجربة لعب قابلة للتشغيل. الإعدادات المقترحة: رسوميات منخفضة إلى متوسطة لتفادي سخونة الجهاز. ملاحظة: لا يمكن الجزم بمعدل FPS دقيق دون اختبار فعلي."
            CompatibilityLevel.NOT_RECOMMENDED -> "غير موصى بتشغيل هذه اللعبة على جهازك. قد تواجه تقطيعاً حاداً أو فشل تثبيت. ننصحك باختيار ألعاب الفئة الخفيفة."
        }

        return CompatibilityResult(
            level = level,
            score = score,
            ramCheck = ramComparison,
            storageCheck = storageComparison,
            osCheck = osComparison,
            cpuCheck = cpuComparison,
            architectureCheck = archComparison,
            adviceEn = adviceEn,
            adviceAr = adviceAr
        )
    }
}
