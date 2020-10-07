package com.muhammadiqbalafandi.enotes

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.ui.addedit.AddEditNoteViewModel
import com.muhammadiqbalafandi.enotes.ui.detail.NoteDetailViewModel
import com.muhammadiqbalafandi.enotes.ui.encryptiontext.EncryptionTextViewModel
import com.muhammadiqbalafandi.enotes.ui.note.NoteViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val noteRepository: NoteRepository,
    private val application: Application,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(AddEditNoteViewModel::class.java) -> AddEditNoteViewModel(
                noteRepository,
                application
            )
            isAssignableFrom(NoteViewModel::class.java) -> NoteViewModel(
                noteRepository,
                handle,
                application
            )
            isAssignableFrom(NoteDetailViewModel::class.java) -> NoteDetailViewModel(
                noteRepository,
                application
            )
            isAssignableFrom(EncryptionTextViewModel::class.java) -> EncryptionTextViewModel(
                application
            )
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}