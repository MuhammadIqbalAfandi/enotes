package com.muhammadiqbalafandi.enotes.ui.settings

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.ui.customview.preference.theme.ThemeDialogFragmentCompat
import com.muhammadiqbalafandi.enotes.ui.customview.preference.theme.ThemePreferences

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        var dialogFragment: DialogFragment? = null
        when (preference) {
            is ThemePreferences -> dialogFragment =
                ThemeDialogFragmentCompat.newInstance(preference.key)
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(parentFragmentManager, dialogFragment.tag)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}