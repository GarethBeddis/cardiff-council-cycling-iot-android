package uk.gov.cardiff.cleanairproject.setup.fragments

import android.os.Bundle
import android.view.View
import android.content.Context
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register.*

import uk.gov.cardiff.cleanairproject.R
import uk.gov.cardiff.cleanairproject.setup.Animations
import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.sql.DatabaseHelper
import uk.gov.cardiff.cleanairproject.auth.validation.InputValidation

// Adapted from ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/tree/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/activities
class RegisterFragment : Fragment() {

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
        inputValidation = InputValidation(this.activity!!)
        databaseHelper = DatabaseHelper(this.activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(uk.gov.cardiff.cleanairproject.R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_register.setOnClickListener { postDataToSQLite() }
        button_login.setOnClickListener {
            listener.changeFragmentListener(Pages.LOGIN)
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

    // Validate the input text fields and post data to SQLite
    private fun postDataToSQLite() {
        // Form Validation
        if (!inputValidation.isInputEditTextFilled(text_input_email, text_input_layout_email, getString(R.string.error_message_email))) return
        if (!inputValidation.isInputEditTextEmail(text_input_email, text_input_layout_email, getString(R.string.error_message_email))) return
        if (!inputValidation.isInputEditTextFilled(text_input_password, text_input_layout_password, getString(R.string.error_message_password))) return
        if (!inputValidation.isInputEditTextMatches(text_input_password, text_input_confirm_password, text_input_layout_confirm_password, getString(R.string.error_password_match))) return
        // Get the email and password
        val email = text_input_email.text.toString().trim()
        val password = text_input_password.text.toString().trim()
        // Authenticate with the server
        listener.register(email, password)
    }
}
