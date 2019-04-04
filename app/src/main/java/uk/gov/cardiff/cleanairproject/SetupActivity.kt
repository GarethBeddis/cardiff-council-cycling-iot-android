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
import android.support.v4.content.ContextCompat
import uk.gov.cardiff.cleanairproject.auth.ServerAuthenticator
import uk.gov.cardiff.cleanairproject.auth.ServerAuthenticatorListener

import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.setup.fragments.*

class SetupActivity : AppCompatActivity(), Listeners {

    private val fragmentManager = supportFragmentManager
    private val welcomeFragment = WelcomeFragment()
    private val locationFragment = LocationFragment()
    private val bluetoothFragment = BluetoothFragment()
    private val finishedFragment = FinishedFragment()
    private val loadingFragment = LoadingFragment()
    private val loginFragment = LoginFragment()
    private val registerFragment = RegisterFragment()

    private var historySteps = 0
    private var backToExitPressed = false
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Setup)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        // If there's no savedInstanceState, set the starting fragment
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.fragment_container, welcomeFragment).commit()
        }
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

    override fun login(email: String, password: String) {
        // Show the loading screen
        loading = true
        changeFragmentListener(Pages.LOADING)
        // Authenticate with the server
        ServerAuthenticator(this).login(email, password,
            object : ServerAuthenticatorListener {
                override fun onAuthSuccess(token: String) {
                    // Set the FirstTimeSetup completed preference
                    saveLogin(email, token)
                    changeFragmentListener(Pages.LOCATION)
                    loading = false
                }
                override fun onAuthFailure(error: String) {
                    fragmentManager.popBackStack()
                    Snackbar.make(findViewById(R.id.content), error, Snackbar.LENGTH_LONG).show()
                    loading = false
                }
            })
    }

    override fun register(email: String, password: String) {
        // Show the loading screen
        loading = true
        changeFragmentListener(Pages.LOADING)
        // Authenticate with the server
        ServerAuthenticator(this).register(email, password,
            object : ServerAuthenticatorListener {
                override fun onAuthSuccess(token: String) {
                    // Set the FirstTimeSetup completed preference
                    saveLogin(email, token)
                    changeFragmentListener(Pages.LOCATION)
                    loading = false
                }
                override fun onAuthFailure(error: String) {
                    fragmentManager.popBackStack()
                    Snackbar.make(findViewById(R.id.content), error, Snackbar.LENGTH_LONG).show()
                    loading = false
                }
            })
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
        val targetFragment: Fragment
        var canGoBack = false
        when(targetPage) {
            Pages.WELCOME -> targetFragment = welcomeFragment
            Pages.LOCATION -> targetFragment = locationFragment
            Pages.BLUETOOTH -> targetFragment = bluetoothFragment
            Pages.FINISHED -> targetFragment = finishedFragment
            Pages.LOADING -> {
                targetFragment = loadingFragment
                canGoBack = true
            }
            Pages.LOGIN -> {
                targetFragment = loginFragment
                canGoBack = true
            }
            Pages.REGISTER -> {
                targetFragment = registerFragment
                canGoBack = true
            }
        }
        
        // Switch Fragment
        changeFragment(targetFragment, canGoBack)
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
        // If loading is in progress, do nothing when back is pressed
        if (loading) return
        // If history steps is more than 0, go to the previous fragment
        if (historySteps > 0) {
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

    private fun saveLogin(email: String, token: String) {
        // Add the login email and token to sharedPrefs
        getSharedPreferences("Account", MODE_PRIVATE)
            .edit()
            .putString("email", email)
            .putString("token", token)
            .apply()
    }
}
