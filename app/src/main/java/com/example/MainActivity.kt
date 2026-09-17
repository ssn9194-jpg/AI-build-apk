package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ApkExportScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodePreviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudioApp()
            }
        }
    }
}

@Composable
fun StudioApp(viewModel: StudioViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF090D16),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.CHAT,
                    onClick = { viewModel.setTab(AppTab.CHAT) },
                    icon = {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "چت و سازنده")
                    },
                    label = { Text("چت و سازنده", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = Color(0xFF6366F1),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("chat_tab")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.CODE_PREVIEW,
                    onClick = { viewModel.setTab(AppTab.CODE_PREVIEW) },
                    icon = {
                        Icon(Icons.Default.Code, contentDescription = "کد و شبیه‌ساز")
                    },
                    label = { Text("کد و شبیه‌ساز", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = Color(0xFF6366F1),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("code_tab")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.APK_EXPORT,
                    onClick = { viewModel.setTab(AppTab.APK_EXPORT) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.lastApkResult?.success == true) {
                                    Badge(containerColor = Color(0xFF10B981)) {
                                        Text("✓", fontSize = 9.sp, color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "خروجی APK")
                        }
                    },
                    label = { Text("خروجی APK", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = Color(0xFF6366F1),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("apk_tab")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.SETTINGS,
                    onClick = { viewModel.setTab(AppTab.SETTINGS) },
                    icon = {
                        Icon(Icons.Default.Tune, contentDescription = "تنظیمات")
                    },
                    label = { Text("تنظیمات", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = Color(0xFF6366F1),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("settings_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.CHAT -> ChatScreen(viewModel = viewModel, uiState = uiState)
                AppTab.CODE_PREVIEW -> CodePreviewScreen(viewModel = viewModel, uiState = uiState)
                AppTab.APK_EXPORT -> ApkExportScreen(viewModel = viewModel, uiState = uiState)
                AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel, uiState = uiState)
                AppTab.PROJECTS -> ChatScreen(viewModel = viewModel, uiState = uiState)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
