package uk.gov.cardiff.cleanairproject.setup_fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import uk.gov.cardiff.cleanairproject.R

class WelcomeFragment : Fragment() {

    private lateinit var listener: OnCompleteListener
    private lateinit var buttonLogin: Button

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
        val view: View = inflater.inflate(R.layout.fragment_welcome, container, false)
        buttonLogin = view.findViewById<Button>(R.id.button_login) as Button
        buttonLogin.setOnClickListener {
            listener.onCompleteListener()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCompleteListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnComplete")
        }
    }

    interface OnCompleteListener {
        fun onCompleteListener()
    }
}
