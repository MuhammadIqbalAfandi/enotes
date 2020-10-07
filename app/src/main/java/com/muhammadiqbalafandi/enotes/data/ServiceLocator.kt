package com.muhammadiqbalafandi.enotes.data

import android.content.Context
import com.muhammadiqbalafandi.enotes.data.source.DefaultNoteRepository
import com.muhammadiqbalafandi.enotes.data.source.NoteDataSource
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.NoteDatabase
import com.muhammadiqbalafandi.enotes.data.source.local.NoteLocalDataSource

/**
 * A Service Locator for the [NoteRepository].
 */
object ServiceLocator {

    fun provideNoteRepository(context: Context): NoteRepository {
        return createNoteRepository(context)
    }

    private fun createNoteRepository(context: Context): NoteRepository {
        return DefaultNoteRepository(
            // TODO: 01/10/20 change to the actual remote data source
            createNoteLocalDataSource(context),
            createNoteLocalDataSource(context)
        )
    }

    private fun createNoteLocalDataSource(context: Context): NoteDataSource {
        val database = NoteDatabase.getDatabase(context)
        return NoteLocalDataSource(database.noteDao())
    }
}