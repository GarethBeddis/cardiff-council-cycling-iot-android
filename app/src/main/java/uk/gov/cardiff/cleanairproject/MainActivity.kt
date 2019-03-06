package uk.gov.cardiff.cleanairproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.cardiff.cleanairproject.foreground.ForegroundService

class MainActivity : AppCompatActivity() {
    var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playPauseFab = playPauseButton as FloatingMusicActionButton
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)


        playPauseButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {

                if(started == false){
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.START_FOREGROUND_SERVICE
                    startService(intent)
                    started = true
                }else if(started == true){
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.STOP_FOREGROUND_SERVICE
                    startService(intent)
                    started = false
                }
            }

        })
    }


}
