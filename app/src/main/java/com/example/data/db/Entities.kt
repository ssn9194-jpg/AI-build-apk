package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val packageName: String,
    val mainActivityCode: String,
    val screenCode: String,
    val manifestCode: String,
    val gradleCode: String,
    val previewType: String,
    val apkPath: String? = null,
    val apkSize: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val lastMessagePreview: String = "",
    val messageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long = 1L,
    val role: String,
    val content: String,
    val thinkingProcess: String? = null,
    val projectId: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)
