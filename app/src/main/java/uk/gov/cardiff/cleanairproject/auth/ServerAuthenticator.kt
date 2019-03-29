package uk.gov.cardiff.cleanairproject.auth

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import uk.gov.cardiff.cleanairproject.R


class ServerAuthenticator(private val context: Context) {

    private val serverAddress = "http://192.168.0.58:3000"
    private val requestQueue = Volley.newRequestQueue(context)

    fun login(email: String, password: String, listener: ServerAuthenticatorListener) {
        // Prepare the request object
        val requestObject = HashMap<String, String>()
        requestObject["email"] = email
        requestObject["password"] = password
        // Prepare the request
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            "$serverAddress/auth/app/login",
            JSONObject(requestObject),
            Response.Listener<JSONObject> {response ->
                listener.onAuthSuccess(response.getString("token"))
            },
            Response.ErrorListener {error ->
                if (error is TimeoutError) {
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
        // Prepare the request
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            "$serverAddress/auth/app/register",
            JSONObject(requestObject),
            Response.Listener<JSONObject> {response ->
                listener.onAuthSuccess(response.getString("token"))
            },
            Response.ErrorListener {error ->
                if (error is TimeoutError) {
                    listener.onAuthFailure(context.getString(R.string.connection_error))
                } else {
                    listener.onAuthFailure(context.getString(R.string.error_valid_email_password))
                }
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }
}