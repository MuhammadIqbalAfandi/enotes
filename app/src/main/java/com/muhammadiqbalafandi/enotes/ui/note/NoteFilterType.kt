package com.muhammadiqbalafandi.enotes.ui.note

/**
 * Used with the filter spinner in the tasks list.
 */
enum class NoteFilterType {
    /**
     * Do not filter note.
     */
    ALL_NOTE,

    /**
     * Filters only the pin note.
     */
    PIN_NOTE,

    /**
     * Filters only the encryption note.
     */
    ENCRYPTION_NOTE
}