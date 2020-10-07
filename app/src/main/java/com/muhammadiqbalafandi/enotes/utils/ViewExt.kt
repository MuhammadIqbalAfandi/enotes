package com.muhammadiqbalafandi.enotes.utils

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.muhammadiqbalafandi.enotes.Event

fun showSnackbar(parent: View, snackbarText: String, timeLength: Int) {
    Snackbar.make(parent, snackbarText, timeLength).show()
}


fun View.setupSnackbar(
    parent: View,
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(parent, context.getString(it), timeLength)
        }
    })
}