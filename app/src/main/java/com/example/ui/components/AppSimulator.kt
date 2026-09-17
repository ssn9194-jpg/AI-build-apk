package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeneratedProject
import kotlinx.coroutines.delay

/**
 * An interactive, live phone mockup that runs dynamic simulated versions
 * of generated apps (Tasks, Calculator, Pomodoro, Counter/Custom).
 */
@Composable
fun AppSimulator(
    project: GeneratedProject,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(2.dp, Color(0xFF334155), RoundedCornerShape(28.dp))
            .background(Color(0xFF090D16))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A))
        ) {
            // Simulated Android Phone Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B1120))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "12:00",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                // Center Camera Notch
                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Wifi,
                        contentDescription = "Wifi",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp)
                    )
                    Icon(
                        Icons.Default.BatteryFull,
                        contentDescription = "Battery",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // App Bar inside Simulator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6366F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = project.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "پیش‌نمایش زنده",
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0369A1).copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Interactive Simulator Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(8.dp)
            ) {
                when (project.previewType) {
                    "calculator" -> InteractiveCalculator()
                    "pomodoro" -> InteractivePomodoro()
                    "todo" -> InteractiveTodoList()
                    else -> InteractiveGenericApp(project.title)
                }
            }
        }
    }
}

@Composable
private fun InteractiveTodoList() {
    var items by remember {
        mutableStateOf(
            listOf(
                "طراحی کامپوننت‌های Compose" to true,
                "اتصال به موتور هوش مصنوعی" to true,
                "تست خروجی فایل نصبی APK" to false,
                "بهینه‌سازی رابط کاربری مینیمال" to false
            )
        )
    }
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("عنوان وظیفه جدید...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        items = items + (input.trim() to false)
                        input = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF6366F1))
                    .testTag("sim_add_todo")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(items) { (text, isDone) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDone) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { checked ->
                            items = items.map { if (it.first == text) it.first to checked else it }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF10B981),
                            uncheckedColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = text,
                        color = if (isDone) Color(0xFF94A3B8) else Color.White,
                        fontSize = 12.sp,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { items = items.filter { it.first != text } },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveCalculator() {
    var display by remember { mutableStateOf("0") }
    var op1 by remember { mutableStateOf<Double?>(null) }
    var op by remember { mutableStateOf<String?>(null) }
    var reset by remember { mutableStateOf(false) }

    fun click(key: String) {
        when (key) {
            "C" -> { display = "0"; op1 = null; op = null }
            in listOf("+", "-", "×", "÷") -> {
                op1 = display.toDoubleOrNull()
                op = key
                reset = true
            }
            "=" -> {
                val op2 = display.toDoubleOrNull()
                if (op1 != null && op2 != null && op != null) {
                    val res = when (op) {
                        "+" -> op1!! + op2
                        "-" -> op1!! - op2
                        "×" -> op1!! * op2
                        "÷" -> if (op2 != 0.0) op1!! / op2 else Double.NaN
                        else -> op2
                    }
                    display = if (res.isNaN()) "Err" else if (res % 1.0 == 0.0) res.toInt().toString() else "%.2f".format(res)
                    op1 = null
                    op = null
                    reset = true
                }
            }
            else -> {
                if (display == "0" || reset) {
                    display = key
                    reset = false
                } else {
                    display += key
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = display,
            color = Color.White,
            fontSize = 32.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )

        val keys = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { k ->
                    val isOp = k in listOf("+", "-", "×", "÷", "=")
                    Button(
                        onClick = { click(k) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOp) Color(0xFF6366F1) else Color(0xFF1E293B),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(k, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractivePomodoro() {
    var seconds by remember { mutableStateOf(25 * 60) }
    var active by remember { mutableStateOf(false) }

    LaunchedEffect(active) {
        while (active && seconds > 0) {
            delay(1000)
            seconds--
        }
    }

    val min = seconds / 60
    val sec = seconds % 60

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%02d:%02d", min, sec),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )
        Text(
            if (active) "زمان تمرکز کاری" else "آماده برای شروع",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { active = !active },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (active) Color(0xFFEF4444) else Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (active) "توقف" else "شروع")
            }
            Button(
                onClick = { active = false; seconds = 25 * 60 },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("بازنشانی")
            }
        }
    }
}

@Composable
private fun InteractiveGenericApp(title: String) {
    var count by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("اپلیکیشن $title", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("شمارنده تعاملی پیش‌فرض کامپوز:", color = Color(0xFF94A3B8), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("$count", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF818CF8))
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { count++ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Plus")
                Spacer(modifier = Modifier.width(4.dp))
                Text("افزایش")
            }
            Button(
                onClick = { if (count > 0) count-- },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
            ) {
                Text("کاهش")
            }
        }
    }
}
