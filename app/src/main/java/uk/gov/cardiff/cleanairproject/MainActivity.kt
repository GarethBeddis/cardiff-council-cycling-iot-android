package uk.gov.cardiff.cleanairproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.cardiff.cleanairproject.foreground.ForegroundService
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import uk.gov.cardiff.cleanairproject.foreground.ServiceCallback

class MainActivity : AppCompatActivity(), ServiceConnection, ServiceCallback {

    private lateinit var playPauseFab: FloatingMusicActionButton
    private var foregroundService: ForegroundService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        if (ForegroundService.isRunning) {
            val serviceBindingIntent = Intent(this, ForegroundService::class.java)
            bindService(serviceBindingIntent, this, Context.BIND_AUTO_CREATE)
            onServiceStarted()
        } else {
            foregroundService = null
            onServiceStopped()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        foregroundService = (service as ForegroundService.Binder).service
        foregroundService?.setCallBack(this)
        onServiceStarted()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        onServiceStopped()
    }

    override fun onServiceStarted() {
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
        // Check if the service is already connected and set the connection status text
        if (foregroundService?.connected == true) {
            connectionStatus.text = resources.getString(R.string.connected)
        } else {
            connectionStatus.text = resources.getString(R.string.connecting)
        }
    }

    override fun onServiceStopped() {
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
        // Set the connection status text
        connectionStatus.text = resources.getString(R.string.disconnected)
    }

    override fun onConnected() {
        connectionStatus.text = resources.getString(R.string.connected)
    }

    override fun onReading(longitude: Double?) {
        Log.d("New Reading", longitude.toString())
    }


}