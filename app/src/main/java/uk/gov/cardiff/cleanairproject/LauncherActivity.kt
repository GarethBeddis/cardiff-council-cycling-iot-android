package uk.gov.cardiff.cleanairproject

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    // Reference:
    // https://android.jlelse.eu/the-complete-android-splash-screen-guide-c7db82bce565

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Switch Activity
        launchActivity()
        // Finish the Splash Activity
        finish()
    }

    private fun launchActivity() {
        // Get the FirstTimeSetup completed preference
        val sharedPref = getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
        val setupComplete = sharedPref.getBoolean("completed", false)
        // Get the intent
        val intent = if (setupComplete) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SetupActivity::class.java)
        }
        // Start the activity
        startActivity(intent)
    }
}