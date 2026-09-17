package com.example.data.templates

import com.example.data.api.GeneratedProject

object AppTemplates {

    val ZEN_TASKS = GeneratedProject(
        title = "Zen Tasks",
        packageName = "com.aistudio.zentasks",
        description = "مدیریت ساده و مینیمال کارها و وظایف روزانه با کامپوز و انیمیشن‌های روان",
        previewType = "todo",
        mainActivityCode = """
package com.aistudio.zentasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TaskScreen()
                }
            }
        }
    }
}
        """.trimIndent(),
        screenCode = """
package com.aistudio.zentasks

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

data class TaskItem(val id: Int, val title: String, var isDone: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                TaskItem(1, "طراحی رابط کاربری مینیمال", true),
                TaskItem(2, "اتصال به مدل زبانی جمینای", true),
                TaskItem(3, "تولید خروجی مستقیم فایل APK", false),
                TaskItem(4, "بهینه‌سازی مصرف حافظه و رندرینگ", false)
            )
        )
    }
    var newTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zen Tasks • وظایف روزانه", style = MaterialTheme.typography.titleMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (newTitle.isNotBlank()) {
                        tasks = tasks + TaskItem(tasks.size + 1, newTitle.trim(), false)
                        newTitle = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "افزودن")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("عنوان کار جدید...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks, key = { it.id }) { task ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (task.isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isDone,
                                onCheckedChange = { checked ->
                                    tasks = tasks.map { if (it.id == task.id) it.copy(isDone = checked) else it }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = task.title,
                                modifier = Modifier.weight(1f),
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                            IconButton(
                                onClick = { tasks = tasks.filter { it.id != task.id } }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
        """.trimIndent(),
        manifestCode = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.aistudio.zentasks">
    <application
        android:label="Zen Tasks"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
        """.trimIndent(),
        gradleDependencies = """
dependencies {
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.activity:activity-compose:1.9.0")
}
        """.trimIndent()
    )

    val NEO_CALCULATOR = GeneratedProject(
        title = "Neo Calculator",
        packageName = "com.aistudio.neocalc",
        description = "ماشین‌حساب مدرن با طراحی تمیز، دکمه‌های با ارگونومی بالا و محاسبه دقیق",
        previewType = "calculator",
        mainActivityCode = """
package com.aistudio.neocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorScreen()
                }
            }
        }
    }
}
        """.trimIndent(),
        screenCode = """
package com.aistudio.neocalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || resetOnNextDigit) {
            display = d
            resetOnNextDigit = false
        } else {
            display += d
        }
    }

    fun onOp(op: String) {
        operand1 = display.toDoubleOrNull()
        operator = op
        resetOnNextDigit = true
    }

    fun onEquals() {
        val op2 = display.toDoubleOrNull()
        if (operand1 != null && op2 != null && operator != null) {
            val result = when (operator) {
                "+" -> operand1!! + op2
                "-" -> operand1!! - op2
                "×" -> operand1!! * op2
                "÷" -> if (op2 != 0.0) operand1!! / op2 else Double.NaN
                else -> op2
            }
            display = if (result.isNaN()) "Error" else if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
            operand1 = null
            operator = null
            resetOnNextDigit = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121316))
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = display,
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        )

        val rows = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    val isOp = key in listOf("÷", "×", "-", "+", "=")
                    Button(
                        onClick = {
                            when (key) {
                                "C" -> { display = "0"; operand1 = null; operator = null }
                                "=" -> onEquals()
                                in listOf("+", "-", "×", "÷") -> onOp(key)
                                else -> onDigit(key)
                            }
                        },
                        modifier = Modifier
                            .weight(if (key == "0") 2f else 1f)
                            .aspectRatio(if (key == "0") 2.1f else 1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOp) Color(0xFF6366F1) else Color(0xFF27292E),
                            contentColor = Color.White
                        )
                    ) {
                        Text(key, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
        """.trimIndent(),
        manifestCode = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.aistudio.neocalc">
    <application
        android:label="Neo Calculator"
        android:theme="@android:style/Theme.Material.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
        """.trimIndent(),
        gradleDependencies = """
dependencies {
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.activity:activity-compose:1.9.0")
}
        """.trimIndent()
    )

    val POMODORO = GeneratedProject(
        title = "Deep Focus Timer",
        packageName = "com.aistudio.pomodoro",
        description = "تایمر پومودورو مینیمال برای افزایش تمرکز کاری و تحصیلی با رابط کاربری تاریک و شیک",
        previewType = "pomodoro",
        mainActivityCode = """
package com.aistudio.pomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PomodoroScreen()
                }
            }
        }
    }
}
        """.trimIndent(),
        screenCode = """
package com.aistudio.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen() {
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(3) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                isRunning = false
                completedSessions++
                timeLeft = 25 * 60
            }
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Deep Focus", color = Color(0xFF94A3B8), fontSize = 16.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            CircularProgressIndicator(
                progress = { (timeLeft.toFloat() / (25 * 60)) },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 10.dp,
                color = Color(0xFF818CF8),
                trackColor = Color(0xFF1E293B)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(timeFormatted, fontSize = 54.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(if (isRunning) "در حال تمرکز..." else "آماده برای شروع", color = Color(0xFF94A3B8), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFEF4444) else Color(0xFF6366F1)
                ),
                shape = CircleShape,
                modifier = Modifier.height(56.dp)
            ) {
                Text(if (isRunning) "توقف موقت" else "شروع تمرکز", fontSize = 16.sp)
            }

            IconButton(
                onClick = {
                    isRunning = false
                    timeLeft = 25 * 60
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF1E293B), CircleShape)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "بازنشانی", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("جلسات تکمیل‌شده امروز: 3", color = Color(0xFF64748B), fontSize = 13.sp)
    }
}
        """.trimIndent(),
        manifestCode = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.aistudio.pomodoro">
    <application
        android:label="Deep Focus"
        android:theme="@android:style/Theme.Material.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
        """.trimIndent(),
        gradleDependencies = """
dependencies {
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.activity:activity-compose:1.9.0")
}
        """.trimIndent()
    )

    val ALL = listOf(ZEN_TASKS, NEO_CALCULATOR, POMODORO)

    fun parseProjectFromResponse(response: String, userPrompt: String): GeneratedProject {
        // Look for kotlin code blocks in markdown: ```kotlin ... ```
        val codeBlockRegex = Regex("```(?:kotlin)?([\\s\\S]*?)```", RegexOption.IGNORE_CASE)
        val matches = codeBlockRegex.findAll(response).toList()

        val fullCode = if (matches.isNotEmpty()) {
            matches.joinToString("\n\n") { it.groupValues[1].trim() }
        } else {
            response
        }

        val titleCandidate = when {
            userPrompt.contains("ماشین حساب") || userPrompt.contains("calculator") -> "Calculator App"
            userPrompt.contains("کار") || userPrompt.contains("تسک") || userPrompt.contains("todo") -> "Tasks App"
            userPrompt.contains("تمرکز") || userPrompt.contains("تایمر") || userPrompt.contains("pomodoro") -> "Timer App"
            userPrompt.contains("هزینه") || userPrompt.contains("مالی") || userPrompt.contains("expense") -> "Expense Tracker"
            else -> "Custom Gemini App"
        }

        val pkgCandidate = "com.aistudio." + titleCandidate.lowercase().replace(" ", "")

        val previewTypeCandidate = when {
            userPrompt.contains("ماشین حساب") || userPrompt.contains("calculator") -> "calculator"
            userPrompt.contains("کار") || userPrompt.contains("تسک") || userPrompt.contains("todo") -> "todo"
            userPrompt.contains("تمرکز") || userPrompt.contains("تایمر") || userPrompt.contains("pomodoro") -> "pomodoro"
            else -> "generic"
        }

        return GeneratedProject(
            title = titleCandidate,
            packageName = pkgCandidate,
            description = userPrompt.take(120),
            mainActivityCode = fullCode.take(2500),
            screenCode = fullCode,
            manifestCode = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="$pkgCandidate">
    <application
        android:label="$titleCandidate"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
            """.trimIndent(),
            gradleDependencies = """
dependencies {
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.activity:activity-compose:1.9.0")
}
            """.trimIndent(),
            previewType = previewTypeCandidate
        )
    }
}
