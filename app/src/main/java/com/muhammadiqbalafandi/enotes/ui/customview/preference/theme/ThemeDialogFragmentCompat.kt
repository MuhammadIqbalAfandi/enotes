package com.muhammadiqbalafandi.enotes.ui.customview.preference.theme

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.preference.PreferenceDialogFragmentCompat
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.utils.Utils

class ThemeDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    private lateinit var radioGroupThemePref: RadioGroup

    companion object {
        fun newInstance(key: String): ThemeDialogFragmentCompat {
            val fragment =
                ThemeDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val preference = preference
        if (preference is ThemePreferences) {
            radioGroupThemePref = view.findViewById(R.id.radio_group_theme_preferences)

            val initialId =
                if (preference.checkedIdValue == View.NO_ID) R.id.radio_light_theme_preferences else preference.checkedIdValue
            radioGroupThemePref.check(initialId)

            radioGroupThemePref.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_light_theme_preferences -> {
                        // Set theme for apps
                        Utils.applyTheme(Utils.LIGHT_MODE)

                        // Update value summary
                        preference.summary = resources.getString(R.string.light)
                        // Save value
                        preference.themeApps = resources.getString(R.string.light)
                        preference.checkedIdValue = checkedId

                        dismiss()
                    }
                    R.id.radio_dark_theme_preferences -> {
                        Utils.applyTheme(Utils.DARK_MODE)

                        preference.summary = resources.getString(R.string.dark)
                        preference.themeApps = resources.getString(R.string.dark)
                        preference.checkedIdValue = checkedId

                        dismiss()
                    }
                    R.id.radio_set_battery_server_theme_preferences -> {
                        Utils.applyTheme(Utils.BATTERY_SEVER_MODE)

                        preference.summary = resources.getString(R.string.set_by_battery_serve)
                        preference.themeApps =
                            resources.getString(R.string.set_by_battery_serve)
                        preference.checkedIdValue = checkedId

                        dismiss()
                    }
                    R.id.radio_system_default_preferences -> {
                        Utils.applyTheme(Utils.DEFAULT_SYSTEM_MODE)

                        preference.summary = resources.getString(R.string.system_default)
                        preference.themeApps = resources.getString(R.string.system_default)
                        preference.checkedIdValue = checkedId

                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (!positiveResult) {
            dismiss()
        }
    }
}
