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
    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText
    private lateinit var textInputEditTextConfirmPassword: TextInputEditText
    private lateinit var appCompatButtonRegister: AppCompatButton
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
        textInputLayoutEmail = view.findViewById(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = view.findViewById(R.id.textInputLayoutPassword) as TextInputLayout
        textInputLayoutConfirmPassword = view.findViewById(R.id.textInputLayoutConfirmPassword) as TextInputLayout
        textInputEditTextEmail = view.findViewById(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = view.findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText
        textInputEditTextConfirmPassword = view.findViewById(R.id.textInputEditTextConfirmPassword) as TextInputEditText
        appCompatButtonRegister = view.findViewById(R.id.appCompatButtonRegister) as AppCompatButton

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appCompatButtonRegister.setOnClickListener { postDataToSQLite() }
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

        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) { return }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_password_match))) { return }
        listener.hideKeyboard()

        if (!databaseHelper.checkUser(textInputEditTextEmail.text.toString().trim())) {
            val user = User(
                email = textInputEditTextEmail.text.toString().trim(),
                password = textInputEditTextPassword.text.toString().trim())

            databaseHelper.addUser(user)

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()

        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show()
        }
    }
}
