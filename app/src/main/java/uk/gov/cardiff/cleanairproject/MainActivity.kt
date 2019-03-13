package uk.gov.cardiff.cleanairproject
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.cardiff.cleanairproject.foreground.ForegroundService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playPauseFab = playPauseButton as FloatingMusicActionButton
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)


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

        // Menu Icon listener
        menuIcon.setOnClickListener {
                v -> showPopup(v)
        }



    }
    fun showPopup(v: View) {
        // Inflate
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_main, popup.menu)
        // Set on click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.about -> {
                    TODO("Add Link to About page")
                }
                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    this.startActivity(intent)
                    true
                }
                R.id.help -> {
                    TODO("Add Link to Help page")
                }
                R.id.logout -> {
                    TODO("Add Logout Link")
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        // Show the menu
        popup.show()
    }
}

