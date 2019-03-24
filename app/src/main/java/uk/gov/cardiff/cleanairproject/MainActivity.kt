package uk.gov.cardiff.cleanairproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.cardiff.cleanairproject.foreground.ForegroundService
import android.app.ActivityManager
import android.content.Context
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var playPauseFab: FloatingMusicActionButton
    private var serviceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playPauseFab = playPauseButton
        // Check if the service is running
        if (isServiceRunning(ForegroundService::class.java)) {
            serviceRunning = true
            playPauseFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
        } else {
            serviceRunning = false
            playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
        }
        // Add the FAB on click listener
        playPauseButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {
                if(playPauseFab.currentMode.isShowingPlayIcon){
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.START_FOREGROUND_SERVICE
                    startService(intent)
                }else {
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.STOP_FOREGROUND_SERVICE
                    startService(intent)
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        // Check if the service status has changed
        if (isServiceRunning(ForegroundService::class.java)) {
            serviceRunning = true
            playPauseFab.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
        } else {
            serviceRunning = false
            playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


}
