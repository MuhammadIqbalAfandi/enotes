package com.muhammadiqbalafandi.enotes.ui.note

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragNoteBinding
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.setupSnackbar

class NoteFragment : Fragment() {

    private lateinit var viewDataBinding: FragNoteBinding

    private val args: NoteFragmentArgs by navArgs()

    private val viewModel: NoteViewModel by viewModels { getViewModelFactory() }

    private lateinit var listAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragNoteBinding.inflate(inflater, container, false)
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            else -> false
        }

    private fun setupNavigation() {
        viewModel.openNoteEvent.observe(viewLifecycleOwner, EventObserver {
            openEditNote(it)
        })
        viewModel.addNewNote.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewNote()
        })
    }

    private fun openEditNote(noteId: String) {
        val action = NoteFragmentDirections.actionNoteFragmentToNoteDetailFragment(
            noteId
        )
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewModel
        if (viewModel != null) {
            listAdapter = NoteAdapter(viewModel)
            viewDataBinding.rvListNote.adapter = listAdapter
        } else {
            // TODO: Replace with Timber
            Log.w(
                NoteFragment().tag,
                "setupListAdapter: ViewModel not initialized when attempting to set up adapter."
            )
        }
    }

    private fun navigateToAddNewNote() {
        val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(
            null,
            resources.getString(R.string.new_note)
        )
        findNavController().navigate(action)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
            // TODO: Replace with Timber
            Log.d("ADDED", "setupSnackbar: ${args.userMessage}")
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_note, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.menu_pin_note -> NoteFilterType.PIN_NOTE
                        R.id.menu_encryption_note -> NoteFilterType.ENCRYPTION_NOTE
                        else -> NoteFilterType.ALL_NOTE
                    }
                )
                true
            }
            show()
        }
    }
}