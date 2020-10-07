package com.muhammadiqbalafandi.enotes.ui.detail

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
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
import java.util.*

class NoteDetailViewModel(
    private val noteRepository: NoteRepository,
    application: Application
) : AndroidViewModel(application) {

    // Used in this class.
    private val context: Application = getApplication()

    private val _noteId = MutableLiveData<String>()

    private val _runTheAction = MutableLiveData<NoteDetailActionType>()

    private val _noteAreLoaded: LiveData<Note?> = _noteId.switchMap { noteId ->
        noteRepository.observerNote(noteId).map { computeResult(it) }
    }

    /**
     * Used directly in [FragDetailNoteBinding].
     */
    val title = MutableLiveData<String>()

    val body = MutableLiveData<String>()

    val date = MutableLiveData<Date>()

    val encryptionKey = MutableLiveData<String>()

    // Show decryption button when encryption key exist.
    val isDecryptionNote = MutableLiveData<Boolean>()

    // Show error message when note is null.
    val isDataAvailable: LiveData<Boolean> = _noteAreLoaded.map { it == null }

    // Used in fragment, to listen changes.
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _goToEditNotesEvent = MutableLiveData<Event<Unit>>()
    val goToEditNotesEvent: LiveData<Event<Unit>> = _goToEditNotesEvent

    private val _goToListNoteEvent = MutableLiveData<Event<Unit>>()
    val goToListNoteEvent: LiveData<Event<Unit>> = _goToListNoteEvent

    private val _showDialogDecryptionNoteEvent = MutableLiveData<Event<Unit>>()
    val showDialogDecryptionNoteEvent: LiveData<Event<Unit>> = _showDialogDecryptionNoteEvent

    private val _showDialogDeleteNoteEvent = MutableLiveData<Event<Unit>>()
    val showDialogDeleteNoteEvent: LiveData<Event<Unit>> = _showDialogDeleteNoteEvent

    fun start(noteId: String?) {
        _noteId.value = noteId
    }

    private fun computeResult(noteResult: Result<Note>): Note? {
        if (noteResult is Success) {
            setContent(noteResult.data)
        } else {
            showSnackbarMessage(R.string.snackbar_message_loading_note_error)
        }
        return null
    }

    private fun setContent(noteResult: Note) {
        title.value = noteResult.title
        body.value = noteResult.body
        date.value = noteResult.date
        encryptionKey.value = noteResult.encryptionKey

        // Show decryption button when encryption key exist.
        isDecryptionNote.value = !encryptionKey.value.isNullOrEmpty()
    }

    fun whetherTheNoteIsEncrypted(actionType: NoteDetailActionType) {
        _runTheAction.value = actionType

        // Check whether the note is encrypted
        // if the note is encrypted, show dialog to user to input key.
        val encryptionKey = this.encryptionKey.value
        if (!encryptionKey.isNullOrEmpty()) {
            _showDialogDecryptionNoteEvent.value = Event(Unit)
            return
        }

        if (_runTheAction.value == DELETE_NOTE) {
            _showDialogDeleteNoteEvent.value = Event(Unit)
            return
        }

        setAction()
    }

    // To get file txt from disk.
    fun importFileText(uri: Uri?) {
        try {
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
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

    // Check if encryption key same with encryption key that is imported.
    private fun whetherTheImportTheSame(encryptionKeyImported: String) {
        if (encryptionKey.value == encryptionKeyImported) {
            setAction()
        } else {
            Toast.makeText(context, R.string.message_error_import_key, Toast.LENGTH_SHORT).show()
        }
    }

    // User agree to delete note.
    fun agreeDeletedNote() {
        setAction()
    }

    // Handle action such when user request notes to delete, edit, or note decryption.
    private fun setAction() {
        when (_runTheAction.value) {
            EDIT_NOTE -> editNote()
            DECRYPTION_NOTE -> decryptionNote()
            DELETE_NOTE -> deleteNote()
        }
    }

    private fun editNote() {
        goToEditNote()
    }

    private fun deleteNote() = viewModelScope.launch {
        _noteId.value?.let {
            noteRepository.deleteNote(it)
            goToListNote()
        }
    }

    private fun decryptionNote() {
        val body = body.value!!
        val encryptionKey = encryptionKey.value

        if (encryptionKey != null) {
            viewModelScope.launch {
                // Decryption note body.
                val decryptedBody = Utils.decryptionText(body, encryptionKey)

                this@NoteDetailViewModel.body.value = decryptedBody

                // Hide Button when note is decrypted.
                isDecryptionNote.value = false
            }
        }
    }

    private fun goToEditNote() {
        _goToEditNotesEvent.value = Event(Unit)
    }

    private fun goToListNote() {
        _goToListNoteEvent.value = Event(Unit)
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}