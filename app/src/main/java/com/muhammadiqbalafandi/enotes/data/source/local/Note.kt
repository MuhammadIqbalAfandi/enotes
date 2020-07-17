package com.muhammadiqbalafandi.enotes.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note")
data class Note @JvmOverloads constructor(
    val title: String?,
    val body: String,
    val date: String,
    val pin: Boolean = false,
    @ColumnInfo(name = "encryption_key") val encryptionKey: String?,
    @PrimaryKey var id: String = UUID.randomUUID().toString()
)