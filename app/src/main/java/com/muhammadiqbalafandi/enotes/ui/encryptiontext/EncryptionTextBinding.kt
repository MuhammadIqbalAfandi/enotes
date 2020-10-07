package com.muhammadiqbalafandi.enotes.ui.encryptiontext

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:errorText")
fun TextInputLayout.errorText(text: Int?) {
    if (text == null) {
        this.error = null
    } else {
        this.error = resources.getString(text)
    }
}