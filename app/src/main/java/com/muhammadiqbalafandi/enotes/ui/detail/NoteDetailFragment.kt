package com.muhammadiqbalafandi.enotes.ui.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragDetailNoteBinding
import com.muhammadiqbalafandi.enotes.databinding.NavHeaderDetailNoteBinding
import com.muhammadiqbalafandi.enotes.ui.note.DELETE_RESULT_OK
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.hideKeyboard
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
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(viewDataBinding.toolbarDetailNote)
            title = resources.getString(R.string.title_fragment_note_detail)
            supportActionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_twotone_arrow_back
                )
            )
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        viewModel.start(args.noteId)
        setupNavigationView()
        setupObserver()
        hideKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toolbar_detail_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_menu -> {
                viewDataBinding.drawerDetailNote.openDrawer(GravityCompat.END)
            }
            android.R.id.home -> {
                requireActivity().onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (resultCode == Activity.RESULT_OK && resultData != null) {
            val uri = resultData.data
            if (requestCode == REQUEST_CODE_IMPORT_FILE) {
                viewModel.importFileText(uri)
            }
        }
    }

    private fun setupObserver() {
        viewModel.goToEditNotesEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(
                    args.noteId,
                    resources.getString(R.string.snackbar_message_edit_note)
                )
            findNavController().navigate(action)
        })
        viewModel.goToListNoteEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                NoteDetailFragmentDirections.actionNoteDetailToNoteFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
        viewModel.showDialogDecryptionNoteEvent.observe(viewLifecycleOwner, EventObserver {
            showDialogDecryptionNote()
        })
        viewModel.showDialogDeleteNoteEvent.observe(viewLifecycleOwner, EventObserver {
            showDialogDeleteNote()
        })
    }

    private fun setupNavigationView() {
        val navViewDataBinding = NavHeaderDetailNoteBinding.bind(viewDataBinding.navViewDetailNote.getHeaderView(0))
        navViewDataBinding.lifecycleOwner = viewLifecycleOwner
        navViewDataBinding.viewModel = viewModel
    }

    private fun showDialogDecryptionNote() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_decryption_note)
            .setMessage(R.string.dialog_message_decryption_note)
            .setPositiveButton(R.string.button_text_import_key) { _, _ ->
                setupPermission()
            }
            .show()
    }

    private fun setupPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                actionOpenDocument()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedTitle(R.string.permission_title_storage)
            .setDeniedMessage(R.string.permission_description_storage)
            .setDeniedCloseButtonText(android.R.string.cancel)
            .setGotoSettingButtonText(R.string.dialog_positive_text_go_to_settings)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun actionOpenDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(
            intent,
            REQUEST_CODE_IMPORT_FILE
        )
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

// Key for Activity Result.
const val REQUEST_CODE_IMPORT_FILE = Activity.RESULT_FIRST_USER + 1