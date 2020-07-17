package com.muhammadiqbalafandi.enotes.utils

import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muhammadiqbalafandi.enotes.NoteApplication
import com.muhammadiqbalafandi.enotes.ViewModelFactory
import com.muhammadiqbalafandi.enotes.data.ServiceLocator
import com.muhammadiqbalafandi.enotes.ui.encryptiontextnote.EncryptionTextFragment

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = ServiceLocator.provideNoteRepository(requireContext())
    return ViewModelFactory(repository, requireActivity().application, this)
}

fun Fragment.hideSoftInput() {
    val imm =
        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Fragment.showSoftInput() {
    val imm =
        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun Fragment.customBackNavigation(custom: () -> Unit) {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            custom()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}

fun Fragment.setupMaterialAlertDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    noticeDialogListener: NoticeDialogListener,
    @StringRes negativeButtonTitle: Int = android.R.string.cancel,
    @StringRes positiveButtonTitle: Int = android.R.string.yes
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeButtonTitle) { dialog, _ ->
            noticeDialogListener.onDialogNegativeClick(dialog)
        }
        .setPositiveButton(positiveButtonTitle) { dialog, _ ->
            noticeDialogListener.onDialogPositiveClick(dialog)
        }
        .show()
}

interface NoticeDialogListener {
    fun onDialogPositiveClick(dialog: DialogInterface)
    fun onDialogNegativeClick(dialog: DialogInterface)
}