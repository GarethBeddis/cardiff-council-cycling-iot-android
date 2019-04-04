package uk.gov.cardiff.cleanairproject.auth.validation

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout

// Ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/blob/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/helpers/InputValidation.kt
class InputValidation

    // Constructor
    (private val context: Context) {

    // Method to check if InputEditText is filled
    fun isInputEditTextFilled(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean {
        val value = textInputEditText.text.toString().trim()
        if (value.isEmpty()) {
            textInputLayout.error = message
            return false
        } else {
            textInputLayout.isErrorEnabled = false
        }
        return true
    }

    // Method to check if InputEditText contains a valid email
    fun isInputEditTextEmail(textInputEditText: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean {
        val value = textInputEditText.text.toString().trim()
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputLayout.error = message
            return false
        } else {
            textInputLayout.isErrorEnabled = false
        }
        return true
    }

    // Method to check both InputEditText values match
    fun isInputEditTextMatches(textInputEditText1: TextInputEditText, textInputEditText2: TextInputEditText, textInputLayout: TextInputLayout, message: String): Boolean {
        val value1 = textInputEditText1.text.toString().trim()
        val value2 = textInputEditText2.text.toString().trim()
        if (!value1.contentEquals(value2)) {
            textInputLayout.error = message
            return false
        } else {
            textInputLayout.isErrorEnabled = false
        }
        return true
    }
}
