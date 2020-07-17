package com.muhammadiqbalafandi.enotes.ui.searchnote

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.muhammadiqbalafandi.enotes.databinding.FragSearchNoteBinding

class SearchNoteFragment : Fragment() {

    private lateinit var binding: FragSearchNoteBinding

    private val searchNoteViewModel: SearchNoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragSearchNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val linearLayout = LinearLayoutManager(requireContext())
//        binding.rvListSearchNote.layoutManager = linearLayout
//        val adapter = ListNoteAdapter()
//        binding.rvListSearchNote.adapter = adapter
//
//        binding.searchViewSearchNote.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean = false
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//            }
//        })


        // Provide custom back navigation
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hideSoftInput(requireView())
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.toolbarSearchNote.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        // Show keyboard when search query text focus
        binding.searchViewSearchNote.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showSoftInput(v.findFocus())
            }
        }
    }

    private fun showSoftInput(view: View) {
        val imm =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun hideSoftInput(view: View) {
        val imm =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}