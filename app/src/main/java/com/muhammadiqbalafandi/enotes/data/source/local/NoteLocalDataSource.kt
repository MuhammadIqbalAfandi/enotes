package com.muhammadiqbalafandi.enotes.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.Result.Error
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteLocalDataSource internal constructor(
    private val noteDao: NoteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoteDataSource {

    override fun observeNote(): LiveData<Result<List<Note>>> {
        return noteDao.observeNote().map {
            Success(it)
        }
    }

    override fun observerNote(noteId: String): LiveData<Result<Note>> {
        return noteDao.observerNoteById(noteId).map {
            Success(it)
        }
    }

    override fun observeSearchNote(keywordSearch: String): LiveData<Result<List<Note>>> {
        return noteDao.observeSearchNote(keywordSearch).map {
            Success(it)
        }
    }

    override suspend fun getNote(): Result<List<Note>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(noteDao.getNote())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getNote(noteId: String): Result<Note> = withContext(ioDispatcher) {
        try {
            val note = noteDao.getNoteById(noteId)
            if (note != null) {
                return@withContext Success(note)
            } else {
                return@withContext Error(Exception("Note not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveNote(note: Note) = withContext(ioDispatcher) {
        noteDao.insertNote(note)
    }

    override suspend fun updateNote(note: Note) = withContext<Unit>(ioDispatcher) {
        noteDao.updateNote(note)
    }

    override suspend fun deleteNote(noteId: String) = withContext<Unit>(ioDispatcher) {
        noteDao.deleteNoteById(noteId)
    }

    override suspend fun deleteAllNote() = withContext(ioDispatcher) {
        noteDao.deleteNote()
    }
}