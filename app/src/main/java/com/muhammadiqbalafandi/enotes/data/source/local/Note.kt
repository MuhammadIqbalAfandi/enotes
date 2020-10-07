package com.muhammadiqbalafandi.enotes.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note")
data class Note @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "body") var body: String,
    @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "pin") var pin: Boolean = false,
    @ColumnInfo(name = "encryption_key") val encryptionKey: String?,
    @PrimaryKey @ColumnInfo(name = "id")  val id: String = UUID.randomUUID().toString()
)