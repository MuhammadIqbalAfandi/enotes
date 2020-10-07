package com.muhammadiqbalafandi.enotes.data.source.local

import android.content.Context
import androidx.room.*
import java.util.*

// Reference
// https://developer.android.com/training/data-storage/room/referencing-data
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        // if the INSTANCE is not null, then return it,
        // if it is, then create the database
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDatabase::class.java,
                        "note_database"
                    ).build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
        }
    }
}