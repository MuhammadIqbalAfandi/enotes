package com.muhammadiqbalafandi.enotes.ui.encryptiontextnote

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragEncryptionTextBinding
import com.muhammadiqbalafandi.enotes.utils.*

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
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewModel.start(args.encryptionKey)
        setupNavigation()
        customBackNavigation {
            viewModel.exit()
            hideSoftInput()
        }

        viewDataBinding.btnImportEncryptionText.setOnClickListener {
        }

        viewDataBinding.btnExportEncryptionText.setOnClickListener {
            checkPermissionExportFile()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.exportFile()
        }
    }

    private fun checkPermissionExportFile() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.exportFile()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                showInContextUI()
            }
            else -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_REQUEST_WRITE_STORAGE
                )
            }
        }
    }

    private fun showInContextUI() {
        setupMaterialAlertDialog(
            R.string.permission_title,
            R.string.permission_description,
            object : NoticeDialogListener {
                override fun onDialogPositiveClick(dialog: DialogInterface) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PERMISSION_REQUEST_WRITE_STORAGE
                    )
                }

                override fun onDialogNegativeClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            }
        )
    }

    private fun setupNavigation() {
        viewModel.backAddEditNoteEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                ALERT_DIALOG_NULL -> {
                    setupMaterialAlertDialog(
                        R.string.dialog_title_not_save_encryption_key,
                        R.string.dialog_message_not_save_encryption_key,
                        object : NoticeDialogListener {
                            override fun onDialogPositiveClick(dialog: DialogInterface) {
                                viewModel.saveEncryptionText()
                                findNavController().popBackStack()
                            }

                            override fun onDialogNegativeClick(dialog: DialogInterface) {
                                dialog.dismiss()
                                viewModel.restoreKey()
                            }
                        }
                    )
                }
                ALERT_DIALOG_NEW_KEY -> setupMaterialAlertDialog(
                    R.string.dialog_title_new_encryption_key,
                    R.string.dialog_message_new_encryption_key,
                    object : NoticeDialogListener {
                        override fun onDialogPositiveClick(dialog: DialogInterface) {
                            viewModel.saveEncryptionText()
                            findNavController().popBackStack()
                        }

                        override fun onDialogNegativeClick(dialog: DialogInterface) {
                            findNavController().popBackStack()
                        }
                    }
                )
                NOTHING_ALERT_DIALOG -> {
                    viewModel.saveEncryptionText()
                    findNavController().popBackStack()
                }
            }
        })
    }
}

// Keys for navigation
const val ALERT_DIALOG_NEW_KEY = Activity.RESULT_FIRST_USER + 1
const val ALERT_DIALOG_NULL = Activity.RESULT_FIRST_USER + 2
const val NOTHING_ALERT_DIALOG = Activity.RESULT_FIRST_USER + 3

// Key for Android Permission
const val PERMISSION_REQUEST_WRITE_STORAGE = 1
const val PERMISSION_REQUEST_READ_STORAGE = 2

// Key for Activity Result
const val REQUEST_CODE_IMPORT_FILE = Activity.RESULT_FIRST_USER + 1
const val REQUEST_CODE_EXPORT_FILE = Activity.RESULT_FIRST_USER + 2