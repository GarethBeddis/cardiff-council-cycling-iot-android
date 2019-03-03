package uk.gov.cardiff.cleanairproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.setup.fragments.BluetoothFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.FinishedFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.LocationFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.WelcomeFragment

class SetupActivity : AppCompatActivity(), Listeners {

    private var welcomeFragment = WelcomeFragment()
    private var locationFragment = LocationFragment()
    private var bluetoothFragment = BluetoothFragment()
    private var finishedFragment = FinishedFragment()
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Setup)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        // Set the starting fragment
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, welcomeFragment)
        transaction.commit()
    }

    override fun finishSetup() {
        // Set the FirstTimeSetup completed preference
        getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
            .edit()
            .putBoolean("completed", true)
            .apply()
        // Switch to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        // Stop SetupActivity
        finish()
    }

    override fun changeFragmentListener(targetPage: Pages) {
        val targetFragment = when(targetPage) {
            Pages.WELCOME -> welcomeFragment
            Pages.LOCATION -> locationFragment
            Pages.BLUETOOTH -> bluetoothFragment
            Pages.FINISHED -> finishedFragment
        }

        // Switch Fragment
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, targetFragment)
            .addToBackStack(null)
            .commit()
    }
}
