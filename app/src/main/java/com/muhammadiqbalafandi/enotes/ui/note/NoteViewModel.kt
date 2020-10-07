package com.muhammadiqbalafandi.enotes.ui.note

import android.app.Application
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.muhammadiqbalafandi.enotes.Event
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.data.Result
import com.muhammadiqbalafandi.enotes.data.Result.Success
import com.muhammadiqbalafandi.enotes.data.source.NoteRepository
import com.muhammadiqbalafandi.enotes.data.source.local.Note
import com.muhammadiqbalafandi.enotes.databinding.FragNoteBinding
import com.muhammadiqbalafandi.enotes.databinding.ListItemNoteBinding
import com.muhammadiqbalafandi.enotes.ui.note.NoteFilterType.*
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    // Used in this class.
    // private val context: Application = getApplication()

    private val _forceUpdate = MutableLiveData(false)

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

    private var resultMessageShown: Boolean = false

    /**
     * Used directly in [FragNoteBinding].
     */
    val searchQuery = MutableLiveData<String>()

    val items: LiveData<List<Note>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    // Set resource, such string, icon.
    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noNoteLabel = MutableLiveData<Int>()
    val noNoteLabel: LiveData<Int> = _noNoteLabel

    private val _noNoteIconRes = MutableLiveData<Int>()
    val noNoteIconRes: LiveData<Int> = _noNoteIconRes

    // Used in fragment to listen changed.
    val searchResult: LiveData<List<Note>> = Transformations.switchMap(searchQuery) {
        noteRepository.observeSearchNote("%${it}%")
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _goToDetailNoteEvent = MutableLiveData<Event<String>>()
    val goToDetailNoteEvent: LiveData<Event<String>> = _goToDetailNoteEvent

    private val _goToAddNewNoteEvent = MutableLiveData<Event<Unit>>()
    val goToAddNewNoteEvent: LiveData<Event<Unit>> = _goToAddNewNoteEvent

    private val _popupMenu = MutableLiveData<Event<View>>()
    val popupMenu: LiveData<Event<View>> = _popupMenu

    private val _goToActivitySettingEvent = MutableLiveData<Event<Unit>>()
    val goToActivitySettingEvent: LiveData<Event<Unit>> = _goToActivitySettingEvent

    init {
        // Set initial state.
        setFiltering(getSavedFilterType())
        loadNote(true)
    }

    private fun filterNote(noteResult: Result<List<Note>>): LiveData<List<Note>> {
        // TODO: This is a good case for liveData builder. Replace when stable.
        val result = MutableLiveData<List<Note>>()

        if (noteResult is Success) {
            viewModelScope.launch {
                result.value = filterItems(noteResult.data, getSavedFilterType())
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.snackbar_message_loading_note_error)
        }

        return result
    }

    private fun filterItems(notes: List<Note>, filteringType: NoteFilterType): List<Note> {
        val noteToShow = ArrayList<Note>()
        // We filter the note based on the requestType
        for (note in notes) {
            when (filteringType) {
                ALL_NOTE -> {
                    noteToShow.add(note)
                }
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

    /**
     * Used directly in [FragNoteBinding].
     */
    fun addNewNote() {
        _goToAddNewNoteEvent.value = Event(Unit)
    }

    fun showPopupMenu(view: View) {
        _popupMenu.value = Event(view)
    }

    fun goToActivitySetting() {
        _goToActivitySettingEvent.value = Event(Unit)
    }

    fun refresh() {
        _forceUpdate.value = true
    }

    /**
     * Used directly in [ListItemNoteBinding].
     */
    fun openNote(noteId: String) {
        _goToDetailNoteEvent.value = Event(noteId)
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
                    R.string.filter_title_all_note,
                    R.string.message_error_no_note,
                    R.drawable.ic_twotone_list
                )
            }
            PIN_NOTE -> {
                setFilter(
                    R.string.filter_title_pin_note,
                    R.string.message_error_no_note_pin,
                    R.drawable.ic_twotone_pin
                )
            }
            ENCRYPTION_NOTE -> {
                setFilter(
                    R.string.message_error_encryption_note,
                    R.string.message_error_no_encryption_note,
                    R.drawable.ic_twotone_lock
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
     * @param forceUpdate pass in true to refresh the data in the NoteDataSource.
     */
    private fun loadNote(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    /**
     * Used directly in [NoteFragment].
     */
    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.snackbar_message_note_saved)
            ADD_RESULT_OK -> showSnackbarMessage(R.string.snackbar_message_note_added)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.snackbar_message_deleted_note)
            NO_SAVE_RESULT_OK -> showSnackbarMessage(R.string.snackbar_message_note_discarded)
        }
        resultMessageShown = true
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}

// Used as key SavedStateHandle, to filtering notes.
const val NOTE_FILTER_SAVED_STATE_KEY = "NOTE_FILTER_SAVED_STATE_KEY"