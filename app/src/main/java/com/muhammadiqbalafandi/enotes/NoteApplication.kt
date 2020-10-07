package com.muhammadiqbalafandi.enotes

import android.app.Application
import com.muhammadiqbalafandi.enotes.data.ServiceLocator
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import timber.log.Timber

class NoteApplication : Application() {

    val noteRepository: NoteRepository
        get() = ServiceLocator.provideNoteRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}