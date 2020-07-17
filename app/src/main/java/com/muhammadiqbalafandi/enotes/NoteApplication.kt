package com.muhammadiqbalafandi.enotes

import android.app.Application
import com.muhammadiqbalafandi.enotes.data.ServiceLocator
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository

class NoteApplication : Application() {

    val noteRepository: NoteRepository
        get() = ServiceLocator.provideNoteRepository(this)
}