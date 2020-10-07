package com.muhammadiqbalafandi.enotes.ui.addedit

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import com.muhammadiqbalafandi.enotes.databinding.FragAddEditNoteBinding
import com.muhammadiqbalafandi.enotes.databinding.NavHeaderAddEditNoteBinding
import com.muhammadiqbalafandi.enotes.ui.encryptiontext.ENCRYPTION_KEY_PREFERENCES
import com.muhammadiqbalafandi.enotes.ui.encryptiontext.ENCRYPTION_KEY_SAVED_STATE_KEY
import com.muhammadiqbalafandi.enotes.ui.note.ADD_RESULT_OK
import com.muhammadiqbalafandi.enotes.ui.note.DELETE_RESULT_OK
import com.muhammadiqbalafandi.enotes.ui.note.EDIT_RESULT_OK
import com.muhammadiqbalafandi.enotes.utils.Utils
import kotlinx.coroutines.launch
import java.util.*

class AddEditNoteViewModel(
    private val noteRepository: NoteRepository, application: Application
) : AndroidViewModel(application) {

    private val context: Application = getApplication()

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            ENCRYPTION_KEY_PREFERENCES, Activity.MODE_PRIVATE
        )

    private val _noteId = MutableLiveData<String>()

    private val time: Date = Calendar.getInstance().time

    private var isNewNote: Boolean = false

    private var isDataLoaded: Boolean = false

    /**
     * Used directly in [FragAddEditNoteBinding].
     */
    val title = MutableLiveData<String>()

    val body = MutableLiveData<String>()

    private val pin = MutableLiveData(false)

    val encryptionKey = MutableLiveData<String>()

    /**
     * Used directly in [NavHeaderAddEditNoteBinding].
     * Set menu title in drawer navigation view.
     */
    val pinStringRes: LiveData<Int> = pin.map {
        if (it) R.string.menu_title_unpin else R.string.menu_title_pin
    }

    val encryptionStringRes: LiveData<Int> = encryptionKey.map {
        if (!it.isNullOrEmpty()) R.string.menu_title_encryption_text_active else R.string.menu_title_encryption_text
    }

    // Used in fragment, to listen changes.
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _goToEncryptionNoteEvent = MutableLiveData<Event<Unit>>()
    val goToEncryptionNoteEvent: LiveData<Event<Unit>> = _goToEncryptionNoteEvent

    private val _goToListNoteEvent = MutableLiveData<Event<Int>>()
    val goToListNoteEvent: LiveData<Event<Int>> = _goToListNoteEvent

    private val _showDialogDeleteNoteEvent = MutableLiveData<Event<Unit>>()
    val showDialogDeleteNoteEvent: LiveData<Event<Unit>> = _showDialogDeleteNoteEvent

    fun start(noteId: String?) {
        this._noteId.value = noteId

        if (noteId == null) {
            // No need to populate, it's a new note
            this.isNewNote = true
            return
        }

        if (this.isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        this.isNewNote = false
        viewModelScope.launch {
            noteRepository.getNote(noteId).let {
                if (it is Success) {
                    onNotesValidation(it.data)
                } else {
                    // TODO: 03/10/20 test to error, what will happen.
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onNotesValidation(noteResult: Note) {
        val encryptionKey = noteResult.encryptionKey
        if (!encryptionKey.isNullOrBlank()) {
            noteDecryption(noteResult)
        } else {
            onNotesLoaded(noteResult, null)
        }
    }

    private fun onDataNotAvailable() {
        showSnackbarMessage(R.string.message_error_no_note)
    }

    private fun noteDecryption(noteResult: Note) {
        val body = noteResult.body
        val encryptionKey = noteResult.encryptionKey!!
        viewModelScope.launch {
            // Result note decryption.
            val decryptedBody = Utils.decryptionText(body, encryptionKey)

            onNotesLoaded(noteResult, decryptedBody)
        }
    }

    private fun onNotesLoaded(noteResult: Note, decryptedBody: String?) {
        title.value = noteResult.title
        body.value = decryptedBody ?: noteResult.body
        pin.value = noteResult.pin
        encryptionKey.value = noteResult.encryptionKey

        isDataLoaded = true
    }

    /**
     * Used directly in [NavHeaderAddEditNoteBinding].
     */
    fun pin() {
        pin.value = !pin.value!!
    }

    fun goToEncryptionNote() {
        _goToEncryptionNoteEvent.value = Event(Unit)
    }

    fun showDialogDeleteNote() {
        _showDialogDeleteNoteEvent.value = Event(Unit)
    }

    // User agree to delete note.
    fun agreeDeletedNote() {
        deleteNote()
    }

    private fun deleteNote() = viewModelScope.launch {
        _noteId.value?.let {
            noteRepository.deleteNote(it)
            goToListNote(DELETE_RESULT_OK)
        }
    }

    fun saveNote() {
        val title = title.value
        val body = body.value
        val time = time
        val pin = pin.value ?: false
        val encryptionKey = encryptionKey.value
        val id = _noteId.value

        if (body.isNullOrBlank()) {
            showSnackbarMessage(R.string.message_error_no_body)
            return
        }

        fun encryptionText(): String {
            return if (encryptionKey.isNullOrBlank()) {
                body
            } else {
                Utils.encryptionText(body, encryptionKey)
            }
        }

        if (isNewNote || id == null) {
            val note = Note(title, encryptionText(), time, pin, encryptionKey)
            createNote(note)
        } else {
            val note = Note(title, encryptionText(), time, pin, encryptionKey, id)
            updateNote(note)
        }
    }

    private fun createNote(note: Note) = viewModelScope.launch {
        // Connected to the database to store data.
        noteRepository.saveNote(note)

        goToListNote(ADD_RESULT_OK)
    }

    private fun updateNote(note: Note) {
        if (isNewNote) {
            throw RuntimeException("updateNote() was called but note is new.")
        }
        viewModelScope.launch {
            // Connected to the database to store data.
            noteRepository.saveNote(note)

            goToListNote(EDIT_RESULT_OK)
        }
    }

    /**
     * Get encryption key value  that saved,
     * when back from [com.muhammadiqbalafandi.enotes.ui.encryptiontext].
     */
    fun getSavedEncryptionKey() {
        val sharedPrefResult = sharedPreferences.getString(ENCRYPTION_KEY_SAVED_STATE_KEY, null)
        this.encryptionKey.value = sharedPrefResult
    }

    fun clearEncryptionKey() {
        sharedPreferences.edit().clear().apply()
    }

    private fun goToListNote(resultCode: Int) {
        _goToListNoteEvent.value = Event(resultCode)
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
