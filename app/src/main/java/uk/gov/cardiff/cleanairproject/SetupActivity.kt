package uk.gov.cardiff.cleanairproject

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import uk.gov.cardiff.cleanairproject.setup_fragments.LocationFragment
import uk.gov.cardiff.cleanairproject.setup_fragments.WelcomeFragment

class SetupActivity : AppCompatActivity(), WelcomeFragment.OnCompleteListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Setup)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // Prepare the fragments
        val welcomeFragment = WelcomeFragment()

        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_container, welcomeFragment)

        transaction.commit()

        // Stop setup showing again
        // setSetupComplete()
    }

    private fun setSetupComplete() {
        // Set the FirstTimeSetup completed preference
        val sharedPref = getSharedPreferences("FirstTimeSetup", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("completed", true)
        editor.apply()
    }

    override fun onCompleteListener() {
        val locationFragment = LocationFragment()
//        val slideRight = Slide(Gravity.RIGHT)
//        slideRight.duration = 200
//
//        // This is a like locationFragment.setEnterTransition(slideRight) in Java
//        locationFragment.enterTransition = slideRight

        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()

        // transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        transaction.replace(R.id.fragment_container, locationFragment)

        transaction.commit()
    }
}
