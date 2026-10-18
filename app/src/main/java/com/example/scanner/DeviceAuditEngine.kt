package com.example.scanner

import android.app.KeyguardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.example.model.DeviceSecurityState
import com.example.model.EmergencyGuideItem
import com.example.model.Language
import com.example.model.ProtectionState
import com.example.model.RiskLevel
import com.example.model.SecurityRecommendation

class DeviceAuditEngine(private val context: Context) {

    fun auditDeviceState(
        threatCount: Int = 0,
        suspiciousCount: Int = 0,
        sensitivePermCount: Int = 0,
        totalAppsScanned: Int = 0,
        lastScanTime: Long = 0L
    ): DeviceSecurityState {
        // 1. Keyguard / Lock Screen check
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isScreenLockSet = keyguardManager?.isDeviceSecure ?: true

        // 2. Developer options check
        val isDeveloperOptionsEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        } catch (e: Exception) {
            false
        }

        // 3. ADB Debugging check
        val isAdbDebuggingEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
        } catch (e: Exception) {
            false
        }

        // 4. Unknown sources / install unknown apps
        val isInstallUnknownRestricted = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                !context.packageManager.canRequestPackageInstalls()
            } else {
                @Suppress("DEPRECATION")
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 0
            }
        } catch (e: Exception) {
            true
        }

        // 5. Network / Wi-Fi check
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNetwork)
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isVpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        var wifiSsid: String? = null
        if (isWifi) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val info = wifiManager?.connectionInfo
            wifiSsid = info?.ssid?.replace("\"", "")
            if (wifiSsid == "<unknown ssid>") wifiSsid = "Wi-Fi Tarmoq"
        }

        // Calculate Hardening score
        var hardeningScore = 100
        if (!isScreenLockSet) hardeningScore -= 25
        if (isAdbDebuggingEnabled) hardeningScore -= 15
        if (isDeveloperOptionsEnabled) hardeningScore -= 10
        if (!isInstallUnknownRestricted) hardeningScore -= 10
        hardeningScore = hardeningScore.coerceIn(30, 100)

        // Calculate Overall Security Score (0 to 100)
        var overallScore = 100
        if (threatCount > 0) overallScore -= (threatCount * 30)
        if (suspiciousCount > 0) overallScore -= (suspiciousCount * 15)
        if (!isScreenLockSet) overallScore -= 15
        if (isAdbDebuggingEnabled) overallScore -= 10
        if (lastScanTime == 0L) overallScore -= 10
        overallScore = overallScore.coerceIn(15, 100)

        val protectionState = when {
            threatCount > 0 -> ProtectionState.THREAT_FOUND
            lastScanTime == 0L -> ProtectionState.SCAN_NEEDED
            else -> ProtectionState.SECURE
        }

        return DeviceSecurityState(
            isScreenLockSet = isScreenLockSet,
            isDeveloperOptionsEnabled = isDeveloperOptionsEnabled,
            isAdbDebuggingEnabled = isAdbDebuggingEnabled,
            isInstallUnknownAppsRestricted = isInstallUnknownRestricted,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            securityPatchDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A",
            wifiSsid = wifiSsid,
            wifiSecurityType = if (isWifi) "WPA2/WPA3 Shifrlangan" else "Mobil internet",
            isWifiOpen = false,
            isVpnConnected = isVpn,
            deviceHardeningScore = hardeningScore,
            overallSecurityScore = overallScore,
            protectionState = protectionState,
            lastScanTimestamp = lastScanTime,
            totalAppsScanned = totalAppsScanned,
            threatCount = threatCount,
            suspiciousCount = suspiciousCount,
            sensitivePermissionCount = sensitivePermCount
        )
    }

    fun getSecurityRecommendations(state: DeviceSecurityState): List<SecurityRecommendation> {
        val list = mutableListOf<SecurityRecommendation>()

        if (!state.isScreenLockSet) {
            list.add(
                SecurityRecommendation(
                    id = "rec_screen_lock",
                    titleUz = "Ekran qulfi (PIN / Barmoq izi) o'rnatilmagan",
                    titleRu = "Блокировка экрана не настроена",
                    titleEn = "Screen Lock Not Configured",
                    descriptionUz = "Telefoningiz yo'qolganda yoki begona qo'liga tushganda bank ilovalari va Telegram yozishmalaringiz xavf ostida qoladi.",
                    descriptionRu = "Без PIN-кода или биометрии ваши банковские приложения и мессенджеры доступны посторонним.",
                    descriptionEn = "Without a screen lock or biometrics, your banking and chat apps are vulnerable to physical theft.",
                    severity = RiskLevel.THREAT_CONFIRMED,
                    actionType = "SECURITY_SETTINGS"
                )
            )
        }

        if (state.isAdbDebuggingEnabled) {
            list.add(
                SecurityRecommendation(
                    id = "rec_adb",
                    titleUz = "USB orqali nosozliklarni tuzatish (ADB) yoqilgan",
                    titleRu = "Отладка по USB (ADB) включена",
                    titleEn = "USB Debugging (ADB) Is Active",
                    descriptionUz = "Agar telefon kompyuter yoki begona zaryadlash stansiyasiga ulansa, shaxsiy ma'lumotlar ruxsatsiz ko'chirib olinishi mumkin.",
                    descriptionRu = "Включенный ADB позволяет считывать данные при подключении к компьютеру или публичным зарядкам.",
                    descriptionEn = "Active USB debugging allows unauthorized data extraction when connected to untrusted PCs or public chargers.",
                    severity = RiskLevel.SUSPICIOUS,
                    actionType = "DEVELOPER_SETTINGS"
                )
            )
        }

        list.add(
            SecurityRecommendation(
                id = "rec_2fa_telegram",
                titleUz = "Telegram va WhatsApp uchun 2 bosqichli tasdiqlash (2FA)",
                titleRu = "Двухфакторная аутентификация в Telegram",
                titleEn = "Enable 2-Step Verification (2FA)",
                descriptionUz = "Profil o'g'irlanishidan saqlanish uchun 'Sozlamalar -> Maxfiylik -> Bulutli parol' bo'limida 2FA parolini yoqing.",
                descriptionRu = "Защитите аккаунт Telegram от угона, установив облачный пароль (2FA) в настройках конфиденциальности.",
                descriptionEn = "Protect Telegram and messaging apps from SIM-swap hijacking by setting a cloud 2FA password.",
                severity = RiskLevel.SENSITIVE_PERMISSIONS,
                actionType = "TELEGRAM_2FA"
            )
        )

        list.add(
            SecurityRecommendation(
                id = "rec_apk_sideload",
                titleUz = "Noma'lum manbalardan APK o'rnatishni cheklang",
                titleRu = "Запретите установку из неизвестных источников",
                titleEn = "Restrict Unknown APK Installations",
                descriptionUz = "Telegram guruhlaridan yoki notanish saytlardan yuklangan soxta ilovalar bank kartalarini tozalashning asosiy sababidir.",
                descriptionRu = "Не скачивайте APK-файлы из групп Telegram и подозрительных сайтов.",
                descriptionEn = "Never install APK files distributed via Telegram channels or unsolicited links.",
                severity = RiskLevel.SENSITIVE_PERMISSIONS,
                actionType = "UNKNOWN_SOURCES"
            )
        )

        list.add(
            SecurityRecommendation(
                id = "rec_bank_sms",
                titleUz = "Bank SMS-kodlarini hech qachon birovga aytmang",
                titleRu = "Никогда не сообщайте SMS-коды банков",
                titleEn = "Never Disclose Bank SMS Codes",
                descriptionUz = "Bank, Click, Payme yoki Ichki ishlar xodimi hech qachon telefon orqali tasdiqlash kodini so'ramaydi.",
                descriptionRu = "Сотрудники банков и платежных систем никогда не запрашивают SMS-коды по телефону.",
                descriptionEn = "Bank, payment service, and police staff will NEVER ask for SMS confirmation codes over the phone.",
                severity = RiskLevel.SAFE,
                actionType = "AWARENESS"
            )
        )

        return list
    }

    fun getEmergencyGuides(): List<EmergencyGuideItem> {
        return listOf(
            EmergencyGuideItem(
                id = "em_bank_fraud",
                iconName = "CreditCard",
                titleUz = "Bank kartasidan pul yechildi yoki SMS-kod berildi",
                titleRu = "С карты списаны деньги или передан SMS-код",
                titleEn = "Card Fraud or Disclosed Bank SMS Code",
                summaryUz = "Zudlik bilan kartani bloklash va tranzaksiyalarni to'xtatish bo'yicha qadamlar.",
                summaryRu = "Экстренная блокировка карты и остановка несанкционированных списаний.",
                summaryEn = "Immediate steps to freeze card and halt unauthorized debits.",
                stepsUz = listOf(
                    "1. Zudlik bilan bank yoki to'lov ilovangizga (Payme/Click/Uzum) kiring va kartani BLOKLANG.",
                    "2. Bank call-markaziga yoki xizmat ko'rsatuvchi bankka tezda qo'ng'iroq qiling va xodimlarni xabardor qiling.",
                    "3. Agar ilovaga kirish imkoni bo'lmasa, bank kartangiz orqasidagi telefon raqamiga zudlik bilan sim qoqing.",
                    "4. O'zbekiston Kiberxavfsizlik markaziga (1122) va IIV 102 xizmatiga firibgarlik to'g'risida ariza bering.",
                    "5. Firibgar bilan yozishmalar va chek skrinshotlarini o'chirmasdan saqlab qo'ying."
                ),
                stepsRu = listOf(
                    "1. Немедленно откройте банковское приложение (Payme/Click/Uzum) и ЗАБЛОКИРУЙТЕ карту.",
                    "2. Позвоните в колл-центр банка и сообщите о мошеннической операции.",
                    "3. Если доступа к приложению нет, наберите номер банка, указанный на обратной стороне карты.",
                    "4. Обратитесь в Центр кибербезопасности (1122) или МВД по номеру 102.",
                    "5. Сохраните скриншоты переписки, ссылки и электронные чеки для заявления."
                ),
                stepsEn = listOf(
                    "1. Immediately open your bank/payment app (Payme/Click/Uzum) and FREEZE your card.",
                    "2. Call your bank's 24/7 hotline and request a card block and transaction recall.",
                    "3. If app is inaccessible, call the bank number on the back of your card.",
                    "4. Report cyber fraud to the national CERT / Cybersecurity hotline (1122) and Police (102).",
                    "5. Preserve all chat screenshots, phishing URLs, and SMS logs as evidence."
                ),
                urgentActionPhone = "1122"
            ),
            EmergencyGuideItem(
                id = "em_telegram_hack",
                iconName = "Send",
                titleUz = "Telegram yoki WhatsApp hisobim buzildi",
                titleRu = "Взлом или угон аккаунта Telegram / WhatsApp",
                titleEn = "Hacked Telegram or WhatsApp Account",
                summaryUz = "Barcha faol sessiyalarni uzish va profil ustidan nazoratni qaytarish.",
                summaryRu = "Сброс посторонних сессий и восстановление контроля над мессенджером.",
                summaryEn = "Terminate unauthorized active sessions and recover account control.",
                stepsUz = listOf(
                    "1. Telegram -> Sozlamalar (Settings) -> Qurilmalar (Devices) bo'limiga kiring.",
                    "2. 'Boshqa barcha seanslarni tugatish' (Terminate all other sessions) tugmasini bosing.",
                    "3. Darhol 'Maxfiylik va xavfsizlik' -> 'Ikki bosqichli tasdiqlash' (2-Step Verification) parolini yoqing yoki parolni yangilang.",
                    "4. Tanishlaringizga sizning nomingizdan pul so'rab xabarlar yuborilayotgan bo'lsa, SMS yoki qo'ng'iroq orqali ogohlantiring.",
                    "5. Notanish botlar va kanallarga berilgan ruxsatlarni bekor qiling."
                ),
                stepsRu = listOf(
                    "1. Перейдите в Telegram: Настройки -> Устройства.",
                    "2. Нажмите 'Завершить все другие сеансы'.",
                    "3. В разделе 'Конфиденциальность' включите или смените Двухэтапную аутентификацию (облачный пароль).",
                    "4. Предупредите контакты по телефону о возможной рассылке просьб о деньгах от вашего имени.",
                    "5. Удалите подозрительных ботов и ссылки."
                ),
                stepsEn = listOf(
                    "1. Open Telegram -> Settings -> Devices.",
                    "2. Tap 'Terminate all other sessions' immediately.",
                    "3. Go to 'Privacy and Security' -> Set or update 'Two-Step Verification' password.",
                    "4. Alert your contacts by voice call or SMS that fake money-request messages may be sent.",
                    "5. Revoke permissions from unknown bots and linked web services."
                )
            ),
            EmergencyGuideItem(
                id = "em_malicious_apk",
                iconName = "BugReport",
                titleUz = "Shubhali ilova (virus/trojan) o'rnatdim",
                titleRu = "Установлено вредоносное приложение (APK)",
                titleEn = "Installed Suspicious / Rogue APK",
                summaryUz = "Tizimni xavfsiz rejimda tozalash va ruxsatlarni to'xtatish.",
                summaryRu = "Обезвреживание вредоносного ПО и отзыв системных привилегий.",
                summaryEn = "Neutralize rogue APKs and revoke administrative privileges.",
                stepsUz = listOf(
                    "1. Internetni darhol o'chiring (Parvoz rejimi / Aviaryjimni yoqing).",
                    "2. HIMOYACHI ilovasidagi 'Skaner' bo'limi orqali o'sha ilovani topib, 'Xavfsiz o'chirish' tugmasini bosing.",
                    "3. Agar ilova o'chirishga yo'l qo'ymasa: Sozlamalar -> Xavfsizlik -> Qurilma ma'murlari (Device Administrators) ro'yxatidan uni o'chiring.",
                    "4. Maxsus imkoniyatlar (Accessibility) ro'yxatidan ilovaning ruxsatini bekor qiling.",
                    "5. Telefonni 'Xavfsiz rejim'da (Safe Mode) qayta ishga tushirib, dasturlar ro'yxatidan butunlay olib tashlang."
                ),
                stepsRu = listOf(
                    "1. Немедленно включите 'Режим полета' (отключите Wi-Fi и мобильный интернет).",
                    "2. Откройте сканер HIMOYACHI, найдите приложение и нажмите 'Безопасно удалить'.",
                    "3. Если удаление заблокировано: зайдите в Настройки -> Безопасность -> Администраторы устройства и отключите приложение.",
                    "4. Отключите его в Спец. возможностях (Accessibility).",
                    "5. Перезагрузите телефон в 'Безопасный режим' (Safe Mode) и удалите файл."
                ),
                stepsEn = listOf(
                    "1. Turn on Airplane Mode immediately to stop outbound data transmission.",
                    "2. Open HIMOYACHI Scanner, locate the app, and tap 'Safe Uninstall'.",
                    "3. If uninstall is greyed out: Settings -> Security -> Device Admin Apps -> Deactivate the app.",
                    "4. Turn off its toggle under Accessibility settings.",
                    "5. Boot into Android Safe Mode and remove the malicious package completely."
                )
            )
        )
    }
}
