package uk.gov.cardiff.cleanairproject

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(bundle:Bundle, s:String) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences)
    }
}
