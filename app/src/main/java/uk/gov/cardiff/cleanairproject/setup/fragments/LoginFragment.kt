package uk.gov.cardiff.cleanairproject.setup.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*
import uk.gov.cardiff.cleanairproject.setup.Animations
import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.sql.DatabaseHelper
import uk.gov.cardiff.cleanairproject.auth.validation.InputValidation
import uk.gov.cardiff.cleanairproject.R
import uk.gov.cardiff.cleanairproject.auth.ServerAuthenticator
import uk.gov.cardiff.cleanairproject.auth.ServerAuthenticatorListener

// Adapted from ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/tree/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/activities
class LoginFragment : Fragment() {

    private lateinit var listener: Listeners
    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set Fragment Animations
        this.exitTransition = Animations.getSlideLeftAnimation()
        this.enterTransition = Animations.getSlideRightAnimation()
        this.reenterTransition = Animations.getSlideLeftAnimation()
        // Initialise the objects
        databaseHelper = DatabaseHelper(this.activity!!)
        inputValidation = InputValidation(this.activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(uk.gov.cardiff.cleanairproject.R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_login.setOnClickListener {
            login()
        }
        button_register.setOnClickListener {
            listener.changeFragmentListener(Pages.REGISTER)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listeners) {
            listener = context
        } else {
            throw RuntimeException("$context must implement the Listeners interface")
        }
    }

    private fun login() {
        // Validate email and password fields, if invalid return appropriate message
        if (!inputValidation.isInputEditTextEmail(text_input_email, text_input_layout_email, getString(R.string.error_message_email))) return
        if (!inputValidation.isInputEditTextFilled(text_input_email, text_input_layout_email, getString(R.string.error_message_email))) return
        if (!inputValidation.isInputEditTextFilled(text_input_password, text_input_layout_password,getString(R.string.error_message_password))) return
        // Get the email and password
        val email = text_input_email.text.toString().trim()
        val password = text_input_password.text.toString().trim()
        // Authenticate with the server
        ServerAuthenticator(context!!).login(email, password,
            object : ServerAuthenticatorListener {
                override fun onAuthSuccess(token: String) {
                    // Set the FirstTimeSetup completed preference
                    listener.saveLogin(email, token)
                    listener.changeFragmentListener(Pages.LOCATION)
                }
                override fun onAuthFailure(error: String) {
                    Snackbar.make(view!!, error, Snackbar.LENGTH_LONG).show()
                }
            })
    }
}