package com.muhammadiqbalafandi.enotes.ui.detailnote

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragDetailNoteBinding
import com.muhammadiqbalafandi.enotes.ui.note.DELETE_RESULT_OK
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.setupSnackbar

class NoteDetailFragment : Fragment() {

    private lateinit var viewDataBinding: FragDetailNoteBinding

    private val args: NoteDetailFragmentArgs by navArgs()

    private val viewModel: NoteDetailViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragDetailNoteBinding.inflate(inflater, container, false)
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewModel.start(args.noteId)
        setupFab()
        setupSnackbar()
        setupNavigation()
        setupNavigationView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_detail_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_menu) {
            viewDataBinding.drawerLayoutDetailNote.openDrawer(GravityCompat.END)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.editNoteEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(
                    args.noteId,
                    resources.getString(R.string.edit_note)
                )
            findNavController().navigate(action)
        })

        viewModel.deleteNoteEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                NoteDetailFragmentDirections.actionNoteDetailToNoteFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        viewDataBinding.fabDetailNote.setOnClickListener {
            viewModel.editNote()
        }
    }

    private fun setupNavigationView() = viewDataBinding.navViewDetailNote.apply {
        setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_delete -> {
                    viewModel.deleteNote()
                    true
                }
               R.id.menu_encryption_text -> {
                   Toast.makeText(requireContext(), "setupNavigationView: Encryption Text", Toast.LENGTH_SHORT).show()
                   true
               }
                else -> false
            }
        }
    }
}