package uk.gov.cardiff.cleanairproject.setup.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.gov.cardiff.cleanairproject.R
import uk.gov.cardiff.cleanairproject.setup.Animations

class LocationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Fragment Animations
        this.exitTransition = Animations.getSlideLeftAnimation()
        this.enterTransition = Animations.getSlideRightAnimation()
        this.reenterTransition = Animations.getSlideLeftAnimation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }
}
