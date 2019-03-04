package uk.gov.cardiff.cleanairproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.setup.fragments.BluetoothFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.FinishedFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.LocationFragment
import uk.gov.cardiff.cleanairproject.setup.fragments.WelcomeFragment

private const val PERMISSION_REQUEST_CODE_LOCATION = 100

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
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, welcomeFragment)
            .commit()
    }

    private fun changeFragment(targetFragment: Fragment) {
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
        changeFragment(targetFragment)
    }

    override fun requestLocationPermission() {
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE_LOCATION
            )
        } else {
            // If the permission has already been granted, go to the Bluetooth page
            changeFragment(bluetoothFragment)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        // If the permission has been granted, go to the Bluetooth page
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            changeFragment(bluetoothFragment)
    }
}
