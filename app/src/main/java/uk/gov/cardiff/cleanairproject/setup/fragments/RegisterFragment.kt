package uk.gov.cardiff.cleanairproject.setup.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.content.Context
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup

import uk.gov.cardiff.cleanairproject.R
import uk.gov.cardiff.cleanairproject.model.User
import uk.gov.cardiff.cleanairproject.setup.Animations
import uk.gov.cardiff.cleanairproject.setup.Listeners
import uk.gov.cardiff.cleanairproject.setup.Pages
import uk.gov.cardiff.cleanairproject.sql.DatabaseHelper
import uk.gov.cardiff.cleanairproject.validation.InputValidation

// TODO: Automatically login after register

// Adapted from ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/tree/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/activities
class RegisterFragment : Fragment() {

    private lateinit var listener: Listeners

    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout
    private lateinit var textInputLayoutConfirmPassword: TextInputLayout
    private lateinit var textInputEmail: TextInputEditText
    private lateinit var textInputPassword: TextInputEditText
    private lateinit var textInputConfirmPassword: TextInputEditText
    private lateinit var buttonRegister: AppCompatButton
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
        val view = inflater.inflate(uk.gov.cardiff.cleanairproject.R.layout.fragment_register, container, false)

        // Initialise views
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        textInputLayoutEmail = view.findViewById(R.id.text_input_layout_email) as TextInputLayout
        textInputLayoutPassword = view.findViewById(R.id.text_input_layout_password) as TextInputLayout
        textInputLayoutConfirmPassword = view.findViewById(R.id.text_input_layout_confirm_password) as TextInputLayout
        textInputEmail = view.findViewById(R.id.text_input_email) as TextInputEditText
        textInputPassword = view.findViewById<View>(R.id.text_input_password) as TextInputEditText
        textInputConfirmPassword = view.findViewById(R.id.text_input_confirm_password) as TextInputEditText
        buttonRegister = view.findViewById(R.id.button_register) as AppCompatButton

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonRegister.setOnClickListener { postDataToSQLite() }
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

        if (!inputValidation.isInputEditTextFilled(textInputEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextEmail(textInputEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextFilled(textInputPassword, textInputLayoutPassword, getString(R.string.error_message_password))) { return }
        if (!inputValidation.isInputEditTextMatches(textInputPassword, textInputConfirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_password_match))) { return }
        listener.hideKeyboard()

        // Add user to the database and log in
        if (!databaseHelper.checkUser(textInputEmail.text.toString().trim())) {
            val user = User(
                email = textInputEmail.text.toString().trim(),
                password = textInputPassword.text.toString().trim())

            databaseHelper.addUser(user)
            listener.changeFragmentListener(Pages.LOCATION)

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()

        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show()
        }
    }
}
