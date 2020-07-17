package com.muhammadiqbalafandi.enotes.data.source

import androidx.lifecycle.LiveData
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.Result.Error
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DefaultNoteRepository(
    private val noteRemoteDataSource: NoteDataSource,
    private val noteLocalDataSource: NoteDataSource
) : NoteRepository {

    override fun observeNote(): LiveData<Result<List<Note>>> {
        return noteLocalDataSource.observeNote()
    }

    override fun observerNote(noteId: String): LiveData<Result<Note>> {
        return noteLocalDataSource.observerNote(noteId)
    }

    override fun observeSearchNote(keywordSearch: String): LiveData<Result<List<Note>>> {
        return noteLocalDataSource.observeSearchNote(keywordSearch)
    }

    override suspend fun refreshNote() {
        updateNoteFromRemoteDataSource()
    }

    override suspend fun refreshNote(noteId: String) {
        updateNoteFromRemoteDataSource(noteId)
    }

    private suspend fun updateNoteFromRemoteDataSource() {
        val remoteNote = noteRemoteDataSource.getNote()

        if (remoteNote is Success) {
            noteLocalDataSource.deleteAllNote()
            remoteNote.data.forEach { note ->
                noteLocalDataSource.saveNote(note)
            }
        } else if (remoteNote is Error) {
            throw remoteNote.exception
        }
    }

    private suspend fun  updateNoteFromRemoteDataSource(noteId: String) {
        val remoteNote = noteRemoteDataSource.getNote(noteId)

        if (remoteNote is Success) {
            noteLocalDataSource.saveNote(remoteNote.data)
        }
    }

    override suspend fun getNote(noteId: String): Result<Note> {
        return noteLocalDataSource.getNote(noteId)
    }

    override suspend fun saveNote(note: Note) {
        coroutineScope {
            launch { noteLocalDataSource.saveNote(note) }
            // TODO: added operation save from noteRemoteDataSource
        }
    }

    override suspend fun updateNote(note: Note) {
        coroutineScope {
            launch { noteLocalDataSource.updateNote(note) }
            // TODO: added operation delete from noteRemoteDataSource
        }
    }

    override suspend fun deleteNote(noteId: String) {
        coroutineScope {
            launch { noteLocalDataSource.deleteNote(noteId) }
            // TODO: added operation update from noteRemoteDataSource
        }
    }

    override suspend fun deleteAllNote() {
        coroutineScope {
            launch { noteLocalDataSource.deleteAllNote() }
            // TODO: added operation delete from noteRemoteDataSource
        }
    }
}