package com.example.data.api

import org.json.JSONArray
import org.json.JSONObject

/**
 * Gemini API Configuration and Request / Response models.
 */
object GeminiConstants {
    const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    // Supported modern models per specification
    const val MODEL_PRO = "gemini-3.1-pro-preview"        // Complex tasks & deep thinking
    const val MODEL_FLASH = "gemini-3.5-flash"             // General tasks & responsive chat
    const val MODEL_FLASH_LITE = "gemini-3.1-flash-lite-preview" // Ultra-fast queries

    val AVAILABLE_MODELS = listOf(
        ModelInfo(
            id = MODEL_PRO,
            displayName = "Gemini 3.1 Pro (استدلال عمیق)",
            subtitle = "بهترین برای برنامه‌نویسی پیچیده، معماری کد و High Thinking",
            recommendedFor = "توسعه کامل پروژه‌ها و کدهای پیشرفته"
        ),
        ModelInfo(
            id = MODEL_FLASH,
            displayName = "Gemini 3.5 Flash (سریع و هوشمند)",
            subtitle = "مدل پیش‌فرض متوازن برای چت و تولید کد",
            recommendedFor = "گفتگو و تولید سریع کامپوننت‌ها"
        ),
        ModelInfo(
            id = MODEL_FLASH_LITE,
            displayName = "Gemini 3.1 Flash Lite (فوق سریع)",
            subtitle = "پاسخ‌دهی آنی و سبک برای پرسش‌های فوری",
            recommendedFor = "بررسی سریع و رفع خطای کدها"
        )
    )

    const val DEFAULT_SYSTEM_INSTRUCTION = """
شما «جمینای استودیو (Gemini Studio)» هستید؛ یک دستیار هوش مصنوعی فوق پیشرفته و معمار ارشد اندروید (Android Architect) متخصص در زبان Kotlin و رابط کاربری مدرن Jetpack Compose و Material Design 3.
وظیفه اصلی شما:
1. تحلیل نیازمندی‌های کاربر و گفتگو به زبان فارسی محترمانه، دقیق، موجز و فنی.
2. طراحی و نوشتن کدهای کامل، تمیز، کامپایل‌پذیر و بهینه برای اپلیکیشن‌های اندروید.
3. در هر درخواست ساخت برنامه، ساختار کامل شامل نام اپلیکیشن، توضیحات، فایل MainActivity.kt، فایل Screen.kt، کدهای AndroidManifest.xml و نیازمندی‌های Gradle را با فرمت منظم ارائه دهید.
4. اگر کاربر خواست برنامه‌ای تولید کنید، حتماً یک نام انگلیسی پکیج (مثلاً com.example.myapp) و نام نمایشی برنامه را مشخص کنید.
5. همیشه از الگوهای مدرن Jetpack Compose، Scaffold، MaterialTheme، و کامپوننت‌های زیبا و مینیمال استفاده کنید.
"""
}

data class ModelInfo(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val recommendedFor: String
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val sessionId: Long = 1L,
    val role: String, // "user" or "model"
    val content: String,
    val thinkingProcess: String? = null,
    val generatedProject: GeneratedProject? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class BotPersona(val id: String, val title: String, val description: String) {
    ARCHITECT("architect", "معمار ارشد اندروید", "متخصص ارشد Jetpack Compose، الگوهای تمیز معماری و کدهای تولیدی"),
    TUTOR("tutor", "مربی صمیمی برنامه‌نویسی", "توضیحات آموزشی گام‌به‌گام با صبر و راهنمایی‌های تشویق‌کننده"),
    AUDITOR("auditor", "حسابرس سخت‌گیر کد و بهینه‌سازی", "تمرکز بر کارایی، امنیت، مصرف حافظه و حداقل کد زائد"),
    GENERAL("general", "دستیار هوشمند همه‌فن‌حریف", "پاسخگویی به تمام موضوعات فنی و عمومی با تحلیل جامع"),
    CUSTOM("custom", "شخصیت سفارشی", "تنظیم دلخواه دستیار هوش مصنوعی بر اساس دستورالعمل کاربر")
}

enum class BotTone(val id: String, val title: String, val promptInstruction: String) {
    FORMAL("formal", "رسمی و اداری", "لحن کلام کاملاً رسمی، محترمانه، فاخر و بدون شوخی یا زبان عامیانه باشد."),
    FRIENDLY("friendly", "دوستانه و صمیمی", "لحن کلام گرم، صمیمی، دلسوزانه و خودمانی اما با رعایت ادب و احترام باشد."),
    CONCISE_TECH("concise", "فنی و فشرده (مینیمال)", "بدون مقدمه‌چینی، کاملاً مستقیم، موجز، خلاصه و متمرکز بر قطعه کدها و نکات کلیدی باشد."),
    ENTHUSIASTIC("enthusiastic", "پرانرژی و مشتاق", "با انگیزه، الهام‌بخش و با شور و اشتیاق نسبت به موفقیت کاربر پاسخ دهید.")
}

enum class KnowledgeScope(val id: String, val title: String, val promptInstruction: String) {
    ANDROID_COMPOSE("android", "تخصصی اندروید و Compose", "تمرکز شما صرفاً بر توسعه اندروید با Kotlin، Jetpack Compose، Material 3 و معماری MVVM است."),
    FULLSTACK_MOBILE("mobile", "توسعه موبایل و وب‌سرویس‌ها", "علاوه بر کلاینت اندروید، در زمینه APIهای REST، Retrofit، Ktor، اتصال پایگاه‌های داده و ابزارهای خارجی هم راهنمایی کنید."),
    GENERAL_TECH("tech", "فناوری و مهندسی نرم‌افزار", "شامل معماری سیستم‌ها، الگوریتم‌ها، دیتابیس‌ها و دانش عمومی تکنولوژی."),
    UNRESTRICTED("unrestricted", "دامنه دانش باز و نامحدود", "شما به تمام زمینه‌های علمی، فنی و عمومی تسلط دارید و بدون محدودیت پاسخ می‌دهید.")
}

enum class GuardrailMode(val id: String, val title: String, val promptInstruction: String) {
    STRICT_REFUSE("strict", "رد قاطعانه و محترمانه", "اگر کاربر سوالی نامربوط یا خارج از دامنه تخصصی تعریف‌شده مطرح کرد، مودبانه عذرخواهی کرده و اعلام کنید که وظیفه شما تنها راهنمایی در حوزه تعیین‌شده است و از پاسخ به موضوعات نامربوط معذورید."),
    GENTLE_REDIRECT("gentle", "پاسخ کوتاه و هدایت نرم", "اگر سوال نامربوط بود، یک پاسخ بسیار کوتاه (حداکثر ۱ جمله) بدهید و فوراً بحث را با یک پیشنهاد مرتبط به حوزه اصلی هدایت کنید."),
    PERMISSIVE("permissive", "آزاد (پاسخ به هر سوال)", "به هر پرسشی که کاربر مطرح کند با کمال میل و دقت پاسخ دهید.")
}

object PromptHelper {
    fun buildSystemPrompt(
        persona: BotPersona,
        tone: BotTone,
        scope: KnowledgeScope,
        guardrail: GuardrailMode,
        customInstructions: String = ""
    ): String {
        return buildString {
            appendLine("شما «جمینای استودیو (Gemini Studio)» هستید.")
            appendLine("نقش و شخصیت شما: ${persona.title} (${persona.description}).")
            appendLine("لحن پاسخ‌دهی: ${tone.promptInstruction}")
            appendLine("دامنه دانش و تخصص: ${scope.promptInstruction}")
            appendLine("سیاست برخورد با سوالات خارج از دامنه: ${guardrail.promptInstruction}")
            appendLine()
            appendLine("قواعد ساختاری:")
            appendLine("۱. در گفتگوها به زبان فارسی روان، دقیق و ساختاریافته بنویسید.")
            appendLine("۲. هنگامی که کاربر درخواست اپلیکیشن یا کد می‌کند، کدهای Kotlin و Jetpack Compose را با استفاده از Material 3 و الگوهای بدون باگ ارائه دهید.")
            appendLine("۳. در درخواست ساخت برنامه، فایل‌های MainActivity.kt و Screen.kt و AndroidManifest.xml را به صورت تمیز مشخص کنید.")
            if (customInstructions.isNotBlank()) {
                appendLine()
                appendLine("دستورالعمل‌های تکمیلی کاربر:")
                appendLine(customInstructions.trim())
            }
        }
    }
}

data class GeneratedProject(
    val title: String,
    val packageName: String,
    val description: String,
    val mainActivityCode: String,
    val screenCode: String,
    val manifestCode: String,
    val gradleDependencies: String,
    val previewType: String // "counter", "todo", "calculator", "notes", "pomodoro", "generic"
)
