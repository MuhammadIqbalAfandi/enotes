package com.muhammadiqbalafandi.enotes.ui.detailnote

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import kotlinx.coroutines.launch

class NoteDetailViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private val _noteId = MutableLiveData<String>()

    private val _note = _noteId.switchMap { noteId ->
        noteRepository.observerNote(noteId).map { computeResult(it) }
    }
    val note: LiveData<Note?> = _note

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _editNoteEvent = MutableLiveData<Event<Unit>>()
    val editNoteEvent: LiveData<Event<Unit>> = _editNoteEvent

    private val _deleteNoteEvent = MutableLiveData<Event<Unit>>()
    val deleteNoteEvent: LiveData<Event<Unit>> = _deleteNoteEvent

    val isDataAvailable: LiveData<Boolean> = _note.map { it != null }

    fun editNote() {
        _editNoteEvent.value = Event(Unit)
    }

    fun deleteNote() = viewModelScope.launch {
        _noteId.value?.let {
            noteRepository.deleteNote(it)
            _deleteNoteEvent.value = Event(Unit)
        }
    }

    fun refresh() {
        // Refresh the repository and the note will be updated automatically.
        _note.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                noteRepository.refreshNote(it.id)
                _dataLoading.value = false
            }
        }
    }

    fun start(noteId: String?) {
        if (_dataLoading.value == true || noteId == _noteId.value) {
            return
        }
        // Trigger the load
        _noteId.value = noteId
    }

    private fun computeResult(noteResult: Result<Note>): Note? {
        return if (noteResult is Success) {
            noteResult.data
        } else {
            showSnackbarMessage(R.string.loading_note_error)
            null
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}