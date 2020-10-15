package com.muhammadiqbalafandi.enotes.ui.detail

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import com.muhammadiqbalafandi.enotes.databinding.FragDetailNoteBinding
import com.muhammadiqbalafandi.enotes.ui.detail.NoteDetailActionType.*
import com.muhammadiqbalafandi.enotes.utils.Utils
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

class NoteDetailViewModel(
    private val noteRepository: NoteRepository,
    application: Application
) : AndroidViewModel(application) {

    // Used in this class.
    private val context: Application = getApplication()

    private val noteId: MutableLiveData<String> = MutableLiveData()

    private val runTheAction: MutableLiveData<NoteDetailActionType> = MutableLiveData()

    private val _note: MutableLiveData<Note?> = noteId.switchMap { noteId ->
        noteRepository.observerNote(noteId).map { computeResult(it) }
    } as MutableLiveData<Note?>

    /**
     * Used directly in [FragDetailNoteBinding].
     */
    val note: MutableLiveData<Note?> = _note

    // Show error message when note is null.
    val isDataAvailable: LiveData<Boolean> = _note.map { it == null }

    // Show decryption button when encryption key exist.
    val isDecryptionNoteAvailable: MutableLiveData<Boolean> = MutableLiveData()

    // Used in fragment, to listen changes.
    private val _goToEditNotesEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val goToEditNotesEvent: LiveData<Event<Unit>> = _goToEditNotesEvent

    private val _goToListNoteEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val goToListNoteEvent: LiveData<Event<Unit>> = _goToListNoteEvent

    private val _showDialogDecryptionNoteEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val showDialogDecryptionNoteEvent: LiveData<Event<Unit>> = _showDialogDecryptionNoteEvent

    private val _showDialogDeleteNoteEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val showDialogDeleteNoteEvent: LiveData<Event<Unit>> = _showDialogDeleteNoteEvent

    fun start(noteId: String?) {
        this.noteId.value = noteId
    }

    private fun computeResult(noteResult: Result<Note>): Note? {
        return if (noteResult is Success) {
            try {
                noteResult.data.encryptionKey.run {
                    this@NoteDetailViewModel.isDecryptionNoteAvailable.value = !this.isNullOrBlank()
                }
            } catch (e: Exception) {
                Timber.e(e)
            }

            noteResult.data
        } else {
            null
        }
    }

    fun whetherTheNoteIsEncrypted(actionType: NoteDetailActionType) {
        this.runTheAction.value = actionType

        this._note.value?.run {
            if (!encryptionKey.isNullOrBlank()) {
                this@NoteDetailViewModel._showDialogDecryptionNoteEvent.value = Event(Unit)
                return
            }
        }

        if (this.runTheAction.value == DELETE_NOTE) {
            this._showDialogDeleteNoteEvent.value = Event(Unit)
            return
        }

        setAction()
    }

    fun importFileText(uri: Uri?) {
        try {
            if (uri != null) {
                val inputStream = this.context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)

                    // Read content and display as encryption key imported.
                    val encryptionKeyImported = bufferedReader.readLine()
                    whetherTheImportTheSame(encryptionKeyImported)

                    bufferedReader.close()
                }
            }
        } catch (e: Exception) {
            // TODO: 8/8/20 ERROR : Always return null when import key that empty content.
            Timber.e(e)
        }
    }

    private fun whetherTheImportTheSame(encryptionKeyImported: String) {
        this._note.value?.run {
            if (encryptionKey == encryptionKeyImported) {
                setAction()
            } else {
                Toast.makeText(
                    this@NoteDetailViewModel.context,
                    R.string.message_error_import_key,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun agreeDeletedNote() {
        setAction()
    }

    // Handle action such when user request notes to delete, edit, or note decryption.
    private fun setAction() {
        when (this.runTheAction.value) {
            EDIT_NOTE -> editNote()
            DECRYPTION_NOTE -> decryptionNote()
            DELETE_NOTE -> deleteNote()
        }
    }

    private fun editNote() {
        goToEditNote()
    }

    private fun deleteNote() = viewModelScope.launch {
        this@NoteDetailViewModel.noteId.value?.run {
            this@NoteDetailViewModel.noteRepository.deleteNote(this)
            goToListNote()
        }
    }

    private fun decryptionNote() {
        this._note.value?.run {
            if (!encryptionKey.isNullOrBlank()) {
                viewModelScope.launch {
                    val decryptedBody = Utils.decryptionText(body, encryptionKey)

                    val note = Note(title, decryptedBody, date, pin, encryptionKey, id)
                    this@NoteDetailViewModel.note.value = note

                    this@NoteDetailViewModel.isDecryptionNoteAvailable.value = false
                }
            }
        }
    }

    private fun goToEditNote() {
        _goToEditNotesEvent.value = Event(Unit)
    }

    private fun goToListNote() {
        _goToListNoteEvent.value = Event(Unit)
    }
}