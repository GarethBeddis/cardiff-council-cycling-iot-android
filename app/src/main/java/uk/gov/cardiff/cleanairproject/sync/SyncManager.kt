package uk.gov.cardiff.cleanairproject.sync

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import uk.gov.cardiff.cleanairproject.BuildConfig
import uk.gov.cardiff.cleanairproject.data.sql.DatabaseHelper

class SyncManager(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private val serverAddress = BuildConfig.API_URL
    private val requestQueue = Volley.newRequestQueue(context)

    fun isJourneySyncRequired(): Boolean {
        Log.d("UNSYNCED JOURNEY COUNT", databaseHelper.getUnsyncedJourneys().size.toString())
        return databaseHelper.getUnsyncedJourneys().isNotEmpty()
    }

    fun isReadingSyncRequired(): Boolean {
        Log.d("UNSYNCED READING COUNT", databaseHelper.getUnsyncedReadings().size.toString())
        return databaseHelper.getUnsyncedReadings().isNotEmpty()
    }

    fun syncJourneys() {
        // Get the unsynced journeys from the database and convert them to JSON
        val requestJSON = Gson().toJson(databaseHelper.getUnsyncedJourneys())
        // Prepare the JSON object
        Log.d("Journeys", requestJSON)
        val jsonRequest = JsonArrayRequest(
            Request.Method.POST,
            "$serverAddress/api/app/sync/journeys",
            JSONArray(requestJSON),
            Response.Listener<JSONArray> {response ->
                Log.d("Sync Success", response.toString())
            },
            Response.ErrorListener {error ->
                if (error is TimeoutError || error is ServerError) {
                    Log.e("Sync Error", error.toString())
                } else {
                    Log.e("Sync Error", error.toString())
                }
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }
}