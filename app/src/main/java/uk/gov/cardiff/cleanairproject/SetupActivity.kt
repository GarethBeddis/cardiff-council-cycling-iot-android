package uk.gov.cardiff.cleanairproject

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import uk.gov.cardiff.cleanairproject.setup_fragments.Animations
import uk.gov.cardiff.cleanairproject.setup_fragments.LocationFragment
import uk.gov.cardiff.cleanairproject.setup_fragments.WelcomeFragment

class SetupActivity : AppCompatActivity(), WelcomeFragment.OnCompleteListener {

    private var welcomeFragment = WelcomeFragment()
    private var locationFragment = LocationFragment()
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Setup)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // Set the starting fragment
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, welcomeFragment)
        transaction.commit()

        // Stop setup showing again
        // setSetupComplete()
    }

    private fun setSetupComplete() {
        // Set the FirstTimeSetup completed preference
        getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
            .edit()
            .putBoolean("completed", true)
            .apply()
    }

    override fun onCompleteListener() {
        // This is a like locationFragment.setEnterTransition(slideRight) in Java
        locationFragment.enterTransition = Animations.getSlideRightAnimation()

        // Switch Fragment
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, locationFragment)
            .addToBackStack(null)
            .commit()
    }
}
