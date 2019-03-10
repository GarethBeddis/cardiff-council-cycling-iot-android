package uk.gov.cardiff.cleanairproject
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val playPauseFab = playPauseButton as FloatingMusicActionButton
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
        playPauseButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {
                //do stuff
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
                    Toast.makeText(this,"Test", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    this.startActivity(intent)
                    true
                }
                R.id.help -> {
                    true
                }
                R.id.logout -> {
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        // Show the menu
        popup.show()
    }
}
