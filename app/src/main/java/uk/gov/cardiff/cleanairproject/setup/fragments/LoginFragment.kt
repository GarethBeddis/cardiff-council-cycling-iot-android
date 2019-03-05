package uk.gov.cardiff.cleanairproject.setup.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.gov.cardiff.cleanairproject.R
import uk.gov.cardiff.cleanairproject.setup.Animations
import uk.gov.cardiff.cleanairproject.setup.Listeners

class LoginFragment : Fragment() {

    private lateinit var listener: Listeners

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listeners) {
            listener = context
        } else {
            throw RuntimeException("$context must implement the Listeners interface")
        }
    }
}
