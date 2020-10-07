package com.muhammadiqbalafandi.enotes.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragNoteBinding
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.hideKeyboard
import com.muhammadiqbalafandi.enotes.utils.setupSnackbar
import timber.log.Timber

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
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSnackbar()
        setupListAdapter()
        setupObserver()
        hideKeyboard()
        hideFAB()
    }

    private fun setupObserver() {
        viewModel.goToDetailNoteEvent.observe(viewLifecycleOwner, EventObserver { noteId ->
            val action = NoteFragmentDirections.actionNoteFragmentToNoteDetailFragment(noteId)
            findNavController().navigate(action)
        })
        viewModel.goToAddNewNoteEvent.observe(viewLifecycleOwner, EventObserver {
            val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(
                null,
                resources.getString(R.string.title_fragment_add_edit_note)
            )
            findNavController().navigate(action)
        })
        viewModel.popupMenu.observe(viewLifecycleOwner, EventObserver { view ->
            showFilteringPopUpMenu(view)
        })
        viewModel.goToActivitySettingEvent.observe(viewLifecycleOwner, EventObserver {
            val action = NoteFragmentDirections.actionNoteFragmentToSettingsActivity()
            findNavController().navigate(action)
        })
        viewModel.searchResult.observe(viewLifecycleOwner, Observer { listNote ->
            listAdapter.submitList(listNote)
        })
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewModel

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if (viewModel != null) {
            listAdapter = NoteAdapter(viewModel)
            viewDataBinding.rvListNote.layoutManager = linearLayoutManager
            viewDataBinding.rvListNote.adapter = listAdapter
        } else {
            Timber.d("setupListAdapter: ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(
            viewDataBinding.fabNote,
            viewLifecycleOwner,
            viewModel.snackbarText,
            Snackbar.LENGTH_SHORT
        )
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu(parent: View) {
        PopupMenu(requireContext(), parent).run {
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

    // Hide Floating Action Button when recyclerview on scroll.
    private fun hideFAB() {
        viewDataBinding.rvListNote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && viewDataBinding.fabNote.visibility == View.VISIBLE) {
                    viewDataBinding.fabNote.hide()
                } else if (dy < 0 && viewDataBinding.fabNote.visibility != View.VISIBLE) {
                    viewDataBinding.fabNote.show()
                }
            }
        })
    }
}