package com.example.scanner

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.model.AppThreatModel
import com.example.model.Language
import com.example.model.RiskLevel
import com.example.model.SensitivePermissionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Locale

class AppScannerEngine(private val context: Context) {

    // Known official package names of high-profile financial & messaging apps in Uzbekistan & globally
    private val officialAppSignatures = mapOf(
        "payme" to listOf("uz.payme.android", "uz.payme.business"),
        "click" to listOf("com.fido.click", "uz.click.merchant"),
        "uzum" to listOf("uz.uzum.app", "uz.uzumbank.app", "uz.uzumnasiya.app"),
        "telegram" to listOf("org.telegram.messenger", "org.telegram.messenger.web", "org.telegram.plus"),
        "whatsapp" to listOf("com.whatsapp", "com.whatsapp.w4b"),
        "instagram" to listOf("com.instagram.android"),
        "humo" to listOf("uz.humo.card", "uz.humocard.app"),
        "uzcard" to listOf("uz.plump.uzcard", "uz.uzcard.myuzcard"),
        "anorbank" to listOf("uz.anorbank.retail"),
        "kapitalbank" to listOf("uz.kapitalbank.retail", "com.kapitalbank.apelsin"),
        "tbc" to listOf("ge.tbc.tbcbank.uz")
    )

    suspend fun scanInstalledApps(
        lang: Language,
        onProgress: (current: Int, total: Int, currentAppName: String) -> Unit
    ): List<AppThreatModel> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val packages: List<PackageInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val total = packages.size
        val results = mutableListOf<AppThreatModel>()

        for ((index, pkg) in packages.withIndex()) {
            val appInfo = pkg.applicationInfo ?: continue
            val appName = try {
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                pkg.packageName
            }

            onProgress(index + 1, total, appName)
            // Small responsive delay so UI can display scanning animation smoothly
            if (index % 5 == 0) {
                delay(12)
            }

            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val requestedPermissions = pkg.requestedPermissions?.toList() ?: emptyList()
            val isSideloaded = checkIsSideloaded(pm, pkg.packageName, isSystemApp)
            val reasons = mutableListOf<String>()
            val highRiskPermissions = mutableListOf<String>()
            var calculatedRiskScore = 0

            // 1. Check for Dangerous Combinations (Banking Trojan / Spyware pattern)
            val hasAccessibility = requestedPermissions.any { it.contains("ACCESSIBILITY", ignoreCase = true) }
            val hasOverlay = requestedPermissions.contains("android.permission.SYSTEM_ALERT_WINDOW")
            val hasSmsReceive = requestedPermissions.contains("android.permission.RECEIVE_SMS")
            val hasSmsRead = requestedPermissions.contains("android.permission.READ_SMS")
            val hasSmsSend = requestedPermissions.contains("android.permission.SEND_SMS")
            val hasInternet = requestedPermissions.contains("android.permission.INTERNET")
            val hasBoot = requestedPermissions.contains("android.permission.RECEIVE_BOOT_COMPLETED")
            val hasInstallUnknown = requestedPermissions.contains("android.permission.REQUEST_INSTALL_PACKAGES")
            val hasCamera = requestedPermissions.contains("android.permission.CAMERA")
            val hasMic = requestedPermissions.contains("android.permission.RECORD_AUDIO")
            val hasLocation = requestedPermissions.contains("android.permission.ACCESS_FINE_LOCATION") ||
                    requestedPermissions.contains("android.permission.ACCESS_COARSE_LOCATION")

            // Critical combo: Accessibility + Overlay (Banking Trojan hallmark)
            if (hasAccessibility && hasOverlay && !isSystemApp) {
                calculatedRiskScore += 45
                reasons.add(
                    when (lang) {
                        Language.UZBEK -> "Xavfli kombinatsiya: Maxsus imkoniyatlar (Accessibility) va Ekranni qoplash (Overlay). Bank ma'lumotlarini o'g'irlashda qo'llanilishi mumkin."
                        Language.RUSSIAN -> "Опасная комбинация: Специальные возможности и наложение поверх других окон. Признак банковского трояна."
                        Language.ENGLISH -> "High-risk combo: Accessibility Service + System Overlay. Known pattern used by banking trojans to capture screens."
                    }
                )
                highRiskPermissions.add("ACCESSIBILITY + OVERLAY")
            }

            // Critical combo: Full SMS control + Internet (OTP stealer)
            if ((hasSmsReceive || hasSmsRead) && hasSmsSend && hasInternet && !isSystemApp) {
                calculatedRiskScore += 40
                reasons.add(
                    when (lang) {
                        Language.UZBEK -> "SMS o'qish va jo'natish: Bank kartalari va parollarni tasdiqlovchi bir martalik SMS-kodlarni tutib olish xavfi."
                        Language.RUSSIAN -> "Чтение и отправка SMS: Угроза перехвата одноразовых банковских кодов и паролей."
                        Language.ENGLISH -> "SMS interception: Can read and send SMS messages, risking OTP / 2FA bank verification codes."
                    }
                )
                highRiskPermissions.add("SMS_INTERCEPT")
            }

            // Dropper combo: Unknown app installer + Boot receiver
            if (hasInstallUnknown && hasBoot && !isSystemApp) {
                calculatedRiskScore += 25
                reasons.add(
                    when (lang) {
                        Language.UZBEK -> "Noma'lum ilovalarni avtomatik o'rnatish va telefon yoqilganda ishga tushish (Dropper xavfi)."
                        Language.RUSSIAN -> "Установка сторонних приложений и автозапуск при включении (риск дроппера)."
                        Language.ENGLISH -> "Can install unknown applications and auto-start on device boot (Dropper risk)."
                    }
                )
                highRiskPermissions.add("REQUEST_INSTALL_PACKAGES")
            }

            // Sensitive hardware permissions
            if (hasCamera) highRiskPermissions.add("android.permission.CAMERA")
            if (hasMic) highRiskPermissions.add("android.permission.RECORD_AUDIO")
            if (hasLocation) highRiskPermissions.add("android.permission.ACCESS_FINE_LOCATION")
            if (hasOverlay) highRiskPermissions.add("android.permission.SYSTEM_ALERT_WINDOW")
            if (hasAccessibility) highRiskPermissions.add("ACCESSIBILITY")

            // Sideloaded unverified app check
            if (isSideloaded) {
                calculatedRiskScore += 15
                reasons.add(
                    when (lang) {
                        Language.UZBEK -> "Ilova rasmiy Google Play orqali emas, Telegram, brauzer yoki fayl menejeri orqali (APK) o'rnatilgan."
                        Language.RUSSIAN -> "Приложение установлено не из Google Play, а загружено через Telegram, браузер или APK."
                        Language.ENGLISH -> "Application is sideloaded (installed via APK, Telegram or browser, not verified by Google Play)."
                    }
                )
            }

            // 2. Typosquatting / Brand impersonation check
            val pkgLower = pkg.packageName.lowercase(Locale.ROOT)
            val appNameLower = appName.lowercase(Locale.ROOT)

            for ((brand, officialList) in officialAppSignatures) {
                val matchesKeyword = pkgLower.contains(brand) || appNameLower.contains(brand)
                val isOfficial = officialList.any { pkgLower == it }

                if (matchesKeyword && !isOfficial && !isSystemApp) {
                    calculatedRiskScore += 55
                    reasons.add(
                        when (lang) {
                            Language.UZBEK -> "Soxta ilova xavfi: Rasmiy '$brand' ilovasi emas, uning nomidan foydalanib firibgarlik qiluvchi paket bo'lishi mumkin."
                            Language.RUSSIAN -> "Подозрение на фишинговый клон: Не является официальным приложением '$brand'."
                            Language.ENGLISH -> "Brand impersonation risk: App claims to be '$brand' but does not match genuine developer signatures."
                        }
                    )
                }
            }

            // Determine Risk Level
            val riskLevel = when {
                calculatedRiskScore >= 60 -> RiskLevel.THREAT_CONFIRMED
                calculatedRiskScore >= 30 -> RiskLevel.SUSPICIOUS
                highRiskPermissions.isNotEmpty() && !isSystemApp -> RiskLevel.SENSITIVE_PERMISSIONS
                else -> RiskLevel.SAFE
            }

            // Calculate APK file size & SHA-256 for non-system apps
            var apkSha256 = ""
            var apkSizeMb = 0.0
            if (!isSystemApp && appInfo.sourceDir != null) {
                try {
                    val apkFile = File(appInfo.sourceDir)
                    if (apkFile.exists()) {
                        apkSizeMb = apkFile.length() / (1024.0 * 1024.0)
                        // Calculate SHA-256 quickly
                        apkSha256 = calculateApkSha256(apkFile)
                    }
                } catch (e: Exception) {
                    // Ignore hashing errors gracefully
                }
            }

            results.add(
                AppThreatModel(
                    packageName = pkg.packageName,
                    appName = appName,
                    versionName = pkg.versionName ?: "1.0",
                    isSystemApp = isSystemApp,
                    isSideloaded = isSideloaded,
                    riskLevel = riskLevel,
                    riskScore = calculatedRiskScore.coerceIn(0, 100),
                    reasons = reasons,
                    permissions = requestedPermissions,
                    highRiskPermissions = highRiskPermissions,
                    apkSha256 = apkSha256,
                    apkSizeMb = String.format(Locale.US, "%.1f", apkSizeMb).toDoubleOrNull() ?: 0.0,
                    firstInstallTime = pkg.firstInstallTime,
                    lastUpdateTime = pkg.lastUpdateTime
                )
            )
        }

        // Sort: THREAT_CONFIRMED first, then SUSPICIOUS, then SENSITIVE, then SAFE
        results.sortedWith(
            compareByDescending<AppThreatModel> { it.riskLevel == RiskLevel.THREAT_CONFIRMED }
                .thenByDescending { it.riskLevel == RiskLevel.SUSPICIOUS }
                .thenByDescending { it.riskScore }
                .thenBy { it.appName }
        )
    }

    private fun checkIsSideloaded(pm: PackageManager, packageName: String, isSystemApp: Boolean): Boolean {
        if (isSystemApp) return false
        return try {
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                pm.getInstallerPackageName(packageName)
            }
            // If installer is null, or it's Telegram, Chrome, WhatsApp, file manager -> sideloaded
            installer == null || installer.contains("telegram", ignoreCase = true) ||
                    installer.contains("chrome", ignoreCase = true) ||
                    installer.contains("browser", ignoreCase = true) ||
                    installer.contains("whatsapp", ignoreCase = true) ||
                    installer.contains("file", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateApkSha256(file: File): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(8192)
            FileInputStream(file).use { fis ->
                var bytesRead = fis.read(buffer)
                var totalRead = 0
                // Read up to 2MB to keep hash fast and avoid OOM
                while (bytesRead != -1 && totalRead < 2 * 1024 * 1024) {
                    digest.update(buffer, 0, bytesRead)
                    totalRead += bytesRead
                    bytesRead = fis.read(buffer)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

    fun groupAppsBySensitivePermission(apps: List<AppThreatModel>): Map<SensitivePermissionType, List<AppThreatModel>> {
        val map = mutableMapOf<SensitivePermissionType, MutableList<AppThreatModel>>()
        SensitivePermissionType.values().forEach { map[it] = mutableListOf() }

        for (app in apps) {
            val perms = app.permissions
            if (perms.contains("android.permission.CAMERA")) map[SensitivePermissionType.CAMERA]?.add(app)
            if (perms.contains("android.permission.RECORD_AUDIO")) map[SensitivePermissionType.MICROPHONE]?.add(app)
            if (perms.contains("android.permission.ACCESS_FINE_LOCATION") || perms.contains("android.permission.ACCESS_COARSE_LOCATION")) {
                map[SensitivePermissionType.LOCATION]?.add(app)
            }
            if (perms.contains("android.permission.RECEIVE_SMS") || perms.contains("android.permission.READ_SMS") || perms.contains("android.permission.SEND_SMS")) {
                map[SensitivePermissionType.SMS]?.add(app)
            }
            if (perms.contains("android.permission.READ_CONTACTS") || perms.contains("android.permission.WRITE_CONTACTS")) {
                map[SensitivePermissionType.CONTACTS]?.add(app)
            }
            if (perms.contains("android.permission.SYSTEM_ALERT_WINDOW")) map[SensitivePermissionType.OVERLAY]?.add(app)
            if (perms.any { it.contains("ACCESSIBILITY", ignoreCase = true) }) map[SensitivePermissionType.ACCESSIBILITY]?.add(app)
            if (perms.contains("android.permission.REQUEST_INSTALL_PACKAGES")) map[SensitivePermissionType.INSTALL_PACKAGES]?.add(app)
        }

        return map
    }
}
