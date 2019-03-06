package uk.gov.cardiff.cleanairproject

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playPauseFab = fab as FloatingMusicActionButton
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)

        fab.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {
                //do stuff
            }

        })
    }


}
