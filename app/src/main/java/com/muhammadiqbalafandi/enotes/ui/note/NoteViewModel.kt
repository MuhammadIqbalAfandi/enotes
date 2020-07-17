package com.muhammadiqbalafandi.enotes.ui.note

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import com.muhammadiqbalafandi.enotes.ui.note.NoteFilterType.*
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _items: LiveData<List<Note>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                noteRepository.refreshNote()
                _dataLoading.value = false
            }
        }
        noteRepository.observeNote().distinctUntilChanged().switchMap { filterNote(it) }
    }
    val items: LiveData<List<Note>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noNoteLabel = MutableLiveData<Int>()
    val noNoteLabel: LiveData<Int> = _noNoteLabel

    private val _noNoteIconRes = MutableLiveData<Int>()
    val noNoteIconRes: LiveData<Int> = _noNoteIconRes

    // Not used at the moment.
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openNoteEvent = MutableLiveData<Event<String>>()
    val openNoteEvent: LiveData<Event<String>> = _openNoteEvent

    private val _addNewNote = MutableLiveData<Event<Unit>>()
    val  addNewNote: LiveData<Event<Unit>> = _addNewNote

    private var resultMessageShown: Boolean = false

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        // Set initial state.
        setFiltering(getSavedFilterType())
        loadNote(true)
    }

    /**
     * Called when the FAB's click listener.
     */
    fun addNewNote() {
        _addNewNote.value = Event(Unit)
    }

    /**
     * Called by Data Binding.
     */
    fun openNote(noteId: String) {
        _openNoteEvent.value = Event(noteId)
    }

    /**
     * Sets the current note filtering type.
     *
     * @param requestType can be [NoteFilterType.ALL_NOTE],
     * [NoteFilterType.PIN_NOTE]
     */
    fun setFiltering(requestType: NoteFilterType) {
        savedStateHandle.set(NOTE_FILTER_SAVED_STATE_KEY, requestType)

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            ALL_NOTE -> {
                setFilter(
                    R.string.all_note,
                    R.string.no_note_all,
                    R.drawable.ic_baseline_list
                )
            }
            PIN_NOTE -> {
                setFilter(
                    R.string.pin_note,
                    R.string.no_note_pin,
                    R.drawable.ic_baseline_pin
                )
            }
            ENCRYPTION_NOTE -> {
                setFilter(
                    R.string.encryption_note,
                    R.string.no_encryption_note,
                    R.drawable.ic_baseline_encryption
                )
            }
        }
        // Refresh list
        loadNote(false)
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noNoteLabelString: Int,
        @DrawableRes noNoteIconDrawable: Int
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noNoteLabel.value = noNoteLabelString
        _noNoteIconRes.value = noNoteIconDrawable
    }

    /**
     * @param forceUpdate pass in true to refresh the data in the [NoteDataSource]
     */
    private fun loadNote(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun refresh() {
        _forceUpdate.value = true
    }

    private fun filterNote(noteResult: Result<List<Note>>): LiveData<List<Note>> {
        // TODO: This is a good case for liveData builder. Replace when stable.
        val result = MutableLiveData<List<Note>>()

        if (noteResult is Success) {
            isDataLoadingError.value = false
            viewModelScope.launch {
                result.value = filterItems(noteResult.data, getSavedFilterType())
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.loading_note_error)
            isDataLoadingError.value = true
        }

        return result
    }

    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.note_saved)
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.note_added)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.deleted_note)
            NO_SAVE_RESULT_OK -> showSnackbarMessage(R.string.no_note)
        }
        resultMessageShown = true
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun filterItems(notes: List<Note>, filteringType: NoteFilterType): List<Note> {
        val noteToShow = ArrayList<Note>()
        // We filter the note based on the requestType
        for (note in notes) {
            when (filteringType) {
                ALL_NOTE -> noteToShow.add(note)
                PIN_NOTE -> if (note.pin) {
                    noteToShow.add(note)
                }
                ENCRYPTION_NOTE -> if (!note.encryptionKey.isNullOrEmpty()) {
                    noteToShow.add(note)
                }
            }
        }
        return noteToShow
    }

    private fun getSavedFilterType(): NoteFilterType {
        return savedStateHandle.get(NOTE_FILTER_SAVED_STATE_KEY) ?: ALL_NOTE
    }
}

// Used to save the current filtering in SavedStateHandle.
const val NOTE_FILTER_SAVED_STATE_KEY = "NOTE_FILTER_SAVED_STATE_KEY"