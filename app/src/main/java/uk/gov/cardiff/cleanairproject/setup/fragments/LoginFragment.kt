package uk.gov.cardiff.cleanairproject.setup.fragments

import android.content.Context
import android.os.Bundle
import android.renderscript.ScriptGroup
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
import android.view.inputmethod.InputMethodManager
import uk.gov.cardiff.cleanairproject.R


class LoginFragment : Fragment() {

    private lateinit var listener: Listeners
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout
    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText
    private lateinit var appCompatButtonLogin: AppCompatButton
    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set Fragment Animations
        this.exitTransition = Animations.getSlideLeftAnimation()
        this.enterTransition = Animations.getSlideRightAnimation()
        this.reenterTransition = Animations.getSlideLeftAnimation()

        // initializing the objects
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
        textInputLayoutEmail = view.findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = view.findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout
        textInputEditTextEmail = view.findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword = view.findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText
        appCompatButtonLogin = view.findViewById<View>(R.id.appCompatButtonLogin) as AppCompatButton

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appCompatButtonLogin.setOnClickListener { verifyFromSQLite() }
//        this.textViewLinkRegister.setOnClickListener { verifyFromSQLite() }
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
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) { return }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword,getString(R.string.error_message_email))) { return }

        // Hide the keyboard if open so snackbar message is visible
        // Ref: https://stackoverflow.com/questions/13593069/androidhide-keyboard-after-button-click/13593232
        try {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            // TODO: handle exception
        }

        // If valid credentials are entered, log user in
        // Whitespace is removed from text views using:  .trim { it <= ' ' }
        if (databaseHelper.checkUser(textInputEditTextEmail.text.toString().trim(), textInputEditTextPassword.text.toString().trim())) {
            listener.changeFragmentListener(Pages.LOCATION)
        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show()
        }
    }
}