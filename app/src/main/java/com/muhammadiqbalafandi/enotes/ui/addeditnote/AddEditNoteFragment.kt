package com.muhammadiqbalafandi.enotes.ui.addeditnote

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.muhammadiqbalafandi.enotes.EventObserver
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.FragAddEditNoteBinding
import com.muhammadiqbalafandi.enotes.ui.note.ADD_EDIT_RESULT_OK
import com.muhammadiqbalafandi.enotes.utils.getViewModelFactory
import com.muhammadiqbalafandi.enotes.utils.setupSnackbar

class AddEditNoteFragment : Fragment() {

    private lateinit var viewDataBinding: FragAddEditNoteBinding

    private val args: AddEditNoteFragmentArgs by navArgs()

    private val viewModel: AddEditNoteViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragAddEditNoteBinding.inflate(inflater, container, false)
        viewDataBinding.viewModel = viewModel
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewModel.start(args.noteId)
        viewModel.getSavedEncryptionKey()
        setupNavigationView()
        setupNavigation()
        setupSnackbar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_add_edit_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_menu) {
            viewDataBinding.drawerLayoutAddeditNote.openDrawer(GravityCompat.END)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.noteUpdateEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToNoteFragment(
                ADD_EDIT_RESULT_OK
            )
            findNavController().navigate(action)
        })
        viewModel.encryptionKeyEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                AddEditNoteFragmentDirections.actionAddNoteFragmentToEncryptionTextFragment(
                    viewModel.encryptionKey.value
                )
            findNavController().navigate(action)
        })
    }

    private fun setupNavigationView() {
        viewDataBinding.navViewAddeditNote.apply {
            setupIconRightNavigationView(this)
            setPinText(this)
            setKeyText(this)
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_pin -> {
                        viewModel.pin()
                        true
                    }
                    R.id.menu_encryption_text -> {
                        viewModel.openEncryptionText()
                        true
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                        true
                    }
                }
            }
        }
    }

    private fun setupIconRightNavigationView(navView: NavigationView) {
        val navFindId = mutableListOf(
            R.id.menu_pin,
            R.id.menu_encryption_text,
            R.id.menu_delete
        )
        val navActionView = mutableListOf(
            R.layout.menu_image_unpin,
            R.layout.menu_image_no_encryption,
            R.layout.menu_image_delete
        )
        for (index in navFindId.indices) {
            navView.menu.findItem(navFindId[index]).setActionView(navActionView[index])
        }
    }

    private fun setKeyText(navView: NavigationView) {
        viewModel.encryptionKey.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                navView.menu.findItem(R.id.menu_encryption_text).setTitle(R.string.encryption_text_active)
            } else {
                navView.menu.findItem(R.id.menu_encryption_text).setTitle(R.string.encryption_text)
            }
        })
    }

    private fun setPinText(navView: NavigationView) {
        viewModel.pin.observe(viewLifecycleOwner, Observer {
            if (it) {
                navView.menu.findItem(R.id.menu_pin).setTitle(R.string.unpin)
            } else {
                navView.menu.findItem(R.id.menu_pin).setTitle(R.string.pin)
            }
        })
    }
}