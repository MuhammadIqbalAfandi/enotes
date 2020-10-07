package com.muhammadiqbalafandi.enotes.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.muhammadiqbalafandi.enotes.R
import com.muhammadiqbalafandi.enotes.databinding.ActSettingsBinding

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var viewDataBinding: ActSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.act_settings)

        setSupportActionBar(viewDataBinding.toolbarSettingNote)
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_twotone_arrow_back
            )
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Add SettingPreferencesFragment to SettingsActivity
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_settings, SettingPreferencesFragment())
            .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                title = resources.getString(R.string.title_activity_settings)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            ClassLoader.getSystemClassLoader(),
            pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    /**
     * The root preference fragment that displays all preferences apps.
     */
    class SettingPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_pref, rootKey)
        }
    }

    /**
     * A preference fragment that show developer info, such developer name, contact, etc.
     */
    class DeveloperInfoPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.about_pref, rootKey)
        }
    }
}