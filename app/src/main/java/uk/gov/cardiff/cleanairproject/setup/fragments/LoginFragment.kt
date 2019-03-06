package uk.gov.cardiff.cleanairproject.setup.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.gov.cardiff.cleanairproject.setup.Animations
import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.sql.DatabaseHelper
import uk.gov.cardiff.cleanairproject.validation.InputValidation
import uk.gov.cardiff.cleanairproject.R

// Adapted from ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/tree/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/activities
class LoginFragment : Fragment() {

    private lateinit var listener: Listeners
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout
    private lateinit var textInputEmail: TextInputEditText
    private lateinit var textInputPassword: TextInputEditText
    private lateinit var buttonLogin: AppCompatButton
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
        val view = inflater.inflate(uk.gov.cardiff.cleanairproject.R.layout.fragment_login, container, false)

        // Initialise views
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        textInputLayoutEmail = view.findViewById<View>(R.id.text_input_layout_email) as TextInputLayout
        textInputLayoutPassword = view.findViewById<View>(R.id.text_input_layout_password) as TextInputLayout
        textInputEmail = view.findViewById<View>(R.id.text_input_email) as TextInputEditText
        textInputPassword = view.findViewById<View>(R.id.text_input_password) as TextInputEditText
        buttonLogin = view.findViewById<View>(R.id.button_login) as AppCompatButton

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonLogin.setOnClickListener { verifyFromSQLite() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listeners) {
            listener = context
        } else {
            throw RuntimeException("$context must implement the Listeners interface")
        }
    }

    // Used to validate the input text fields and verify login credentials from SQLite
    private fun verifyFromSQLite() {

        // Validate email and password fields, if invalid return appropriate message
        if (!inputValidation.isInputEditTextEmail(textInputEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextFilled(textInputEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextFilled(textInputPassword, textInputLayoutPassword,getString(R.string.error_message_password))) { return }
        listener.hideKeyboard()

        // If valid credentials are entered, log user in
        // Whitespace is removed from text views using:  .trim { it <= ' ' }
        if (databaseHelper.checkUser(textInputEmail.text.toString().trim(), textInputPassword.text.toString().trim())) {
            listener.changeFragmentListener(Pages.LOCATION)
        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show()
        }
    }
}