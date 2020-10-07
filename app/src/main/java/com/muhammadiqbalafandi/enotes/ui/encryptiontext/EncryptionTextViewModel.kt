package com.muhammadiqbalafandi.enotes.ui.encryptiontext

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragEncryptionTextBinding
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

class EncryptionTextViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Application = getApplication()

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            ENCRYPTION_KEY_PREFERENCES, Activity.MODE_PRIVATE
        )

    /**
     * Used directly in [FragEncryptionTextBinding].
     */
    val encryptionKey = MutableLiveData<String>()

    // Hide button export when encryption key null, empty or all white space.
    val isEncryptionKeyAvailable: LiveData<Boolean> = encryptionKey.map {
        if (it !== null) {
            !isStringAllWhiteSpace(it)
        } else {
            false
        }
    }

    private val oldEncryptionKey = MutableLiveData<String>()

    private val _errorText = MutableLiveData<Int>()
    val errorText: LiveData<Int> = _errorText

    // Used in fragments, to listen for changes.
    private val _checkPermissionEvent = MutableLiveData<Event<EncryptionActionType>>()
    val checkPermissionEvent: LiveData<Event<EncryptionActionType>> = _checkPermissionEvent

    private val _goToAddEditNoteEvent = MutableLiveData<Event<Int>>()
    val goToAddEditNoteEvent: LiveData<Event<Int>> = _goToAddEditNoteEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    fun start(encryptionKey: String?) {
        this.encryptionKey.value = encryptionKey
        this.oldEncryptionKey.value = encryptionKey
    }

    /**
     * Used directly in [FragEncryptionTextBinding]
     */
    fun generateKey() {
        this.encryptionKey.value = UUID.randomUUID().toString()

        this._errorText.value = null
    }

    fun restoreKey() {
        this.encryptionKey.value = this.oldEncryptionKey.value
    }

    /**
     * Used directly in [FragEncryptionTextBinding]
     */
    fun checkPermissionEvent(actionType: EncryptionActionType) {
        this._checkPermissionEvent.value = Event(actionType)
    }

    // To get file from disk.
    fun importFileText(uri: Uri?) {
        try {
            if (uri != null) {
                val inputStream = this.context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)

                    // Read content and display as encryptionKey value.
                    this.encryptionKey.value = bufferedReader.readLine()

                    bufferedReader.close()
                }
            }
        } catch (e: Exception) {
            // TODO: 8/8/20 ERROR : Always return null when import key that empty content.
            Timber.e(e)
        }
    }

    // Used to saved file in disk.
    fun exportFileText(uri: Uri?) {
        val encryptionKey = this.encryptionKey.value

        if (encryptionKey.isNullOrBlank()) return

        try {
            if (uri != null) {
                val outputStream = this.context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    val bufferedWriter = BufferedWriter(outputStreamWriter)
                    /**
                     * Enter [encryptionKey] to save in the .txt format.
                     */
                    bufferedWriter.write(encryptionKey)
                    bufferedWriter.close()

                    showSnackbarMessage(R.string.message_success_export_key)
                }
            }
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    fun exit() {
        if (this.encryptionKey.value.isNullOrEmpty()) {
            this.encryptionKey.value = null
        }

        val currentEncryptionKey = this.encryptionKey.value
        val oldEncryptionKey = this.oldEncryptionKey.value

        // Check whether encryption key all white space.
        if (currentEncryptionKey != null) {
            if (isStringAllWhiteSpace(currentEncryptionKey)) {
                this._errorText.value = R.string.message_error_all_white_space
                return
            }
        }

        this._errorText.value = null

        if (currentEncryptionKey != oldEncryptionKey) {
            when {
                currentEncryptionKey == null -> {
                    this._goToAddEditNoteEvent.value = Event(ALERT_DIALOG_NULL)
                }
                oldEncryptionKey == null -> {
                    this._goToAddEditNoteEvent.value = Event(NOTHING_ALERT_DIALOG)
                }
                else -> {
                    this._goToAddEditNoteEvent.value = Event(ALERT_DIALOG_NEW_KEY)
                }
            }
            return
        }

        this._goToAddEditNoteEvent.value = Event(NOTHING_ALERT_DIALOG)
    }

    private fun isStringAllWhiteSpace(str: String): Boolean {
        // Remove the leading whitespaces using trim()
        // and then check if this string is empty
        return str.trim().isEmpty()
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        this._snackbarText.value = Event(message)
    }

    fun saveEncryptionText() {
        sharedPreferences.edit()
            .putString(ENCRYPTION_KEY_SAVED_STATE_KEY, this.encryptionKey.value)
            .apply()
        this._errorText.value = null
    }
}

// Used as key preferences, to saved encryption key.
const val ENCRYPTION_KEY_SAVED_STATE_KEY = "ENCRYPTION_KEY_SAVED_STATE_KEY"
const val ENCRYPTION_KEY_PREFERENCES = "ENCRYPTION_KEY_PREFERENCES"