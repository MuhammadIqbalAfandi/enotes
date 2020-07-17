package com.muhammadiqbalafandi.enotes.ui.encryptiontextnote

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class EncryptionTextViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Application = getApplication()

    // Two-way databinding, exposing MutableLiveData
    val encryptionKey = MutableLiveData<String>()

    private val oldEncryptionKey = MutableLiveData<String>()

    private val time: Date = Calendar.getInstance().time

    @SuppressLint("SimpleDateFormat")
    val timeStamp: String = SimpleDateFormat("ddMMyyyy_HH:mm:ss").format(time)

    private val _errorText = MutableLiveData<Int>()
    val errorText: LiveData<Int> = _errorText

    private val _backAddEditNoteEvent = MutableLiveData<Event<Int>>()
    val backAddEditNoteEvent: LiveData<Event<Int>> = _backAddEditNoteEvent

    private val _noteId = MutableLiveData<String>()
    val noteId: LiveData<String> = _noteId

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            ENCRYPTION_KEY_PREFERENCES, Activity.MODE_PRIVATE
        )

    fun start(encryptionKey: String?) {
        this.encryptionKey.value = encryptionKey
        oldEncryptionKey.value = encryptionKey
    }

    // Generate string random to encryption key.
    fun generateKey() {
        encryptionKey.value = UUID.randomUUID().toString()

        _errorText.value = null
    }

    fun saveEncryptionText() {
        sharedPreferences.edit()
            .putString(ENCRYPTION_KEY_SAVED_STATE_KEY, encryptionKey.value)
            .apply()
    }

    fun restoreKey() {
        encryptionKey.value = oldEncryptionKey.value
    }

    fun exportFile() {
        val encryptionKey = encryptionKey.value

        if (encryptionKey.isNullOrEmpty()) {
            _errorText.value = R.string.message_error_null
            return
        }

        if (isStringAllWhiteSpace(encryptionKey)) {
            _errorText.value = R.string.message_error_all_white_space
            return
        }

        try {
            val path: File = context.filesDir
            val dir = File(path, "/ENotes/")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val fileName = "$timeStamp.txt"
            val file = File(dir, fileName)

            val fileWriter = FileWriter(file.absoluteFile)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(encryptionKey)
            bufferedWriter.close()
            Toast.makeText(context, "$fileName is saved to \n $dir", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            // TODO: 16/07/20 Replace with timber.
            Log.d(ContentValues.TAG, "exportFile: $e")
        }
    }

    fun exit() {
        if (encryptionKey.value.isNullOrEmpty()) {
            encryptionKey.value = null
        }

        val currentEncryptionKey = encryptionKey.value
        val oldEncryptionKey = oldEncryptionKey.value

        if (currentEncryptionKey != null) {
            if (isStringAllWhiteSpace(currentEncryptionKey)) {
                _errorText.value = R.string.message_error_all_white_space
                return
            }
        }

        _errorText.value = null

        if (currentEncryptionKey != oldEncryptionKey) {
            when {
                currentEncryptionKey == null -> {
                    _backAddEditNoteEvent.value = Event(ALERT_DIALOG_NULL)
                }
                oldEncryptionKey == null -> {
                    _backAddEditNoteEvent.value = Event(NOTHING_ALERT_DIALOG)
                }
                else -> {
                    _backAddEditNoteEvent.value = Event(ALERT_DIALOG_NEW_KEY)
                }
            }
            return
        }

        _backAddEditNoteEvent.value = Event(NOTHING_ALERT_DIALOG)
    }

    /**
     * Function to check if the String is all whitespaces.
     *
     * References
     * https://www.geeksforgeeks.org/program-to-check-if-a-string-in-java-contains-only-whitespaces/
     *
     * @param str string to be in check.
     * @return
     */
    private fun isStringAllWhiteSpace(str: String): Boolean {
        // Remove the leading whitespaces using trim()
        // and then check if this string is empty
        return str.trim().isEmpty()
    }
}

// Used to save the current encryption key in SharedPreferences.
const val ENCRYPTION_KEY_SAVED_STATE_KEY = "ENCRYPTION_KEY_SAVED_STATE_KEY"
const val ENCRYPTION_KEY_PREFERENCES = "ENCRYPTION_KEY_PREFERENCES"