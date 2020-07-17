package com.muhammadiqbalafandi.enotes.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    /**
     * Insert a note in the database. if the note already exists, replace it.
     *
     * @param note the note to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    /**
     *  Update a note.
     *
     *  @param note the note to be updated
     *  @return then number of note updated. This should always be 1.
     */
    @Update
    suspend fun updateNote(note: Note): Int

    /**
     * Delete a note by id.
     *
     * @return the number of note deleted. Tis should always be 1.
     */
    @Query("DELETE FROM Note WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Note")
    suspend fun deleteNote()

    /**
     *  Observer the note that search.
     *
     *  @return all note that search.
     */
    @Query("SELECT * FROM Note WHERE title LIKE :keywordSearch OR body LIKE :keywordSearch")
    fun observeSearchNote(keywordSearch: String): LiveData<List<Note>>

    /**
     * Observes list of note.
     *
     * @return all note.
     */
    @Query("SELECT * FROM Note ORDER BY date DESC")
    fun observeNote(): LiveData<List<Note>>

    /**
     * Observer a single note.
     *
     * @param noteId the note id.
     * @return the note with noteId.
     */
    @Query("SELECT * FROM Note WHERE id = :noteId")
    fun observerNoteById(noteId: String): LiveData<Note>

    /**
     * Select all note from the note table.
     *
     * @return all note.
     */
    @Query("SELECT * FROM Note")
    suspend fun getNote(): List<Note>

    /**
     * Select a note by id.
     *
     * @param noteId the note id.
     * @return the note with noteId.
     */
    @Query("SELECT * FROM Note WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): Note?
}