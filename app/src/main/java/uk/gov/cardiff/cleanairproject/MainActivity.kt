package uk.gov.cardiff.cleanairproject

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.cardiff.cleanairproject.foreground.ForegroundService
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import uk.gov.cardiff.cleanairproject.aqi.AirQualityIndex
import uk.gov.cardiff.cleanairproject.aqi.Bands
import uk.gov.cardiff.cleanairproject.foreground.ServiceCallback

class MainActivity : AppCompatActivity(), ServiceConnection, ServiceCallback {

    private lateinit var playPauseFab: FloatingMusicActionButton
    private var foregroundService: ForegroundService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Check that we still have location permissions
        checkLocationPermission()
        // Set the current user
        // TODO: Get the user email
        currentUser.text = "user@email.com"
        // Get the FAB from the layout
        playPauseFab = playPauseButton
        // Add the FAB on click listener
        playPauseButton.setOnClickListener {
            if(!ForegroundService.isRunning){
                val intent = Intent(this@MainActivity, ForegroundService::class.java)
                intent.action = ForegroundService.START_FOREGROUND_SERVICE
                startService(intent)
                bindService(intent, this@MainActivity, Context.BIND_AUTO_CREATE)
            }else {
                val intent = Intent(this@MainActivity, ForegroundService::class.java)
                intent.action = ForegroundService.STOP_FOREGROUND_SERVICE
                startService(intent)
            }
        }
        // Rebind the service if it's running
        rebindService()
    }

    override fun onPause() {
        super.onPause()
        // Disconnect the service
        if (foregroundService != null) {
            foregroundService?.setCallBack(null)
            unbindService(this)
        }
    }

    override fun onResume() {
        super.onResume()
        // Rebind the service
        rebindService()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        foregroundService = (service as ForegroundService.Binder).service
        foregroundService?.setCallBack(this)
        onServiceStarted()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        onServiceStopped()
    }

    override fun onConnected() {
        connectionStatus.text = resources.getString(R.string.connected)
        statusImage.animate().alpha(0.0f).duration = 200
        statusImageConnected.animate().alpha(1.0f).duration = 200
        noiseReading.animate().alpha(1.0f).duration = 200
        airPollution.animate().alpha(1.0f).duration = 200
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

    override fun onServiceStarted() {
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
        // Check if the service is already connected and set the connection status text
        if (foregroundService?.connected == true) {
            onConnected()
        } else {
            connectionStatus.text = resources.getString(R.string.connecting)
        }
    }

    override fun onServiceStopped() {
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
        // Set the connection status text
        connectionStatus.text = resources.getString(R.string.disconnected)
        statusImage.animate().alpha(1.0f).duration = 200
        statusImageConnected.animate().alpha(0.0f).duration = 200
        noiseReading.animate().alpha(0.0f).duration = 200
        airPollution.animate().alpha(0.0f).duration = 200
    }

    private fun rebindService() {
        if (ForegroundService.isRunning) {
            val serviceBindingIntent = Intent(this, ForegroundService::class.java)
            bindService(serviceBindingIntent, this, Context.BIND_AUTO_CREATE)
            onServiceStarted()
        } else {
            foregroundService = null
            onServiceStopped()
        }
    }

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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        // If the permission has been granted, go to the Bluetooth page
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Sorry, you can't use this app without enabling location access.", Snackbar.LENGTH_LONG
            ).show()
        }
    }

}