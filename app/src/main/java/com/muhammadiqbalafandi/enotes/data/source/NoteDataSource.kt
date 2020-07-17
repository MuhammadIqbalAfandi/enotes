package com.muhammadiqbalafandi.enotes.data.source

import androidx.lifecycle.LiveData
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.source.local.Note

/**
 * Main entry point for accessing note data.
 */
interface NoteDataSource {

    fun observeNote(): LiveData<Result<List<Note>>>

    fun observerNote(noteId: String): LiveData<Result<Note>>

    fun observeSearchNote(keywordSearch: String): LiveData<Result<List<Note>>>

    suspend fun getNote(): Result<List<Note>>

    suspend fun getNote(noteId: String): Result<Note>

    suspend fun saveNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(noteId: String)

    suspend fun deleteAllNote()
}