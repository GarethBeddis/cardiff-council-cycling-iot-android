package uk.gov.cardiff.cleanairproject

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Setup)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        // Stop setup showing again
        // setSetupComplete()
    }

    private fun setSetupComplete() {
        // Set the FirstTimeSetup completed preference
        val sharedPref = getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("completed", true)
        editor.apply()
    }
}
