package com.muhammadiqbalafandi.enotes.ui.note

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muhammadiqbalafandi.enotes.data.source.local.Note

/**
 * [BindingAdapter] for  the [Note] list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Note>?) {
    items?.let {
        (listView.adapter as NoteAdapter).submitList(items)
    }
}