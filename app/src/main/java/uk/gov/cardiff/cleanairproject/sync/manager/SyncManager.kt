package uk.gov.cardiff.cleanairproject.sync.manager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import uk.gov.cardiff.cleanairproject.BuildConfig
import uk.gov.cardiff.cleanairproject.data.model.Journey
import uk.gov.cardiff.cleanairproject.data.sql.DatabaseHelper

class SyncManager(private val context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private val serverAddress = BuildConfig.API_URL
    private val requestQueue = Volley.newRequestQueue(context)

    fun isJourneySyncRequired(): Boolean {
        return databaseHelper.getUnsyncedJourneysCount() > 0
    }

    fun isReadingSyncRequired(): Boolean {
        return databaseHelper.getUnsyncedReadingsCount() > 0
    }

    fun syncJourneys(listener: SyncListener) {
        // Get the unsynced journeys from the database and convert them to JSON
        val journeysJSON = Gson().toJson(databaseHelper.getUnsyncedJourneys())
        val requestJSON = "{\"token\": \"${getToken()}\", \"journeys\": $journeysJSON}"
        // Prepare the JSON object
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            "$serverAddress/api/app/sync/journeys",
            JSONObject(requestJSON),
            Response.Listener<JSONObject> {response ->
                // Process the response
                val responseJourneys = JSONArray(response.getString("journeys"))
                val journeys = mutableListOf<Journey>()
                for (i in 0 until responseJourneys.length()) {
                    journeys.add(Gson().fromJson(responseJourneys[i].toString(), Journey::class.java))
                }
                databaseHelper.updateJourneys(journeys)
                // Loop recursively until all journeys are synced
                if (isJourneySyncRequired()) {
                    syncJourneys(listener)
                } else {
                    listener.onSyncSuccess()
                }
            },
            Response.ErrorListener {
                listener.onSyncFailure()
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }

    fun syncReadings(listener: SyncListener) {
        // Get the unsynced readings from the database and convert them to JSON
        val readingsJSON = Gson().toJson(databaseHelper.getUnsyncedReadings())
        val requestJSON = "{\"token\": \"${getToken()}\", \"readings\": $readingsJSON}"
        // Prepare the JSON object
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            "$serverAddress/api/app/sync/readings",
            JSONObject(requestJSON),
            Response.Listener<JSONObject> {response ->
                val responseReadings = JSONArray(response.getString("readings"))
                val readingIDs = mutableListOf<Int>()
                for (i in 0 until responseReadings.length()) {
                    readingIDs.add(responseReadings.getInt(i))
                }
                databaseHelper.updateReadings(readingIDs)
                // Loop recursively until all journeys are synced
                if (isReadingSyncRequired()) {
                    syncReadings(listener)
                } else {
                    listener.onSyncSuccess()
                }
            },
            Response.ErrorListener {
                listener.onSyncFailure()
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }

    private fun getToken(): String {
        // Get the token from shared prefs
        return context.getSharedPreferences("Account", AppCompatActivity.MODE_PRIVATE)
            .getString("token", "")!!
    }
}