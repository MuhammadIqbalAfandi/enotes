package com.muhammadiqbalafandi.enotes.ui.addedit

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragAddEditNoteBinding
import com.muhammadiqbalafandi.enotes.databinding.NavHeaderAddEditNoteBinding
import com.muhammadiqbalafandi.enotes.ui.encryptiontext.ENCRYPTION_KEY_SAVED_STATE_KEY
import com.muhammadiqbalafandi.enotes.ui.encryptiontext.EncryptionActionType
import com.muhammadiqbalafandi.enotes.ui.encryptiontext.EncryptionTextViewModel
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.hideKeyboard
import com.muhammadiqbalafandi.enotes.utils.setupSnackbar

class AddEditNoteFragment : Fragment() {

    private lateinit var viewDataBinding: FragAddEditNoteBinding

    private val args: AddEditNoteFragmentArgs by navArgs()

    private val viewModel: AddEditNoteViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.clearEncryptionKey()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragAddEditNoteBinding.inflate(inflater, container, false)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(viewDataBinding.toolbarAddEditNote)
            title = args.toolbalTitle
            supportActionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_twotone_arrow_back
                )
            )
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        viewModel.start(args.noteId)
        viewModel.getSavedEncryptionKey()
        setupNavigationView()
        setupObserver()
        setupSnackbar()
        hideKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_add_edit_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_menu -> {
                viewDataBinding.drawerAddeditNote.openDrawer(GravityCompat.END)
                hideKeyboard()
            }
            android.R.id.home -> {
                requireActivity().onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(
            viewDataBinding.fabAddEditNote,
            viewLifecycleOwner,
            viewModel.snackbarText,
            Snackbar.LENGTH_SHORT
        )
    }

    private fun setupObserver() {
        viewModel.goToListNoteEvent.observe(viewLifecycleOwner, EventObserver { resultCode ->
            val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToNoteFragment(
                resultCode
            )
            findNavController().navigate(action)
        })
        viewModel.goToEncryptionNoteEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                AddEditNoteFragmentDirections.actionAddNoteFragmentToEncryptionTextFragment(
                     viewModel.encryptionKey.value
                )
            findNavController().navigate(action)
        })
        viewModel.showDialogDeleteNoteEvent.observe(viewLifecycleOwner, EventObserver {
            showDialogDeleteNote()
        })
    }

    private fun setupNavigationView() {
        val headerView = NavHeaderAddEditNoteBinding.bind(viewDataBinding.navViewAddeditNote.getHeaderView(0))
        headerView.lifecycleOwner = viewLifecycleOwner
        headerView.viewModel = viewModel
    }

    private fun showDialogDeleteNote() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_delete_note)
            .setMessage(R.string.dialog_message_delete_note)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.agreeDeletedNote()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}