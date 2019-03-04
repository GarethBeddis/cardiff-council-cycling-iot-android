package uk.gov.cardiff.cleanairproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.widget.Toast

import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.setup.fragments.BluetoothFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.FinishedFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.LocationFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.WelcomeFragment

class SetupActivity : AppCompatActivity(), Listeners {

    private val fragmentManager = supportFragmentManager
    private val welcomeFragment = WelcomeFragment()
    private val locationFragment = LocationFragment()
    private val bluetoothFragment = BluetoothFragment()
    private val finishedFragment = FinishedFragment()

    private var historySteps = 0
    private var backToExitPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Setup)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        // Set the starting fragment
        fragmentManager.beginTransaction().add(R.id.fragment_container, welcomeFragment).commit()
    }

    private fun changeFragment(targetFragment: Fragment, canGoBack: Boolean) {
        // If can go back is true, add to the history steps, otherwise reset the history steps
        if (canGoBack) {
            historySteps++
        } else {
            historySteps = 0
        }
        // Replace the fragment
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, targetFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun finishSetup() {
        // Set the FirstTimeSetup completed preference
        getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
            .edit()
            .putBoolean("completed", true)
            .apply()
        // Switch to MainActivity and stop SetupActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun changeFragmentListener(targetPage: Pages) {
        // Get the matching fragment
        val targetFragment = when(targetPage) {
            Pages.WELCOME -> welcomeFragment
            Pages.LOCATION -> locationFragment
            Pages.BLUETOOTH -> bluetoothFragment
            Pages.FINISHED -> finishedFragment
        }
        // Switch Fragment
        changeFragment(targetFragment, false)
    }

    override fun requestLocationPermission() {
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            // If the permission has already been granted, go to the Bluetooth page
            changeFragment(bluetoothFragment, false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        // If the permission has been granted, go to the Bluetooth page
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            changeFragment(bluetoothFragment, false)
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Sorry, you can't use this app without enabling location access.", Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onBackPressed() {
        if (historySteps > 0) {
            // If history steps is more than 0, go to the previous fragment
            historySteps--
            fragmentManager.popBackStack()
        } else if (!backToExitPressed) {
            // If the back button hasn't been pressed within the last 2 seconds, warn the user about closing the app
            this.backToExitPressed = true
            Snackbar.make(
                findViewById(android.R.id.content),
                "Press back again to exit", Snackbar.LENGTH_SHORT
            ).show()
            // After two seconds, revert the backToExitPressed variable
            Handler().postDelayed({ backToExitPressed = false }, 2000)
        } else {
            // Close the app
            finish()
        }
    }
}
