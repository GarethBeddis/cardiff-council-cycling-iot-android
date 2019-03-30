package uk.gov.cardiff.cleanairproject

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.cardiff.cleanairproject.sensors.SensorService
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import uk.gov.cardiff.cleanairproject.aqi.AirQualityIndex
import uk.gov.cardiff.cleanairproject.aqi.Bands
import uk.gov.cardiff.cleanairproject.sensors.SensorServiceCallback
import uk.gov.cardiff.cleanairproject.sync.SyncService
import uk.gov.cardiff.cleanairproject.sync.SyncServiceCallback
import uk.gov.cardiff.cleanairproject.sync.SyncStates

class MainActivity : AppCompatActivity(), SensorServiceCallback, SyncServiceCallback {

    private lateinit var playPauseFab: FloatingMusicActionButton
    private var sensorService: SensorService? = null
    private var syncService: SyncService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Check that we still have location permissions
        checkLocationPermission()
        // Set the current user
        currentUser.text = getSharedPreferences("Account", MODE_PRIVATE).getString("email", "")
        // Get the FAB from the layout
        playPauseFab = playPauseButton
        // Add the FAB on click listener
        playPauseButton.setOnClickListener {
            if(!SensorService.isRunning){
                val intent = Intent(this@MainActivity, SensorService::class.java)
                intent.action = SensorService.START_FOREGROUND_SERVICE
                startService(intent)
                bindService(intent, sensorServiceConnection, Context.BIND_AUTO_CREATE)
            }else {
                val intent = Intent(this@MainActivity, SensorService::class.java)
                intent.action = SensorService.STOP_FOREGROUND_SERVICE
                startService(intent)
            }
        }
        // Menu Icon listener
        menuIcon.setOnClickListener {
                v -> showPopup(v)
        }
        // Rebind the service if it's running
        rebindServices()
    }
    override fun onPause() {
        super.onPause()
        // Disconnect the services
        if (sensorService != null) {
            sensorService?.setCallBack(null)
            unbindService(sensorServiceConnection)
        }
        if (syncService != null) {
            syncService?.setCallBack(null)
            unbindService(syncServiceConnection)
        }
    }
    override fun onResume() {
        super.onResume()
        // Rebind the service
        rebindServices()
    }

    // Service Bindings
    private var sensorServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            sensorService = (service as SensorService.Binder).service
            sensorService?.setCallBack(this@MainActivity)
            onSensorServiceStarted()
        }
        override fun onServiceDisconnected(name: ComponentName) {}
    }
    private var syncServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            syncService = (service as SyncService.Binder).service
            syncService?.setCallBack(this@MainActivity)
        }
        override fun onServiceDisconnected(name: ComponentName) {}
    }
    private fun rebindServices() {
        // Rebind Sensor Service
        if (SensorService.isRunning) {
            bindService(Intent(this, SensorService::class.java), sensorServiceConnection,
                Context.BIND_AUTO_CREATE)
        } else {
            sensorService = null
            setDisconnected()
        }
        // Rebind Sync Service
        if (SyncService.isRunning) {
            bindService(Intent(this, SyncService::class.java), syncServiceConnection,
                Context.BIND_AUTO_CREATE)
        } else {
            syncService = null
        }
    }

    // Sensor Service Callback Functions
    override fun onConnected() {
        setConnected()
    }
    override fun onReading(longitude: Double?, latitude: Double?, no2: Int, pm25: Int, pm100: Int, db: Int) {
        airPollutionReadingValue.text = when (AirQualityIndex().getOverall(no2, pm25, pm100)) {
            Bands.LOW -> resources.getString(R.string.air_pollution_low)
            Bands.MODERATE -> resources.getString(R.string.air_pollution_moderate)
            Bands.HIGH -> resources.getString(R.string.air_pollution_high)
            Bands.VERY_HIGH -> resources.getString(R.string.air_pollution_very_high)
        }
        no2Reading.text = resources.getString(R.string.air_reading, no2.toString())
        pm25Reading.text = resources.getString(R.string.air_reading, pm25.toString())
        pm100Reading.text = resources.getString(R.string.air_reading, pm100.toString())
        noiseReadingValue.text = resources.getString(R.string.noise_reading, db.toString())
    }
    override fun onSensorServiceStarted() {
        // Check if the service is already connected and set the connection status text
        if (sensorService?.connected == true) {
            onConnected()
        } else {
            setConnecting()
        }
    }
    override fun onSensorServiceStopped() {
        // Attempt to bind to the sync service
        bindService(Intent(this, SyncService::class.java), syncServiceConnection,
            Context.BIND_AUTO_CREATE)
        // Unbind
        unbindService(sensorServiceConnection)
        sensorService = null
        // Show that the sensors are disconnected
        setDisconnected()
    }

    // Sync Service Callback Function
    override fun onSyncStateChange(state: SyncStates) {
        Log.d("Sync State Change", state.toString())
    }
    override fun onSyncServiceStopped() {
        syncService = null
    }

    // Page State Setters
    private fun setDisconnected() {
        // Change the FAB state
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
        // Set the connection status text
        connectionStatus.text = resources.getString(R.string.disconnected)
        // Fade out views
        statusImage.animate().alpha(1.0f).duration = 200
        statusImageConnected.animate().alpha(0.0f).duration = 200
        noiseReading.animate().alpha(0.0f).duration = 200
        airPollution.animate().alpha(0.0f).duration = 200
    }
    private fun setConnecting() {
        // Change the FAB state
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
        // Set the connection status text
        connectionStatus.text = resources.getString(R.string.connecting)
    }
    private fun setConnected() {
        // Set the text views
        connectionStatus.text = resources.getString(R.string.connected)
        airPollutionReadingValue.text = resources.getString(R.string.default_reading)
        no2Reading.text = resources.getString(R.string.default_reading)
        pm25Reading.text = resources.getString(R.string.default_reading)
        pm100Reading.text = resources.getString(R.string.default_reading)
        noiseReadingValue.text = resources.getString(R.string.default_reading)
        // Fade in views
        statusImage.animate().alpha(0.0f).duration = 200
        statusImageConnected.animate().alpha(1.0f).duration = 200
        noiseReading.animate().alpha(1.0f).duration = 200
        airPollution.animate().alpha(1.0f).duration = 200
    }

    // Permissions
    private fun checkLocationPermission() {
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // If the permission has been granted, go to the Bluetooth page
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Sorry, you can't use this app without enabling location access.", Snackbar.LENGTH_LONG
            ).show()
        }
    }

    // Menu Button
    private fun showPopup(v: View) {
        // Inflate
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_main, popup.menu)
        // Set on click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.about -> {
                    true
                }
                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    this.startActivity(intent)
                    true
                }
                R.id.logout -> {
                    // Logout the user
                    getSharedPreferences("Account", MODE_PRIVATE)
                        .edit()
                        .remove("email")
                        .remove("token")
                        .apply()
                    // Set first time setup completed to false
                    getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
                        .edit()
                        .remove("completed")
                        .apply()
                    // Switch to SetupActivity and stop MainActivity
                    startActivity(Intent(this, SetupActivity::class.java))
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        // Show the menu
        popup.show()
    }
}
