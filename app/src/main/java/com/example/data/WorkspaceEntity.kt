package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspace_files")
data class WorkspaceFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "DOC", "SHEET", "SLIDE", "NOTE", "SCAN", "AUDIO"
    val content: String, // Text, CSV data, PDF/images JSON meta, note contents
    val timestamp: Long = System.currentTimeMillis(),
    val isPrivate: Boolean = false,
    val extraPath: String? = null // For audio files, Scan PDFs
)
