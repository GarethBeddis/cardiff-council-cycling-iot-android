package uk.gov.cardiff.cleanairproject

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    // Reference:
    // https://android.jlelse.eu/the-complete-android-splash-screen-guide-c7db82bce565

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Determine whether to show SetupActivity or MainActivity
        // Switch Activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Finish the Splash Activity
        finish()
    }
}