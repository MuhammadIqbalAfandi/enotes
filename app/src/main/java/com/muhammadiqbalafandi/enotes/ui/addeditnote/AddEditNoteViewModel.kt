package com.muhammadiqbalafandi.enotes.ui.addeditnote

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import com.muhammadiqbalafandi.enotes.ui.encryptiontextnote.ENCRYPTION_KEY_PREFERENCES
import com.muhammadiqbalafandi.enotes.ui.encryptiontextnote.ENCRYPTION_KEY_SAVED_STATE_KEY
import com.muhammadiqbalafandi.enotes.utils.Utils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteViewModel(
    private val noteRepository: NoteRepository,
    application: Application
) : AndroidViewModel(application) {

    // Two-way databinding, exposing MutableLiveData
    var title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    var body = MutableLiveData<String>()

    private val _encryptionKeyEvent = MutableLiveData<Event<Unit>>()
    val encryptionKeyEvent: LiveData<Event<Unit>> = _encryptionKeyEvent

    private val _encryptionKey = MutableLiveData<String>()
    val encryptionKey: LiveData<String> = _encryptionKey

    private val _pin = MutableLiveData<Boolean>(false)
    val pin: LiveData<Boolean> = _pin

    // Time configuration.
    private val time: Date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat")
    private val fullTime: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(time)

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _noteUpdateEvent = MutableLiveData<Event<Unit>>()
    val noteUpdateEvent: LiveData<Event<Unit>> = _noteUpdateEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var isNewNote: Boolean = false

    private var isDataLoaded: Boolean = false

    private val _noteId = MutableLiveData<String>()
    val noteId: LiveData<String> = _noteId

    private var sharedPreferences: SharedPreferences =
        getApplication<Application>().getSharedPreferences(
            ENCRYPTION_KEY_PREFERENCES, Activity.MODE_PRIVATE
        )

    fun start(noteId: String?) {

        if (_dataLoading.value == true) {
            return
        }

        this._noteId.value = noteId
        if (noteId == null) {
            // No need to populate, it's a new note
            isNewNote = true
            return
        }

        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        isNewNote = false
        _dataLoading.value = true
        viewModelScope.launch {
            noteRepository.getNote(noteId).let {
                if (it is Success) {
                    onNoteLoaded(it.data)
                } else {
                    onDataNoteAvailable()
                }
            }
        }
    }

    private fun onDataNoteAvailable() {
        _dataLoading.value = false
    }

    private fun onNoteLoaded(note: Note) {
        title.value = note.title
        body.value = note.body
        _pin.value = note.pin
        _encryptionKey.value = note.encryptionKey

        _dataLoading.value = false
        isDataLoaded = true
    }

    /**
     * Pin is a feature to mark notes, whether the note is important or not.
     * @return boolean when the pin is active return true and when is's not return false
     */
    fun pin(): Boolean? {
        _pin.value = !pin.value!!
        return pin.value!!
    }

    fun getSavedEncryptionKey() {
        val sharedPrefResult = sharedPreferences.getString(ENCRYPTION_KEY_SAVED_STATE_KEY, null)
        _encryptionKey.value = sharedPrefResult
    }

    fun openEncryptionText() {
        _encryptionKeyEvent.value = Event(Unit)
    }

    fun saveNote() {
        val currentTitle = title.value
        val currentBody = body.value
        val currentTime = fullTime
        val currentPin = pin.value!!
        val currentKey = encryptionKey.value

        if (currentBody.isNullOrEmpty()) {
            _snackbarText.value = Event(R.string.no_body)
            return
        }

        /**
         * Encryption body note.
         *
         * @return string body encryption.
         */
        fun encryptionText(): String {
            return if (!currentKey.isNullOrEmpty())
                Utils.encryptionText(currentBody, currentKey) else currentBody
        }

        val currentNoteId = noteId.value
        if (isNewNote || currentNoteId == null) {
            val note = Note(
                currentTitle,
                encryptionText(),
                currentTime,
                currentPin,
                currentKey
            )
            createNote(note)
        } else {
            val note = Note(
                currentTitle,
                encryptionText(),
                currentTime,
                currentPin,
                currentKey,
                currentNoteId
            )
            updateNote(note)
        }
    }

    private fun updateNote(note: Note) {
        if (isNewNote) {
            throw RuntimeException("updateNote() was called but note is new.")
        }
        viewModelScope.launch {
            // Connected to the database to store data.
            noteRepository.saveNote(note)
            _noteUpdateEvent.value = Event(Unit)
        }
    }

    private fun createNote(note: Note) = viewModelScope.launch {
        // Connected to the database to store data.
        noteRepository.saveNote(note)
        _noteUpdateEvent.value = Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        sharedPreferences.edit().clear().apply()
    }
}
