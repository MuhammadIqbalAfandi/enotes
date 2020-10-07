package com.muhammadiqbalafandi.enotes.ui.note

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muhammadiqbalafandi.enotes.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_note)
    }
}

// Keys for navigation
const val ADD_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val NO_SAVE_RESULT_OK = Activity.RESULT_FIRST_USER + 4
