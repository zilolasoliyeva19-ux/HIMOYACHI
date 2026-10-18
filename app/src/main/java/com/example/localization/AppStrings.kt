package com.example.localization

import com.example.model.Language
import com.example.model.ProtectionState
import com.example.model.RiskLevel
import com.example.model.UrlRiskStatus

object AppStrings {
    fun appName(lang: Language): String = "HIMOYACHI"

    fun slogan(lang: Language): String = when (lang) {
        Language.UZBEK -> "Kiberxavfsizlik va Antivirus himoyasi"
        Language.RUSSIAN -> "Кибербезопасность и антивирусная защита"
        Language.ENGLISH -> "Cybersecurity & Antivirus Shield"
    }

    // Dashboard
    fun securityStatusTitle(lang: Language): String = when (lang) {
        Language.UZBEK -> "Telefon xavfsizligi"
        Language.RUSSIAN -> "Безопасность телефона"
        Language.ENGLISH -> "Device Security"
    }

    fun scanDeviceButton(lang: Language): String = when (lang) {
        Language.UZBEK -> "Telefonni tekshirish"
        Language.RUSSIAN -> "Проверить телефон"
        Language.ENGLISH -> "Scan Device"
    }

    fun quickScan(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tezkor tekshiruv"
        Language.RUSSIAN -> "Быстрое сканирование"
        Language.ENGLISH -> "Quick Scan"
    }

    fun deepScan(lang: Language): String = when (lang) {
        Language.UZBEK -> "To'liq tekshiruv"
        Language.RUSSIAN -> "Глубокая проверка"
        Language.ENGLISH -> "Deep Scan"
    }

    fun cancelScan(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tekshiruvni to'xtatish"
        Language.RUSSIAN -> "Остановить"
        Language.ENGLISH -> "Cancel Scan"
    }

    fun protectionState(lang: Language, state: ProtectionState): String = when (state) {
        ProtectionState.SECURE -> when (lang) {
            Language.UZBEK -> "Himoyalangan"
            Language.RUSSIAN -> "Защищено"
            Language.ENGLISH -> "Protected"
        }
        ProtectionState.THREAT_FOUND -> when (lang) {
            Language.UZBEK -> "Xavf aniqlandi"
            Language.RUSSIAN -> "Обнаружена угроза"
            Language.ENGLISH -> "Threat Detected"
        }
        ProtectionState.SCAN_NEEDED -> when (lang) {
            Language.UZBEK -> "Tekshiruv kerak"
            Language.RUSSIAN -> "Требуется проверка"
            Language.ENGLISH -> "Scan Needed"
        }
    }

    fun lastScan(lang: Language): String = when (lang) {
        Language.UZBEK -> "Oxirgi tekshiruv"
        Language.RUSSIAN -> "Последнее сканирование"
        Language.ENGLISH -> "Last scan"
    }

    fun neverScanned(lang: Language): String = when (lang) {
        Language.UZBEK -> "Hali tekshirilmagan"
        Language.RUSSIAN -> "Ещё не проверялось"
        Language.ENGLISH -> "Never scanned"
    }

    fun detectedThreats(lang: Language): String = when (lang) {
        Language.UZBEK -> "Aniklangan xavflar"
        Language.RUSSIAN -> "Обнаруженные угрозы"
        Language.ENGLISH -> "Detected Threats"
    }

    fun suspiciousApps(lang: Language): String = when (lang) {
        Language.UZBEK -> "Shubhali ilovalar"
        Language.RUSSIAN -> "Подозрительные приложения"
        Language.ENGLISH -> "Suspicious Apps"
    }

    fun appsInspected(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tekshirilgan ilovalar"
        Language.RUSSIAN -> "Проверенные приложения"
        Language.ENGLISH -> "Scanned Apps"
    }

    fun securityScoreLabel(lang: Language): String = when (lang) {
        Language.UZBEK -> "Xavfsizlik darajasi"
        Language.RUSSIAN -> "Уровень безопасности"
        Language.ENGLISH -> "Security Score"
    }

    // Tabs
    fun tabDashboard(lang: Language): String = when (lang) {
        Language.UZBEK -> "Asosiy"
        Language.RUSSIAN -> "Главная"
        Language.ENGLISH -> "Dashboard"
    }

    fun tabAntivirus(lang: Language): String = when (lang) {
        Language.UZBEK -> "Skaner"
        Language.RUSSIAN -> "Сканер"
        Language.ENGLISH -> "Scanner"
    }

    fun tabPhishing(lang: Language): String = when (lang) {
        Language.UZBEK -> "Havolalar"
        Language.RUSSIAN -> "Ссылки"
        Language.ENGLISH -> "Phishing"
    }

    fun tabTools(lang: Language): String = when (lang) {
        Language.UZBEK -> "Asboblar"
        Language.RUSSIAN -> "Инструменты"
        Language.ENGLISH -> "Tools"
    }

    fun tabSettings(lang: Language): String = when (lang) {
        Language.UZBEK -> "Sozlamalar"
        Language.RUSSIAN -> "Настройки"
        Language.ENGLISH -> "Settings"
    }

    // Tools Names
    fun toolAppScan(lang: Language): String = when (lang) {
        Language.UZBEK -> "Ilovalar tekshiruvi"
        Language.RUSSIAN -> "Проверка приложений"
        Language.ENGLISH -> "App Risk Analysis"
    }

    fun toolPhishing(lang: Language): String = when (lang) {
        Language.UZBEK -> "Phishing tekshiruvi"
        Language.RUSSIAN -> "Проверка фишинга"
        Language.ENGLISH -> "Phishing & Scam Link Check"
    }

    fun toolPermissions(lang: Language): String = when (lang) {
        Language.UZBEK -> "Ruxsatlar nazorati"
        Language.RUSSIAN -> "Контроль разрешений"
        Language.ENGLISH -> "Permission Manager"
    }

    fun toolWifi(lang: Language): String = when (lang) {
        Language.UZBEK -> "Wi-Fi xavfsizligi"
        Language.RUSSIAN -> "Безопасность Wi-Fi"
        Language.ENGLISH -> "Wi-Fi Security"
    }

    fun toolPrivacy(lang: Language): String = when (lang) {
        Language.UZBEK -> "Maxfiylik tekshiruvi"
        Language.RUSSIAN -> "Аудит приватности"
        Language.ENGLISH -> "Privacy Audit"
    }

    fun toolRecommendations(lang: Language): String = when (lang) {
        Language.UZBEK -> "Xavfsizlik tavsiyalari"
        Language.RUSSIAN -> "Рекомендации"
        Language.ENGLISH -> "Security Recommendations"
    }

    fun toolHistory(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tekshiruv tarixi"
        Language.RUSSIAN -> "История сканирований"
        Language.ENGLISH -> "Scan History"
    }

    fun toolEmergency(lang: Language): String = when (lang) {
        Language.UZBEK -> "Favqulodda yordam"
        Language.RUSSIAN -> "Экстренная помощь"
        Language.ENGLISH -> "Emergency Assistance"
    }

    // Risk levels
    fun riskLevel(lang: Language, level: RiskLevel): String = when (level) {
        RiskLevel.THREAT_CONFIRMED -> when (lang) {
            Language.UZBEK -> "Yuqori xavfli tahdid"
            Language.RUSSIAN -> "Подтвержденная угроза"
            Language.ENGLISH -> "High-Risk Threat"
        }
        RiskLevel.SUSPICIOUS -> when (lang) {
            Language.UZBEK -> "Shubhali ilova"
            Language.RUSSIAN -> "Подозрительное"
            Language.ENGLISH -> "Suspicious Application"
        }
        RiskLevel.SENSITIVE_PERMISSIONS -> when (lang) {
            Language.UZBEK -> "Keng ruxsatli ilova"
            Language.RUSSIAN -> "Расширенные разрешения"
            Language.ENGLISH -> "Sensitive Permissions"
        }
        RiskLevel.SAFE -> when (lang) {
            Language.UZBEK -> "Xavfsiz"
            Language.RUSSIAN -> "Безопасно"
            Language.ENGLISH -> "Safe"
        }
    }

    // Remediation actions
    fun safeUninstall(lang: Language): String = when (lang) {
        Language.UZBEK -> "Xavfsiz o'chirish (Uninstall)"
        Language.RUSSIAN -> "Безопасно удалить"
        Language.ENGLISH -> "Safe Uninstall"
    }

    fun openAppInfo(lang: Language): String = when (lang) {
        Language.UZBEK -> "Ilova sozlamalari"
        Language.RUSSIAN -> "Настройки приложения"
        Language.ENGLISH -> "App Settings"
    }

    fun close(lang: Language): String = when (lang) {
        Language.UZBEK -> "Yopish"
        Language.RUSSIAN -> "Закрыть"
        Language.ENGLISH -> "Close"
    }

    fun rescan(lang: Language): String = when (lang) {
        Language.UZBEK -> "Qayta tekshirish"
        Language.RUSSIAN -> "Пересканировать"
        Language.ENGLISH -> "Rescan"
    }

    fun checkUrl(lang: Language): String = when (lang) {
        Language.UZBEK -> "Havolani tekshirish"
        Language.RUSSIAN -> "Проверить ссылку"
        Language.ENGLISH -> "Check Link"
    }

    fun pasteFromClipboard(lang: Language): String = when (lang) {
        Language.UZBEK -> "Nusxadan qo'yish"
        Language.RUSSIAN -> "Вставить из буфера"
        Language.ENGLISH -> "Paste from Clipboard"
    }

    fun urlInputPlaceholder(lang: Language): String = when (lang) {
        Language.UZBEK -> "Havola kiriting (masalan, https://payme-check.uz/bonus)"
        Language.RUSSIAN -> "Введите ссылку (например, https://payme-check.uz/bonus)"
        Language.ENGLISH -> "Enter URL (e.g. https://payme-check.uz/bonus)"
    }

    fun testSampleUrls(lang: Language): String = when (lang) {
        Language.UZBEK -> "Namunaviy havolalarni sinab ko'rish:"
        Language.RUSSIAN -> "Примеры для проверки:"
        Language.ENGLISH -> "Try sample links:"
    }

    fun urlStatusTitle(lang: Language, status: UrlRiskStatus): String = when (status) {
        UrlRiskStatus.SAFE -> when (lang) {
            Language.UZBEK -> "Xavfsiz havola"
            Language.RUSSIAN -> "Безопасная ссылка"
            Language.ENGLISH -> "Safe Link"
        }
        UrlRiskStatus.SUSPICIOUS -> when (lang) {
            Language.UZBEK -> "Shubhali havola"
            Language.RUSSIAN -> "Подозрительная ссылка"
            Language.ENGLISH -> "Suspicious Link"
        }
        UrlRiskStatus.PHISHING_THREAT -> when (lang) {
            Language.UZBEK -> "XAVFLI: Phishing / Firibgarlik"
            Language.RUSSIAN -> "ОПАСНО: Фишинг / Мошенничество"
            Language.ENGLISH -> "DANGER: Phishing / Scam"
        }
        UrlRiskStatus.MALICIOUS -> when (lang) {
            Language.UZBEK -> "XAVFLI: Virus / Zararli sayt"
            Language.RUSSIAN -> "ОПАСНО: Вредоносный сайт"
            Language.ENGLISH -> "DANGER: Malicious Site"
        }
    }

    fun detectedFlagsTitle(lang: Language): String = when (lang) {
        Language.UZBEK -> "Aniqlangan xavfli belgilar:"
        Language.RUSSIAN -> "Выявленные признаки угрозы:"
        Language.ENGLISH -> "Detected Risk Indicators:"
    }

    fun adviceTitle(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tavsiyalar va xavfsizlik choralari:"
        Language.RUSSIAN -> "Рекомендации по безопасности:"
        Language.ENGLISH -> "Safety Recommendations:"
    }

    fun clear(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tozalash"
        Language.RUSSIAN -> "Очистить"
        Language.ENGLISH -> "Clear"
    }

    fun selectLanguage(lang: Language): String = when (lang) {
        Language.UZBEK -> "Tilni tanlang"
        Language.RUSSIAN -> "Выбор языка"
        Language.ENGLISH -> "Choose Language"
    }

    fun honestyPledgeTitle(lang: Language): String = when (lang) {
        Language.UZBEK -> "Haqiqiylik va Xavfsizlik Tamoyili"
        Language.RUSSIAN -> "Принцип честности и прозрачности"
        Language.ENGLISH -> "Transparency & Real Security Principle"
    }

    fun honestyPledgeText(lang: Language): String = when (lang) {
        Language.UZBEK -> "HIMOYACHI hech qachon soxta tahdidlarni to'qib chiqarmaydi va foydalanuvchini chalg'itmaydi. Barcha tekshiruvlar haqiqiy Android tizimi imkoniyatlari, o'rnatilgan paketlar, ruxsatlar va xavf signallari asosida amalga oshiriladi. Ilovalar hech qachon yashirincha o'chirilmaydi — faqat rasmiy Android tizimi orqali foydalanuvchi tasdig'i bilan amalga oshiriladi."
        Language.RUSSIAN -> "HIMOYACHI никогда не фальсифицирует угрозы и не вводит пользователей в заблуждение. Все проверки проводятся на основе реальных возможностей Android, установленных пакетов, опасных комбинаций разрешений и эвристики фишинга. Приложения никогда не удаляются скрытно — только через системный диалог с подтверждением пользователя."
        Language.ENGLISH -> "HIMOYACHI never fabricates threats or misleads users. All scans are conducted using real Android APIs, installed package permissions, sideload status, and URL heuristics. Apps are never silently deleted — remediation is always executed through official Android system dialogs with explicit user confirmation."
    }
}
