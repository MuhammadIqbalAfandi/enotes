package com.muhammadiqbalafandi.enotes.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.muhammadiqbalafandi.enotes.NoteApplication
import com.muhammadiqbalafandi.enotes.ViewModelFactory
import com.muhammadiqbalafandi.enotes.data.ServiceLocator
import org.jetbrains.annotations.NotNull

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return ViewModelFactory(repository, requireActivity().application, this)
}

fun Fragment.hideKeyboard() {
    val imm = getInputMethodManager(requireContext())
    val target = requireActivity().window.decorView
    imm.hideSoftInputFromWindow(target.windowToken, 0)
}

fun Fragment.showKeyboard(target: EditText) {
    val imm = getInputMethodManager(requireContext())
    imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT)
}

private fun getInputMethodManager(context: Context): InputMethodManager {
    return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}

fun Fragment.setupBackNavigation(custom: () -> Unit) {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            custom()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}