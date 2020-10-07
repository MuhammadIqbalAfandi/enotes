package com.muhammadiqbalafandi.enotes.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import com.muhammadiqbalafandi.enotes.databinding.ListItemNoteBinding

class NoteAdapter(private val viewModel: NoteViewModel) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(
    NoteDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            ListItemNoteBinding.inflate(layoutInflater, parent, false)

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    class NoteViewHolder (private val binding: ListItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: NoteViewModel, item: Note) {
            binding.viewModel = viewModel
            binding.note = item
            binding.executePendingBindings()
        }
    }
}

class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}