package com.muhammadiqbalafandi.enotes.ui.customview.preference.theme

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.preference.DialogPreference
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.utils.SharedPreferencesDelegate

class ThemePreferences : DialogPreference {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        const val THEME_APPS = "Theme Apps"
    }

    var themeApps by SharedPreferencesDelegate(
        context,
        THEME_APPS,
        context.resources.getString(R.string.light)
    )
    var checkedIdValue by SharedPreferencesDelegate(context, "ThemePreferencesId", View.NO_ID)

    init {
        dialogLayoutResource = R.layout.pref_dialog_theme
        dialogTitle = context.resources.getString(R.string.message_select_theme)
        summary = themeApps
        negativeButtonText = context.resources.getString(R.string.cancel)
    }
}