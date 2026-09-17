package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ChatMessage
import com.example.data.api.GeminiConstants
import com.example.data.templates.AppTemplates
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.StudioUiState
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: StudioViewModel,
    uiState: StudioUiState,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "اپلیکیشن تسک و وظایف روزانه مینیمال" to AppTemplates.ZEN_TASKS,
        "ماشین‌حساب مدرن با طراحی تمیز" to AppTemplates.NEO_CALCULATOR,
        "تایمر پومودورو برای افزایش تمرکز" to AppTemplates.POMODORO,
        "ردیاب هوشمند مخارج روزانه با نمودار" to null
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        // Minimalist Top Bar with Session Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // History Button with Badge
                IconButton(
                    onClick = { viewModel.toggleHistoryDrawer(true) },
                    modifier = Modifier.testTag("open_history_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (uiState.sessions.isNotEmpty()) {
                                Badge(containerColor = Color(0xFF6366F1)) {
                                    Text("${uiState.sessions.size}", fontSize = 9.sp, color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Forum,
                            contentDescription = "تاریخچه گفتگوها",
                            tint = Color(0xFF818CF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.currentSessionTitle,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${if (uiState.selectedModel == GeminiConstants.MODEL_PRO) "Gemini 3.1 Pro" else "Gemini 3.5 Flash"} • ${uiState.botPersona.title}",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // New Chat Button
                IconButton(
                    onClick = { viewModel.createNewSession() },
                    modifier = Modifier.testTag("new_chat_btn")
                ) {
                    Icon(
                        Icons.Default.AddComment,
                        contentDescription = "گفتگوی جدید",
                        tint = Color(0xFF10B981)
                    )
                }

                // Clear current session messages
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("clear_chat_button")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "پاک‌سازی این گفتگو",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Session History BottomSheet
        if (uiState.showHistoryDrawer) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleHistoryDrawer(false) },
                containerColor = Color(0xFF0F172A),
                scrimColor = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF818CF8))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تاریخچه گفتگوها (Sessions)",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.createNewSession() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("گفتگوی جدید", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Text(
                        text = "تمام مکالمات ذخیره شده و به عنوان زمینه (Context) برای بهبود پاسخ‌های بعدی بات استفاده می‌شوند.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.sessions.isEmpty()) {
                        Text(
                            text = "هنوز گفتگویی ثبت نشده است.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.sessions) { session ->
                                val isCurrent = session.id == uiState.currentSessionId
                                Surface(
                                    onClick = { viewModel.switchSession(session.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCurrent) Color(0xFF1E1B4B) else Color(0xFF1E293B),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCurrent) Color(0xFF818CF8) else Color(0xFF334155)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (isCurrent) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF10B981))
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    text = session.title,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                    maxLines = 1
                                                )
                                            }
                                            if (session.lastMessagePreview.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = session.lastMessagePreview,
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${session.messageCount} پیام • آخرین بروزرسانی",
                                                color = Color(0xFF64748B),
                                                fontSize = 10.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteSession(session.id) }
                                        ) {
                                            Icon(
                                                Icons.Default.DeleteOutline,
                                                contentDescription = "حذف گفتگو",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Error Banner
        AnimatedVisibility(visible = uiState.error != null) {
            Surface(
                color = Color(0xFF450A0A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFF87171))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = Color(0xFFFECACA),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFFFECACA))
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { (prompt, template) ->
                Surface(
                    onClick = {
                        if (template != null) {
                            viewModel.selectProject(template)
                        }
                        viewModel.sendMessage(prompt)
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(prompt, color = Color(0xFFE2E8F0), fontSize = 11.sp)
                    }
                }
            }
        }

        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(
                    message = message,
                    onOpenCode = { project ->
                        viewModel.selectProject(project)
                        viewModel.setTab(AppTab.CODE_PREVIEW)
                    },
                    onExportApk = { project ->
                        viewModel.selectProject(project)
                        viewModel.setTab(AppTab.APK_EXPORT)
                        viewModel.buildApk()
                    }
                )
            }

            if (uiState.isLoading) {
                item {
                    LoadingBubble(thinkingText = uiState.currentThinking)
                }
            }
        }

        // Input Field Bar
        Surface(
            color = Color(0xFF0F172A),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onInputTextChanged(it) },
                    placeholder = {
                        Text(
                            "نام یا ایده اپلیکیشن را بنویسید (مثلاً: ردیاب تمرین ورزشی)...",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendMessage() },
                    enabled = uiState.inputText.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.inputText.isNotBlank() && !uiState.isLoading) Color(0xFF6366F1)
                            else Color(0xFF334155)
                        )
                        .testTag("send_message_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "ارسال",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onOpenCode: (com.example.data.api.GeneratedProject) -> Unit,
    onExportApk: (com.example.data.api.GeneratedProject) -> Unit
) {
    val isUser = message.role == "user"
    var showThinking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6366F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(if (isUser) Color(0xFF312E81) else Color(0xFF1E293B))
                    .border(
                        1.dp,
                        if (isUser) Color(0xFF4338CA) else Color(0xFF334155),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                // High Thinking Process Block if available
                if (!message.thinkingProcess.isNullOrBlank()) {
                    Surface(
                        onClick = { showThinking = !showThinking },
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = Color(0xFFA855F7),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "فرآیند استدلال عمیق (Thinking Mode)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFC084FC),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    if (showThinking) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (showThinking) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = message.thinkingProcess,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                SelectionContainer {
                    Text(
                        text = message.content,
                        color = Color(0xFFF1F5F9),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                // If this message generated or attached an app project, show action card
                message.generatedProject?.let { project ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Android,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = project.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "آماده ساخت APK",
                                    color = Color(0xFF10B981),
                                    fontSize = 10.sp
                                )
                            }

                            Text(
                                text = project.packageName,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenCode(project) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مشاهده کدها", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onExportApk(project) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("bubble_export_apk_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("خروجی APK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingBubble(thinkingText: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF6366F1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF818CF8)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = thinkingText ?: "جمینای در حال نوشتن کدهای اپلیکیشن...",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }
    }
}
