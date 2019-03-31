package uk.gov.cardiff.cleanairproject.auth

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import uk.gov.cardiff.cleanairproject.BuildConfig
import uk.gov.cardiff.cleanairproject.R


class ServerAuthenticator(private val context: Context) {

    private val serverAddress = BuildConfig.API_URL
    private val requestQueue = Volley.newRequestQueue(context)

    fun login(email: String, password: String, listener: ServerAuthenticatorListener) {
        // Prepare the request object
        val requestObject = HashMap<String, String>()
        requestObject["email"] = email
        requestObject["password"] = password
        Log.d("server", "$serverAddress/auth/login")
        // Prepare the request
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            "$serverAddress/auth/login",
            JSONObject(requestObject),
            Response.Listener<JSONObject> {response ->
                listener.onAuthSuccess(response.getString("token"))
            },
            Response.ErrorListener {error ->
                if (error is TimeoutError || error is ServerError) {
                    listener.onAuthFailure(context.getString(R.string.connection_error))
                } else {
                    listener.onAuthFailure(context.getString(R.string.error_valid_email_password))
                }
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }

    fun register(email: String, password: String, listener: ServerAuthenticatorListener) {
        // Prepare the request object
        val requestObject = HashMap<String, String>()
        requestObject["email"] = email
        requestObject["password"] = password
        Log.d("server", "$serverAddress/auth/login")
        // Prepare the request
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            "$serverAddress/auth/signup",
            JSONObject(requestObject),
            Response.Listener<JSONObject> {response ->
                listener.onAuthSuccess(response.getString("token"))
            },
            Response.ErrorListener {error ->
                if (error is TimeoutError || error is ServerError) {
                    listener.onAuthFailure(context.getString(R.string.connection_error))
                } else {
                    listener.onAuthFailure(context.getString(R.string.error_valid_email_password))
                }
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }
}