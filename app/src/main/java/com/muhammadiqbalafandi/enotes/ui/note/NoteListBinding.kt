package com.muhammadiqbalafandi.enotes.ui.note

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Note>?) {
    items?.let {
        (listView.adapter as NoteAdapter).submitList(items)
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("app:textDate")
fun setTextDate(textView: MaterialTextView, time: Date) {
    textView.let {
        val dateFormat: String = SimpleDateFormat("MMM dd, YYYY").format(time)
        it.text = dateFormat
    }
}