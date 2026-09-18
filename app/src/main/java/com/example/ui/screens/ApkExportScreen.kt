package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.StudioUiState
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ApkExportScreen(
    viewModel: StudioViewModel,
    uiState: StudioUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val project = uiState.currentProject
    val buildResult = uiState.lastApkResult
    val terminalListState = rememberLazyListState()

    LaunchedEffect(uiState.apkBuildLogs.size) {
        if (uiState.apkBuildLogs.isNotEmpty()) {
            terminalListState.animateScrollToItem(uiState.apkBuildLogs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Android,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = project.packageName,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "نسخه ۱.۰",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0369A1).copy(alpha = 0.3f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text(
                            text = "Target SDK 36",
                            color = Color(0xFF10B981),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF047857).copy(alpha = 0.3f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        var showCiGuide by remember { mutableStateOf(false) }

        // GitHub Actions CI/CD Build Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "دریافت APK رسمی و واقعی (گیت‌هاب اکشنز)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { showCiGuide = !showCiGuide }) {
                        Text(if (showCiGuide) "بستن" else "مشاهده راهنما", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }

                Text(
                    text = "ورک‌فلو GitHub CI با گریدل واقعی، کلید اختصاصی و ساین رسمی تنظیم شده و در تب Actions آماده دانلود است.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                AnimatedVisibility(visible = showCiGuide) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("مراحل دانلود فایل نصبی ۱۰۰٪ استاندارد:", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("۱. کد پروژه را در گیت‌هاب خود Push کنید.", color = Color.White, fontSize = 11.sp)
                                Text("۲. در تب Actions ریپازیتوری، بیلد خودکار Android CI اجرا می‌شود.", color = Color.White, fontSize = 11.sp)
                                Text("۳. پس از پایان، در بخش Artifacts فایل app-debug.apk را دانلود و مستقیماً روی گوشی نصب کنید.", color = Color.White, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Workflow Path", ".github/workflows/android_build.yml"))
                                        Toast.makeText(context, "مسیر ورک‌فلو کپی شد", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("کپی مسیر فایل ورک‌فلو (.github)", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // THE CORE BUTTON: One-click Build & Export APK
        Button(
            onClick = { viewModel.buildApk() },
            enabled = !uiState.isBuildingApk,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("build_apk_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1),
                disabledContainerColor = Color(0xFF334155)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            if (uiState.isBuildingApk) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.apkBuildStep,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            } else {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ساخت و خروجی APK این برنامه",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Progress Indicator while building
        AnimatedVisibility(visible = uiState.isBuildingApk) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                LinearProgressIndicator(
                    progress = { uiState.apkBuildProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF818CF8),
                    trackColor = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.apkBuildStep,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${(uiState.apkBuildProgress * 100).toInt()}%",
                        color = Color(0xFF818CF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Success APK Info Card if built
        AnimatedVisibility(visible = buildResult != null && buildResult.success) {
            buildResult?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B).copy(alpha = 0.35f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF059669))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "فایل APK با موفقیت آماده شد",
                                    color = Color(0xFF34D399),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = res.fileSizeFormatted,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "فایل: ${res.fileName}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = "امضا: SHA-256 Scheme v2 (${res.sha256Checksum.take(14)}...)",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dual Action Buttons: Share/Save APK and Direct Install
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.shareApk() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("share_apk_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("اشتراک‌گذاری و ذخیره", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.installApk() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("install_apk_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399))
                            ) {
                                Icon(
                                    Icons.Default.DownloadForOffline,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نصب آزمایشی", color = Color(0xFF34D399), fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "نکته: در صورت مواجهه با خطای Parse Error در نصب مستقیم، از بخش زیر (ورک‌فلو گیت‌هاب) خروجی باینری رسمی و امضاشده را دانلود کنید.",
                            color = Color(0xFFFDE047),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // Live Build Terminal Output Box
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .background(Color(0xFF030712))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ترمینال خط ساخت و کامپایلر (Kotlinc & D8)",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = {
                        val allLogs = uiState.apkBuildLogs.joinToString("\n")
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Build Logs", allLogs))
                        Toast.makeText(context, "لاگ‌های بیلد کپی شد", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                }
            }

            HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = terminalListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (uiState.apkBuildLogs.isEmpty()) {
                    item {
                        Text(
                            text = "> برای آغاز ساخت بسته APK، دکمه بالا را فشار دهید.\n> تمام مراحل کامپایل، تولید دکس و امضای بسته در این پنجره نمایش داده می‌شود.",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    items(uiState.apkBuildLogs) { line ->
                        val isSuccess = line.contains("موفقیت") || line.contains("تکمیل")
                        val isStart = line.contains("آغاز") || line.contains("START")
                        Text(
                            text = "> $line",
                            color = when {
                                isSuccess -> Color(0xFF34D399)
                                isStart -> Color(0xFF38BDF8)
                                else -> Color(0xFFCBD5E1)
                            },
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
