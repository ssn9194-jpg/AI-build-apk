package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.*
import com.example.data.apk.ApkBuildResult
import com.example.data.apk.ApkBuilderService
import com.example.data.db.AppDatabase
import com.example.data.db.ChatMessageEntity
import com.example.data.db.ChatSessionEntity
import com.example.data.db.ProjectEntity
import com.example.data.templates.AppTemplates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AppTab {
    CHAT,
    CODE_PREVIEW,
    APK_EXPORT,
    PROJECTS,
    SETTINGS
}

data class StudioUiState(
    val currentTab: AppTab = AppTab.CHAT,
    val messages: List<ChatMessage> = emptyList(),
    val currentProject: GeneratedProject = AppTemplates.ZEN_TASKS,
    val inputText: String = "",
    val isLoading: Boolean = false,
    val currentThinking: String? = null,
    val error: String? = null,
    // Sessions & History
    val sessions: List<ChatSessionEntity> = emptyList(),
    val currentSessionId: Long = 1L,
    val currentSessionTitle: String = "گفتگوی پیش‌فرض",
    val showHistoryDrawer: Boolean = false,
    // Settings & Advanced Bot Parameters
    val selectedModel: String = GeminiConstants.MODEL_PRO,
    val customApiKey: String = "",
    val enableHighThinking: Boolean = true,
    val systemPrompt: String = GeminiConstants.DEFAULT_SYSTEM_INSTRUCTION,
    val botPersona: BotPersona = BotPersona.ARCHITECT,
    val botTone: BotTone = BotTone.FORMAL,
    val knowledgeScope: KnowledgeScope = KnowledgeScope.ANDROID_COMPOSE,
    val guardrailMode: GuardrailMode = GuardrailMode.STRICT_REFUSE,
    val temperature: Float = 0.7f,
    val customPromptNotes: String = "",
    // APK Build State
    val isBuildingApk: Boolean = false,
    val apkBuildProgress: Float = 0f,
    val apkBuildStep: String = "",
    val apkBuildLogs: List<String> = emptyList(),
    val lastApkResult: ApkBuildResult? = null,
    // Code View
    val selectedCodeFile: String = "Screen.kt",
    val showPhoneSimulator: Boolean = false
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiService()
    private val apkBuilderService = ApkBuilderService(application)
    private val db = AppDatabase.getDatabase(application)
    private val projectDao = db.projectDao()

    private val prefs = application.getSharedPreferences("gemini_studio_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        loadInitialData()
        observeSessions()
    }

    private fun loadSettings() {
        val savedKey = prefs.getString("custom_api_key", "") ?: ""
        val savedModel = prefs.getString("selected_model", GeminiConstants.MODEL_PRO) ?: GeminiConstants.MODEL_PRO
        val savedThinking = prefs.getBoolean("high_thinking", true)
        val savedPersonaId = prefs.getString("bot_persona", BotPersona.ARCHITECT.id) ?: BotPersona.ARCHITECT.id
        val savedToneId = prefs.getString("bot_tone", BotTone.FORMAL.id) ?: BotTone.FORMAL.id
        val savedScopeId = prefs.getString("knowledge_scope", KnowledgeScope.ANDROID_COMPOSE.id) ?: KnowledgeScope.ANDROID_COMPOSE.id
        val savedGuardrailId = prefs.getString("guardrail_mode", GuardrailMode.STRICT_REFUSE.id) ?: GuardrailMode.STRICT_REFUSE.id
        val savedTemp = prefs.getFloat("temperature", 0.7f)
        val savedCustomNotes = prefs.getString("custom_notes", "") ?: ""

        val persona = BotPersona.entries.find { it.id == savedPersonaId } ?: BotPersona.ARCHITECT
        val tone = BotTone.entries.find { it.id == savedToneId } ?: BotTone.FORMAL
        val scope = KnowledgeScope.entries.find { it.id == savedScopeId } ?: KnowledgeScope.ANDROID_COMPOSE
        val guardrail = GuardrailMode.entries.find { it.id == savedGuardrailId } ?: GuardrailMode.STRICT_REFUSE

        val defaultOrGeneratedPrompt = PromptHelper.buildSystemPrompt(persona, tone, scope, guardrail, savedCustomNotes)
        val savedPrompt = prefs.getString("system_prompt", defaultOrGeneratedPrompt) ?: defaultOrGeneratedPrompt

        _uiState.value = _uiState.value.copy(
            customApiKey = savedKey,
            selectedModel = savedModel,
            enableHighThinking = savedThinking,
            systemPrompt = savedPrompt,
            botPersona = persona,
            botTone = tone,
            knowledgeScope = scope,
            guardrailMode = guardrail,
            temperature = savedTemp,
            customPromptNotes = savedCustomNotes
        )
    }

    private fun observeSessions() {
        viewModelScope.launch {
            projectDao.getAllSessions().collect { sessionList ->
                _uiState.value = _uiState.value.copy(sessions = sessionList)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val existingSessions = projectDao.getAllSessions().first()
                val activeSessionId: Long
                val activeSessionTitle: String

                if (existingSessions.isEmpty()) {
                    // Create default initial session
                    val defaultSession = ChatSessionEntity(
                        title = "معماری اپلیکیشن اندروید",
                        lastMessagePreview = "شروع گفتگوی هوشمند با جمینای",
                        messageCount = 1
                    )
                    activeSessionId = projectDao.insertSession(defaultSession)
                    activeSessionTitle = defaultSession.title

                    val welcomeMsg = ChatMessage(
                        sessionId = activeSessionId,
                        role = "model",
                        content = "سلام! من دستیار هوش مصنوعی و معمار اپلیکیشن‌های اندروید هستم. تاریخچه گفتگوهای شما ذخیره شده و می‌توانید در هر زمان مکالمات گذشته را بررسی کرده و از همان‌جا گفتگو را ادامه دهید.\n\nهمچنین می‌توانید پارامترهای لحن، شخصیت و ادغام با وب‌سرویس‌های خارجی را تنظیم کنید. چه موضوعی مد نظر شماست؟"
                    )
                    projectDao.insertMessage(
                        ChatMessageEntity(
                            sessionId = activeSessionId,
                            role = welcomeMsg.role,
                            content = welcomeMsg.content
                        )
                    )
                    _uiState.value = _uiState.value.copy(
                        currentSessionId = activeSessionId,
                        currentSessionTitle = activeSessionTitle,
                        messages = listOf(welcomeMsg)
                    )
                } else {
                    val firstSession = existingSessions.first()
                    activeSessionId = firstSession.id
                    activeSessionTitle = firstSession.title
                    loadMessagesForSession(activeSessionId, activeSessionTitle)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
    }

    private suspend fun loadMessagesForSession(sessionId: Long, title: String) {
        val savedMessages = projectDao.getMessagesForSession(sessionId).first()
        val converted = savedMessages.map { entity ->
            ChatMessage(
                id = entity.id,
                sessionId = entity.sessionId,
                role = entity.role,
                content = entity.content,
                thinkingProcess = entity.thinkingProcess,
                timestamp = entity.timestamp
            )
        }
        _uiState.value = _uiState.value.copy(
            currentSessionId = sessionId,
            currentSessionTitle = title,
            messages = if (converted.isNotEmpty()) converted else listOf(
                ChatMessage(
                    sessionId = sessionId,
                    role = "model",
                    content = "گفتگوی «$title» آماده است. پیام خود را برای ادامه ارسال کنید."
                )
            )
        )
    }

    fun toggleHistoryDrawer(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistoryDrawer = show)
    }

    fun createNewSession(title: String = "گفتگوی جدید") {
        viewModelScope.launch {
            val newSession = ChatSessionEntity(
                title = title,
                lastMessagePreview = "مکالمه جدید آغاز شد",
                messageCount = 1
            )
            val newId = projectDao.insertSession(newSession)
            val welcomeMsg = ChatMessage(
                sessionId = newId,
                role = "model",
                content = "یک گفتگوی تازه شروع شد! تاریخچه این جلسه به طور مستقل ذخیره و در پاسخ‌های بعدی به عنوان زمینه استفاده خواهد شد. چه سوال یا پروژه‌ای دارید؟"
            )
            projectDao.insertMessage(
                ChatMessageEntity(
                    sessionId = newId,
                    role = welcomeMsg.role,
                    content = welcomeMsg.content
                )
            )
            _uiState.value = _uiState.value.copy(
                currentSessionId = newId,
                currentSessionTitle = title,
                messages = listOf(welcomeMsg),
                showHistoryDrawer = false
            )
        }
    }

    fun switchSession(sessionId: Long) {
        viewModelScope.launch {
            val session = projectDao.getSessionById(sessionId) ?: return@launch
            loadMessagesForSession(sessionId, session.title)
            _uiState.value = _uiState.value.copy(showHistoryDrawer = false)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            projectDao.deleteSessionById(sessionId)
            projectDao.deleteMessagesForSession(sessionId)

            if (_uiState.value.currentSessionId == sessionId) {
                val remaining = projectDao.getAllSessions().first()
                if (remaining.isNotEmpty()) {
                    switchSession(remaining.first().id)
                } else {
                    createNewSession("گفتگوی اصلی")
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun onInputTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun selectProject(project: GeneratedProject) {
        _uiState.value = _uiState.value.copy(currentProject = project)
    }

    fun selectCodeFile(file: String) {
        _uiState.value = _uiState.value.copy(selectedCodeFile = file)
    }

    fun togglePhoneSimulator(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPhoneSimulator = show)
    }

    fun saveAdvancedPersonalitySettings(
        persona: BotPersona,
        tone: BotTone,
        scope: KnowledgeScope,
        guardrail: GuardrailMode,
        temperature: Float,
        customNotes: String,
        apiKey: String,
        model: String,
        highThinking: Boolean
    ) {
        val generatedPrompt = PromptHelper.buildSystemPrompt(persona, tone, scope, guardrail, customNotes)

        prefs.edit()
            .putString("bot_persona", persona.id)
            .putString("bot_tone", tone.id)
            .putString("knowledge_scope", scope.id)
            .putString("guardrail_mode", guardrail.id)
            .putFloat("temperature", temperature)
            .putString("custom_notes", customNotes)
            .putString("system_prompt", generatedPrompt)
            .putString("custom_api_key", apiKey.trim())
            .putString("selected_model", model)
            .putBoolean("high_thinking", highThinking)
            .apply()

        _uiState.value = _uiState.value.copy(
            botPersona = persona,
            botTone = tone,
            knowledgeScope = scope,
            guardrailMode = guardrail,
            temperature = temperature,
            customPromptNotes = customNotes,
            systemPrompt = generatedPrompt,
            customApiKey = apiKey.trim(),
            selectedModel = model,
            enableHighThinking = highThinking
        )
    }

    fun saveRawSystemPrompt(prompt: String) {
        prefs.edit().putString("system_prompt", prompt).apply()
        _uiState.value = _uiState.value.copy(systemPrompt = prompt)
    }

    fun saveSettings(key: String, model: String, highThinking: Boolean, prompt: String) {
        prefs.edit()
            .putString("custom_api_key", key.trim())
            .putString("selected_model", model)
            .putBoolean("high_thinking", highThinking)
            .putString("system_prompt", prompt)
            .apply()

        _uiState.value = _uiState.value.copy(
            customApiKey = key.trim(),
            selectedModel = model,
            enableHighThinking = highThinking,
            systemPrompt = prompt
        )
    }

    fun sendMessage(promptText: String? = null) {
        val query = (promptText ?: _uiState.value.inputText).trim()
        if (query.isBlank()) return

        val activeSessionId = _uiState.value.currentSessionId
        val userMessage = ChatMessage(
            sessionId = activeSessionId,
            role = "user",
            content = query
        )

        val updatedMessages = _uiState.value.messages + userMessage
        _uiState.value = _uiState.value.copy(
            messages = updatedMessages,
            inputText = "",
            isLoading = true,
            currentThinking = if (_uiState.value.enableHighThinking) "در حال پردازش و استدلال با جمینای براساس تاریخچه گفتگو..." else null,
            error = null
        )

        viewModelScope.launch {
            // Save user message to Room with session ID
            projectDao.insertMessage(
                ChatMessageEntity(
                    sessionId = activeSessionId,
                    role = "user",
                    content = query
                )
            )

            // Update session preview & timestamp
            val session = projectDao.getSessionById(activeSessionId)
            val updatedTitle = if (session?.title == "گفتگوی جدید" || session?.title == "گفتگوی اصلی") {
                if (query.length > 30) query.take(30) + "..." else query
            } else {
                session?.title ?: "گفتگو"
            }

            if (session != null) {
                projectDao.updateSession(
                    session.copy(
                        title = updatedTitle,
                        lastMessagePreview = if (query.length > 50) query.take(50) + "..." else query,
                        messageCount = session.messageCount + 1,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                _uiState.value = _uiState.value.copy(currentSessionTitle = updatedTitle)
            }

            // Call Gemini API passing the entire conversation history of THIS session
            // for contextual awareness
            val result = geminiService.sendMessage(
                history = _uiState.value.messages,
                newMessage = query,
                selectedModel = _uiState.value.selectedModel,
                customApiKey = _uiState.value.customApiKey,
                enableHighThinking = _uiState.value.enableHighThinking,
                customSystemPrompt = _uiState.value.systemPrompt,
                temperature = _uiState.value.temperature
            )

            result.onSuccess { (responseText, thinkingText) ->
                val generatedProject = AppTemplates.parseProjectFromResponse(responseText, query)

                val modelMessage = ChatMessage(
                    sessionId = activeSessionId,
                    role = "model",
                    content = responseText,
                    thinkingProcess = thinkingText,
                    generatedProject = generatedProject
                )

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + modelMessage,
                    currentProject = generatedProject,
                    isLoading = false,
                    currentThinking = null
                )

                // Save model message to Room DB
                projectDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = activeSessionId,
                        role = "model",
                        content = responseText,
                        thinkingProcess = thinkingText
                    )
                )

                // Update session message count
                val currentSess = projectDao.getSessionById(activeSessionId)
                if (currentSess != null) {
                    projectDao.updateSession(
                        currentSess.copy(
                            lastMessagePreview = if (responseText.length > 50) responseText.take(50) + "..." else responseText,
                            messageCount = currentSess.messageCount + 1,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                projectDao.insertProject(
                    ProjectEntity(
                        title = generatedProject.title,
                        description = generatedProject.description,
                        packageName = generatedProject.packageName,
                        mainActivityCode = generatedProject.mainActivityCode,
                        screenCode = generatedProject.screenCode,
                        manifestCode = generatedProject.manifestCode,
                        gradleCode = generatedProject.gradleDependencies,
                        previewType = generatedProject.previewType
                    )
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentThinking = null,
                    error = err.message ?: "خطا در ارتباط با سرور هوش مصنوعی"
                )
            }
        }
    }

    fun buildApk() {
        val project = _uiState.value.currentProject
        _uiState.value = _uiState.value.copy(
            isBuildingApk = true,
            apkBuildProgress = 0f,
            apkBuildStep = "آماده‌سازی خط بیلد...",
            apkBuildLogs = listOf("[START] فرآیند ساخت بسته نصبی APK آغاز شد...")
        )

        viewModelScope.launch {
            val buildResult = apkBuilderService.buildApk(project) { step, progress ->
                _uiState.value = _uiState.value.copy(
                    apkBuildStep = step,
                    apkBuildProgress = progress,
                    apkBuildLogs = _uiState.value.apkBuildLogs + listOf(step)
                )
            }

            _uiState.value = _uiState.value.copy(
                isBuildingApk = false,
                lastApkResult = buildResult
            )

            if (buildResult.success) {
                // Update in Room
                val existing = projectDao.getAllProjects().first().firstOrNull { it.title == project.title }
                if (existing != null) {
                    projectDao.updateProject(
                        existing.copy(
                            apkPath = buildResult.apkFile?.absolutePath,
                            apkSize = buildResult.apkFile?.length() ?: 0L
                        )
                    )
                }
            }
        }
    }

    fun shareApk() {
        val result = _uiState.value.lastApkResult
        if (result?.apkUri != null) {
            apkBuilderService.shareApk(result.apkUri, _uiState.value.currentProject.title)
        }
    }

    fun installApk() {
        val result = _uiState.value.lastApkResult
        if (result?.apkUri != null) {
            apkBuilderService.installApk(result.apkUri)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            projectDao.deleteMessagesForSession(_uiState.value.currentSessionId)
            _uiState.value = _uiState.value.copy(
                messages = listOf(
                    ChatMessage(
                        sessionId = _uiState.value.currentSessionId,
                        role = "model",
                        content = "حافظه گفتگوی «${_uiState.value.currentSessionTitle}» بازنشانی شد. چه موضوعی مد نظر شماست؟"
                    )
                )
            )
        }
    }
}
