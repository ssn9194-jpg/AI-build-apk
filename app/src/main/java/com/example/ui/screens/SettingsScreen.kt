package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.api.*
import com.example.ui.viewmodel.StudioUiState
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SettingsScreen(
    viewModel: StudioViewModel,
    uiState: StudioUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var apiKeyInput by remember(uiState.customApiKey) { mutableStateOf(uiState.customApiKey) }
    var selectedModel by remember(uiState.selectedModel) { mutableStateOf(uiState.selectedModel) }
    var highThinking by remember(uiState.enableHighThinking) { mutableStateOf(uiState.enableHighThinking) }

    // Advanced Bot Parameters
    var selectedPersona by remember(uiState.botPersona) { mutableStateOf(uiState.botPersona) }
    var selectedTone by remember(uiState.botTone) { mutableStateOf(uiState.botTone) }
    var selectedScope by remember(uiState.knowledgeScope) { mutableStateOf(uiState.knowledgeScope) }
    var selectedGuardrail by remember(uiState.guardrailMode) { mutableStateOf(uiState.guardrailMode) }
    var temperature by remember(uiState.temperature) { mutableStateOf(uiState.temperature) }
    var customNotes by remember(uiState.customPromptNotes) { mutableStateOf(uiState.customPromptNotes) }

    // Expandable states
    var showApiGuide by remember { mutableStateOf(false) }
    var showSystemPromptDetails by remember { mutableStateOf(false) }

    val hasEnvKey = BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label کپی شد", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF818CF8))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "تنظیمات پیشرفته چت‌بات و API",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "شخصیت، لحن کلام، دامنه دانش و اتصال به وب‌سرویس‌های خارجی",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // Section 1: Personality & Persona
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "۱. شخصیت و کاراکتر بات (Bot Persona)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تعیین کننده نقش اصلی و سبک تفکر هوش مصنوعی در پاسخگویی:",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                BotPersona.entries.forEach { persona ->
                    val isSelected = persona == selectedPersona
                    Surface(
                        onClick = { selectedPersona = persona },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF1E1B4B) else Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF818CF8) else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPersona = persona },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF818CF8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = persona.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = persona.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Tone & Voice
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "۲. لحن کلام و نگارش (Bot Tone)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotTone.entries.forEach { tone ->
                        val isSelected = tone == selectedTone
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTone = tone },
                            label = { Text(tone.title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF6366F1),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFF334155),
                                selectedBorderColor = Color(0xFF818CF8)
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Knowledge Scope & Guardrails
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "۳. دامنه دانش و گاردریل تخصصی",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("دامنه تخصص:", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                KnowledgeScope.entries.forEach { scope ->
                    val isSelected = scope == selectedScope
                    Surface(
                        onClick = { selectedScope = scope },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF1E1B4B) else Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF818CF8) else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedScope = scope },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF818CF8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(scope.title, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("سیاست برخورد با سوالات خارج از دامنه (Guardrails):", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                GuardrailMode.entries.forEach { guardrail ->
                    val isSelected = guardrail == selectedGuardrail
                    Surface(
                        onClick = { selectedGuardrail = guardrail },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF1E1B4B) else Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF818CF8) else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedGuardrail = guardrail },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF818CF8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(guardrail.title, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 4: Temperature & Creativity
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = Color(0xFFEC4899))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "۴. دمای خلاقیت (Temperature)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "%.2f".format(temperature),
                        color = Color(0xFF38BDF8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (temperature < 0.4f) "دقیق، ساختاریافته و بدون خطا (مناسب کدنویسی)"
                    else if (temperature < 0.8f) "متعادل (پاسخ‌های تحلیلی و مهندسی استاندارد)"
                    else "بسیار خلاق و منعطف (ایده‌پردازی و طوفان فکری)",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    valueRange = 0.1f..1.0f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF818CF8),
                        activeTrackColor = Color(0xFF6366F1),
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "دستورالعمل‌های تکمیلی و اختصاصی شما:",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customNotes,
                    onValueChange = { customNotes = it },
                    placeholder = {
                        Text(
                            "مثال: همیشه در کدهای Compose از Clean Architecture استفاده کن و متغیرها را به زبان انگلیسی بنویس...",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                )
            }
        }

        // Section 5: API Key & Model Selection
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF818CF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "۵. کلید اختصاصی و مدل زبانی",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (apiKeyInput.isNotBlank()) "کلید اختصاصی فعال" else if (hasEnvKey) "کلید محیط استودیو" else "نیاز به کلید",
                        color = if (apiKeyInput.isNotBlank() || hasEnvKey) Color(0xFF10B981) else Color(0xFFF59E0B),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    placeholder = {
                        Text(
                            if (hasEnvKey) "کلید پیش‌فرض محیط فعال است (یا کلید اختصاصی وارد کنید)" else "AIzaSy...",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                GeminiConstants.AVAILABLE_MODELS.forEach { model ->
                    val isSelected = model.id == selectedModel
                    Surface(
                        onClick = { selectedModel = model.id },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF1E1B4B) else Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF818CF8) else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedModel = model.id },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF818CF8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(model.displayName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(model.subtitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("تفکر عمیق (High Thinking Level)", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Switch(
                        checked = highThinking,
                        onCheckedChange = { highThinking = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF6366F1))
                    )
                }
            }
        }

        // Section 6: External API Integration Guide (Interactive Technical Tutorial & Code Samples)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "راهنمای فنی: ادغام API های خارجی (آب‌وهوا، اخبار و...)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { showApiGuide = !showApiGuide }) {
                        Text(if (showApiGuide) "بستن" else "نمایش کدها", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                }

                Text(
                    text = "نحوه تجهیز چت‌بات به قابلیت دریافت آب‌وهوای زنده یا اخبار روز با Gemini Function Calling و Retrofit",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                AnimatedVisibility(visible = showApiGuide) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Method 1: Function Calling
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "روش ۱: ابزارهای جمینای (Gemini Function Calling)",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = {
                                        copyToClipboard(
                                            """
// ۱. تعریف ابزار در JSON درخواست جمینای:
val toolsJson = JSONObject(""${'"'}
{
  "tools": [{
    "functionDeclarations": [
      {
        "name": "getWeather",
        "description": "دریافت وضعیت و دمای زنده آب و هوای یک شهر",
        "parameters": {
          "type": "OBJECT",
          "properties": {
            "cityName": { "type": "STRING", "description": "نام شهر مورد نظر مثلا Tehran" }
          },
          "required": ["cityName"]
        }
      },
      {
        "name": "getLatestNews",
        "description": "دریافت تیتر آخرین اخبار با تعیین دسته‌بندی",
        "parameters": {
          "type": "OBJECT",
          "properties": {
            "category": { "type": "STRING", "description": "موضوع خبر مثل tech, sports, business" }
          }
        }
      }
    ]
  }]
}
""${'"'})
                                            """.trimIndent(),
                                            "کد Function Calling"
                                        )
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "کپی", tint = Color(0xFF818CF8), modifier = Modifier.size(16.dp))
                                    }
                                }

                                Text(
                                    text = "با تعریف Tools، جمینای وقتی کاربر می‌پرسد «هوای تهران چطوره؟»، خودش تشخیص می‌دهد و یک functionCall با نام getWeather برمی‌گرداند. شما در اندروید API را صدا می‌زنید و نتیجه را به جمینای بازمی‌گردانید.",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                SelectionContainer {
                                    Text(
                                        text = """
// تعریف ابزارها در بدنه درخواست جمینای:
"tools": [{
  "functionDeclarations": [{
    "name": "getWeather",
    "description": "دریافت دمای زنده آب و هوای یک شهر",
    "parameters": {
      "type": "OBJECT",
      "properties": {
        "city": {"type": "STRING"}
      },
      "required": ["city"]
    }
  }]
}]
                                        """.trimIndent(),
                                        color = Color(0xFFFDE047),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Method 2: Retrofit Integration Code
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "روش ۲: پیاده‌سازی سرویس Retrofit در کلاینت اندروید",
                                        color = Color(0xFF10B981),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = {
                                        copyToClipboard(
                                            """
// Weather & News API Service در Kotlin:
interface ExternalApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("v2/top-headlines")
    suspend fun getTopNews(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}

data class WeatherResponse(val name: String, val main: MainData, val weather: List<WeatherInfo>)
data class MainData(val temp: Double, val humidity: Int)
data class WeatherInfo(val description: String)
                                            """.trimIndent(),
                                            "کد Retrofit"
                                        )
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "کپی", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                    }
                                }

                                SelectionContainer {
                                    Text(
                                        text = """
interface ExternalApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
// فراخوانی در کلاینت و ارسال به جمینای به عنوان زمینه زنده (RAG)
                                        """.trimIndent(),
                                        color = Color(0xFF6EE7B7),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Save Button
        Button(
            onClick = {
                viewModel.saveAdvancedPersonalitySettings(
                    persona = selectedPersona,
                    tone = selectedTone,
                    scope = selectedScope,
                    guardrail = selectedGuardrail,
                    temperature = temperature,
                    customNotes = customNotes,
                    apiKey = apiKeyInput,
                    model = selectedModel,
                    highThinking = highThinking
                )
                Toast.makeText(context, "شخصیت، لحن و پارامترهای چت‌بات با موفقیت به‌روزرسانی شد", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_settings_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ذخیره تمام تنظیمات و اعمال بر بات", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
