# HIMOYACHI 🛡️

**HIMOYACHI** — Android uchun xavfsizlik yordamchisi. Ilova o‘rnatilgan ilovalarni tekshirish, havolalarni tahlil qilish, ruxsatlarni ko‘rib chiqish va xavfsizlik bo‘yicha tavsiyalar berish uchun ishlab chiqilmoqda.

> **Muhim:** HIMOYACHI telefonni 100% himoya qilishni yoki barcha zararli dasturlarni topishni kafolatlamaydi. Android cheklovlari sabab ayrim xavflarni faqat foydalanuvchi tasdiqlashi bilan bartaraf etish mumkin. Tekshiruv natijalarini yakuniy antivirus hukmi deb qabul qilmang.

## Xususiyatlar

- Kotlin va Jetpack Compose asosidagi Android interfeysi.
- O‘rnatilgan ilovalar xavfsizligini tekshirish moduli.
- Phishing va shubhali URL manzillarini tahlil qilish moduli.
- Qurilma xavfsizligi va ruxsatlar auditi.
- Tekshiruv natijalari hamda xavfsizlik tavsiyalari.
- Uzbek, English va Russian lokalizatsiyasi uchun asos.
- GitHub Actions orqali avtomatik Debug APK yig‘ish.

Funksiyalar Android versiyasi, ruxsatlar va tashqi xizmatlarning mavjudligiga bog‘liq. Shubhali ilovani o‘chirish uchun Android tizimining tasdiqlash oynasi talab qilinishi mumkin.

## Texnologiyalar

- Kotlin
- Jetpack Compose / Material 3
- Android Gradle Plugin
- Room
- Kotlin Coroutines
- OkHttp / Retrofit
- Firebase kutubxonalari (loyihada sozlangan qismlari)

## Talablar

- Android Studio va Android SDK
- JDK 17 yoki loyiha talab qiladigan mos JDK
- Gradle 9.3.1
- Android SDK Platform 36

## Android Studio orqali ishga tushirish

1. Ushbu repository'ni klonlang yoki GitHub'dan ZIP sifatida yuklab oling.
2. Loyihani Android Studio orqali oching.
3. Gradle sync tugashini kuting va kerakli Android SDK komponentlarini o‘rnating.
4. **Run** tugmasini bosib, emulator yoki USB orqali ulangan Android qurilmada ishga tushiring.

## Debug APK yaratish

Loyiha papkasida Gradle 9.3.1 o‘rnatilgan bo‘lsa:

```bash
gradle assembleDebug
```

APK quyidagi manzilda hosil bo‘ladi:

```
app/build/outputs/apk/debug/app-debug.apk
```

Android Studio terminalida `gradle` buyrug‘i mavjud bo‘lmasa, Android Studio'dagi **Build > Build Bundle(s) / APK(s) > Build APK(s)** menyusidan foydalaning.

## GitHub Actions orqali APK olish

Repository'ga kod yuborilganda yoki Actions sahifasidan workflow qo‘lda ishga tushirilganda, GitHub Actions Debug APK'ni yig‘ishga urinadi.

1. Repository ichidagi **Actions** sahifasini oching.
2. **Build Android APK** workflow'ini tanlang.
3. Oxirgi muvaffaqiyatli ishga tushirishni oching.
4. **Artifacts** bo‘limidan `HIMOYACHI-debug-apk` faylini yuklab oling.
5. ZIP ichidagi `app-debug.apk` faylini Android qurilmaga o‘rnating.

Debug APK sinov uchun mo‘ljallangan. Google Play yoki ommaviy tarqatish uchun alohida release signing kaliti va release build sozlamalari kerak bo‘ladi.

## Konfiguratsiya va maxfiy ma'lumotlar

- `.env.example` faqat namuna hisoblanadi.
- Haqiqiy API kalitlarini GitHub repository'ga, APK ichiga yoki ommaviy loglarga joylamang.
- Zarur maxfiy qiymatlarni mahalliy `.env` faylida yoki ishonchli CI secrets tizimida saqlang.
- Agar Firebase yoki Gemini integratsiyasi ishlatilsa, tegishli xizmat konfiguratsiyasi va xavfsizlik qoidalarini alohida sozlang.

## Xavfsizlik va maxfiylik

HIMOYACHI faqat foydalanuvchi bilgan va ruxsat bergan tekshiruvlarni bajarishi kerak. U tizim himoyasini chetlab o‘tmaydi, foydalanuvchi fayllarini yashirincha o‘chirmaydi va Android ruxsatlarini aylanib o‘tmaydi. Internet orqali tekshiruv yuborilsa, qaysi ma'lumot uzatilishi foydalanuvchiga aniq tushuntirilishi kerak.

## Loyiha holati

Loyiha faol ishlab chiqilmoqda. Har bir xavfsizlik moduli real qurilmada sinovdan o‘tkazilishi, noto‘g‘ri ogohlantirishlar va noto‘g‘ri salbiy natijalar bo‘yicha tekshirilishi kerak.

## Litsenziya

Hozircha litsenziya ko‘rsatilmagan. Loyiha muallifi tarqatish shartlarini alohida belgilashi mumkin.
