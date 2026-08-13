package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "control_modules")
data class ControlModuleEntity(
    @PrimaryKey val code: String,
    val name: String,
    val description: String,
    val category: String,
    val isEnabled: Boolean,
    val isVip: Boolean
)
