package com.muhammadiqbalafandi.enotes.ui.encryptiontext

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.muhammadiqbalafandi.enotes.databinding.FragEncryptionTextBinding
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.setupBackNavigation
import com.muhammadiqbalafandi.enotes.utils.setupSnackbar

class EncryptionTextFragment : Fragment() {

    private lateinit var viewDataBinding: FragEncryptionTextBinding

    private val args: EncryptionTextFragmentArgs by navArgs()

    private val viewModel: EncryptionTextViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragEncryptionTextBinding.inflate(inflater, container, false)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.start(args.encryptionKey)
        setupToolbar()
        setupObserver()
        setupSnackBar()
        setupCheckPermission()
        setupBackNavigation {
            viewModel.exit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (resultCode == Activity.RESULT_OK && resultData != null) {
            val uri = resultData.data
            when (requestCode) {
                REQUEST_CODE_IMPORT_FILE -> viewModel.importFileText(uri)
                REQUEST_CODE_EXPORT_FILE -> viewModel.exportFileText(uri)
            }
        }
    }

    private fun setupToolbar() {
        viewDataBinding.toolbarEncryptionText.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupCheckPermission() {
        viewModel.checkPermissionEvent.observe(viewLifecycleOwner, EventObserver {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    when (it) {
                        EncryptionActionType.IMPORT_KEY -> actionOpenDocument()
                        EncryptionActionType.EXPORT_KEY -> actionCreateDocument()
                    }
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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .check()
        })
    }

    private fun actionOpenDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_IMPORT_FILE)
    }

    private fun actionCreateDocument() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_EXPORT_FILE)
    }

    private fun setupSnackBar() {
        view?.setupSnackbar(
            requireView(),
            viewLifecycleOwner,
            viewModel.snackbarTextEvent,
            Snackbar.LENGTH_SHORT
        )
    }

    private fun setupObserver() {
        viewModel.goToAddEditNoteEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                ALERT_DIALOG_NULL -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dialog_title_not_save_encryption_key)
                        .setMessage(R.string.dialog_message_not_save_encryption_key)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            viewModel.saveEncryptionText()
                            findNavController().popBackStack()
                        }
                        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.restoreKey()
                        }
                        .show()
                }
                ALERT_DIALOG_NEW_KEY -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dialog_title_new_encryption_key)
                        .setMessage(R.string.dialog_message_new_encryption_key)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            viewModel.saveEncryptionText()
                            findNavController().popBackStack()
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ ->
                            viewModel.restoreKey()
                            findNavController().popBackStack()
                        }
                        .show()
                }
                NOTHING_ALERT_DIALOG -> {
                    viewModel.saveEncryptionText()
                    findNavController().popBackStack()
                }
            }
        })
    }
}

// Status code to dialog
const val ALERT_DIALOG_NEW_KEY = Activity.RESULT_FIRST_USER + 1
const val ALERT_DIALOG_NULL = Activity.RESULT_FIRST_USER + 2
const val NOTHING_ALERT_DIALOG = Activity.RESULT_FIRST_USER + 3

// Status code for Activity Result.
const val REQUEST_CODE_IMPORT_FILE = Activity.RESULT_FIRST_USER + 1
const val REQUEST_CODE_EXPORT_FILE = Activity.RESULT_FIRST_USER + 2