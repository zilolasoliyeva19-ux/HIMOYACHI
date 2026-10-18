package com.example.scanner

import android.net.Uri
import com.example.model.Language
import com.example.model.UrlRiskStatus
import com.example.model.UrlScanResult
import java.util.Locale
import java.util.regex.Pattern

class PhishingScannerEngine {

    // Common brands targeted by phishing campaigns in Central Asia & globally
    private val highProfileBrands = listOf(
        "payme", "click", "uzum", "kapitalbank", "anorbank", "ipotekabank", "tbcbank",
        "agrobank", "humo", "uzcard", "telegram", "whatsapp", "instagram", "olx",
        "binance", "bybit", "steam", "google", "apple", "netflix"
    )

    // Suspicious TLDs often abused in disposable phishing campaigns
    private val suspiciousTlds = setOf(
        "xyz", "top", "work", "click", "buzz", "cf", "tk", "ml", "ga", "gq", "surf", "rest", "cam", "icu"
    )

    // Suspicious scam keywords frequently found in phishing paths or query parameters
    private val phishingKeywords = listOf(
        "bonus", "yutuq", "pul-yutish", "tasdiqlash", "karta-tekshirish", "parol",
        "sms-kod", "sovg'a", "gift", "spin", "invest", "daromad", "kompensatsiya",
        "yordam-puli", "prezident-yordami", "subsidiya", "login-verify", "account-secure",
        "unfreeze", "re-activation", "card-check", "telegram-premium-free", "stars-free"
    )

    // Regex for IP address host
    private val ipPattern = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")

    fun analyzeUrl(rawUrl: String, lang: Language): UrlScanResult {
        var cleanInput = rawUrl.trim()
        if (!cleanInput.startsWith("http://") && !cleanInput.startsWith("https://")) {
            cleanInput = "https://$cleanInput"
        }

        val parsedUri: Uri? = try {
            Uri.parse(cleanInput)
        } catch (e: Exception) {
            null
        }

        val domain = parsedUri?.host?.lowercase(Locale.ROOT) ?: cleanInput
        val scheme = parsedUri?.scheme?.lowercase(Locale.ROOT) ?: "https"
        val fullPath = (parsedUri?.path ?: "") + (parsedUri?.query ?: "")

        val isHttps = scheme == "https"
        val isIp = ipPattern.matcher(domain).matches()
        var riskScore = 0
        val redFlags = mutableListOf<String>()
        val safeAdvice = mutableListOf<String>()

        // 0. AMTSO Official Phishing & Malware Test Detection
        val isAmtsoTest = cleanInput.contains("amtso.org", ignoreCase = true) ||
                cleanInput.contains("check-android-phishing-page", ignoreCase = true) ||
                cleanInput.contains("eicar", ignoreCase = true)

        if (isAmtsoTest) {
            riskScore = 95
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "AMTSO rasmiy fishing va kiberxavfsizlik sinov sahifasi aniqlandi! (AMTSO Feature Settings Check)"
                    Language.RUSSIAN -> "Обнаружена тестовая фишинговая страница AMTSO! (AMTSO Feature Settings Check)"
                    Language.ENGLISH -> "AMTSO Official Phishing Test Page detected! (AMTSO Feature Settings Check)"
                }
            )
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Ushbu sahifa antivirus va brauzer himoyasining phishing saytlarni to'xtatish qobiliyatini tekshirish uchun mo'ljallangan."
                    Language.RUSSIAN -> "Эта страница предназначена для проверки защиты антивируса от фишинговых страниц."
                    Language.ENGLISH -> "This page verifies if anti-malware software blocks dangerous phishing pages."
                }
            )
        }

        // 1. IP address check
        if (isIp) {
            riskScore += 45
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Domen o'rniga to'g'ridan-to'g'ri IP-manzil ($domain) ishlatilgan. Bu ko'pincha server egasini yashirish uchun qilinadi."
                    Language.RUSSIAN -> "Вместо доменного имени используется прямой IP-адрес ($domain), что типично для фишинга."
                    Language.ENGLISH -> "Direct IP address ($domain) used instead of domain name. Common tactic to hide malicious server origin."
                }
            )
        }

        // 2. HTTPS vs HTTP check
        if (!isHttps) {
            riskScore += 25
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Havola shifrlanmagan HTTP protokolidan foydalanadi. Kiritilgan ma'lumotlar ochiq holda uzatiladi."
                    Language.RUSSIAN -> "Используется незащищенный протокол HTTP. Данные передаются без шифрования."
                    Language.ENGLISH -> "Unencrypted HTTP protocol. Passwords and credentials can be intercepted."
                }
            )
        }

        // 3. Punycode / Homograph attack check (e.g. xn-- or cyrillic lookalikes)
        val hasPunycode = domain.startsWith("xn--") || domain.contains(".xn--")
        val hasCyrillicInAsciiDomain = domain.any { it in '\u0400'..'\u04FF' }

        if (hasPunycode || hasCyrillicInAsciiDomain) {
            riskScore += 60
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Punycode / Homograf hujumi aniqlandi: Domen nomida vizual o'xshash (soxta) harflar ishlatilgan (masalan, ruscha 'а' bilan inglizcha 'a')."
                    Language.RUSSIAN -> "Обнаружена гомографическая атака (Punycode): подмена букв визуально похожими символами других алфавитов."
                    Language.ENGLISH -> "IDN Homograph attack detected: Domain mixes lookalike characters (e.g. Cyrillic instead of Latin) to mimic legit brands."
                }
            )
        }

        // 4. Targeted Brand Spoofing / Typosquatting
        var targetedBrand: String? = null
        for (brand in highProfileBrands) {
            if (domain.contains(brand) || fullPath.contains(brand)) {
                // Check if it's the genuine domain
                val isGenuine = isGenuineBrandDomain(domain, brand)
                if (!isGenuine) {
                    targetedBrand = brand.replaceFirstChar { it.uppercase() }
                    riskScore += 65
                    redFlags.add(
                        when (lang) {
                            Language.UZBEK -> "Brendni soxtalashtirish: Sayt '$targetedBrand' xizmatiga o'xshatilgan, biroq rasmiy domenga tegishli EMAS!"
                            Language.RUSSIAN -> "Имитация бренда: Сайт маскируется под '$targetedBrand', но не принадлежит официальной компании."
                            Language.ENGLISH -> "Brand spoofing: Link imitates '$targetedBrand', but does NOT belong to the official company domain."
                        }
                    )
                    break
                }
            }
        }

        // 5. Suspicious TLD check
        val tld = domain.substringAfterLast(".", "")
        val hasSuspiciousTld = suspiciousTlds.contains(tld)
        if (hasSuspiciousTld) {
            riskScore += 25
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Arzon yoki xavfli domen zonasi (.$tld). Ushbu zonalardan ko'pincha bir kunlik firibgar saytlar ro'yxatdan o'tkaziladi."
                    Language.RUSSIAN -> "Подозрительная доменная зона (.$tld), часто используемая для временных фишинговых сайтов."
                    Language.ENGLISH -> "Suspicious top-level domain (.$tld) frequently associated with disposable phishing campaigns."
                }
            )
        }

        // 6. Phishing keywords in URL / query
        val fullUrlLower = cleanInput.lowercase(Locale.ROOT)
        val matchedKeywords = phishingKeywords.filter { fullUrlLower.contains(it) }
        if (matchedKeywords.isNotEmpty()) {
            riskScore += 30
            val kwJoined = matchedKeywords.take(3).joinToString(", ")
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Shubhali firibgarlik kalit so'zlari aniqlandi: [$kwJoined]. Bunday so'zlar odamlarni aldashda ishlatiladi."
                    Language.RUSSIAN -> "Обнаружены фишинговые ключевые слова: [$kwJoined] (приманка для жертв)."
                    Language.ENGLISH -> "Social engineering keywords detected: [$kwJoined]. Typical scam lure pattern."
                }
            )
        }

        // 7. Excessive subdomains (e.g. login.payme.security-auth.xyz)
        val subdomainsCount = domain.split(".").size
        if (subdomainsCount >= 4) {
            riskScore += 20
            redFlags.add(
                when (lang) {
                    Language.UZBEK -> "Juda ko'p subdomenlar mavjud ($domain). Bu foydalanuvchini haqiqiy manzil borasida adashtirish uchun qilinadi."
                    Language.RUSSIAN -> "Слишком много поддоменов ($domain), что скрывает реальный домен."
                    Language.ENGLISH -> "Excessive nested subdomains ($domain) designed to conceal the true root domain."
                }
            )
        }

        // Advice according to risk
        if (riskScore >= 50) {
            safeAdvice.add(
                when (lang) {
                    Language.UZBEK -> "HECH QACHON ushbu saytda bank kartangiz raqami, amal qilish muddati yoki SMS-tasdiqlash kodini kiritmang!"
                    Language.RUSSIAN -> "НИКОГДА не вводите на этом сайте данные карты, срок действия и SMS-коды!"
                    Language.ENGLISH -> "NEVER enter your bank card number, expiry date, or SMS verification codes on this page!"
                }
            )
            safeAdvice.add(
                when (lang) {
                    Language.UZBEK -> "Ushbu havolani Telegram yoki SMS orqali yuborgan shaxsni bloklang."
                    Language.RUSSIAN -> "Заблокируйте отправителя, приславшего данную ссылку."
                    Language.ENGLISH -> "Block the sender who provided or messaged you this link."
                }
            )
        } else if (riskScore > 0) {
            safeAdvice.add(
                when (lang) {
                    Language.UZBEK -> "Sayt manzilini brauzerda diqqat bilan tekshiring va shubhali fayllarni yuklab olmang."
                    Language.RUSSIAN -> "Внимательно проверьте адресную строку браузера и не скачивайте файлы."
                    Language.ENGLISH -> "Carefully verify the browser address bar and do not download untrusted files."
                }
            )
        } else {
            safeAdvice.add(
                when (lang) {
                    Language.UZBEK -> "Shubhali belgilar aniqlanmadi, ammo notanish manbalardan kelgan so'rovlarga doimo ehtiyot bo'ling."
                    Language.RUSSIAN -> "Подозрительных признаков не выявлено, но всегда соблюдайте осторожность."
                    Language.ENGLISH -> "No immediate threats detected, but always exercise caution with unexpected links."
                }
            )
        }

        // Disclaimer requirement: Never claim a link is 100% safe
        safeAdvice.add(
            when (lang) {
                Language.UZBEK -> "Eslatma: Hech bir xavfsizlik tizimi 100% kafolat bermaydi. Shaxsiy ma'lumotlaringizni himoyalang."
                Language.RUSSIAN -> "Примечание: Ни один сканер не гарантирует 100% безопасность неизвестных ресурсов."
                Language.ENGLISH -> "Note: No scanner guarantees 100% safety. Always maintain personal vigilance."
            }
        )

        val status = when {
            riskScore >= 70 -> UrlRiskStatus.PHISHING_THREAT
            riskScore >= 45 -> UrlRiskStatus.MALICIOUS
            riskScore >= 20 -> UrlRiskStatus.SUSPICIOUS
            else -> UrlRiskStatus.SAFE
        }

        return UrlScanResult(
            url = cleanInput,
            domain = domain,
            status = status,
            riskScore = riskScore.coerceIn(0, 100),
            isHttps = isHttps,
            isIpAddress = isIp,
            isMisleadingOrTyposquatting = targetedBrand != null,
            targetedBrand = targetedBrand,
            hasSuspiciousTld = hasSuspiciousTld,
            hasPunycodeOrHomoglyphs = hasPunycode || hasCyrillicInAsciiDomain,
            detectedRedFlags = redFlags,
            safeAdvice = safeAdvice
        )
    }

    private fun isGenuineBrandDomain(domain: String, brand: String): Boolean {
        return when (brand) {
            "payme" -> domain == "payme.uz" || domain.endsWith(".payme.uz")
            "click" -> domain == "click.uz" || domain.endsWith(".click.uz")
            "uzum" -> domain == "uzum.uz" || domain.endsWith(".uzum.uz") || domain == "uzumbank.uz" || domain.endsWith(".uzumbank.uz")
            "kapitalbank" -> domain == "kapitalbank.uz" || domain.endsWith(".kapitalbank.uz")
            "anorbank" -> domain == "anorbank.uz" || domain.endsWith(".anorbank.uz")
            "ipotekabank" -> domain == "ipotekabank.uz" || domain.endsWith(".ipotekabank.uz")
            "tbcbank" -> domain == "tbcbank.uz" || domain.endsWith(".tbcbank.uz")
            "humo" -> domain == "humocard.uz" || domain.endsWith(".humocard.uz")
            "uzcard" -> domain == "uzcard.uz" || domain.endsWith(".uzcard.uz")
            "telegram" -> domain == "telegram.org" || domain == "t.me" || domain.endsWith(".telegram.org")
            "whatsapp" -> domain == "whatsapp.com" || domain.endsWith(".whatsapp.com")
            "google" -> domain == "google.com" || domain.endsWith(".google.com")
            "instagram" -> domain == "instagram.com" || domain.endsWith(".instagram.com")
            "olx" -> domain == "olx.uz" || domain.endsWith(".olx.uz")
            else -> false
        }
    }
}
