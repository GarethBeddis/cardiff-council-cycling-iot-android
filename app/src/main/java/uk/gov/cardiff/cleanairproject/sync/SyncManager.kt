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
import uk.gov.cardiff.cleanairproject.data.model.Journey
import uk.gov.cardiff.cleanairproject.data.model.Reading
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

    fun syncJourneys(listener: SyncListener) {
        // Get the unsynced journeys from the database and convert them to JSON
        val requestJSON = Gson().toJson(databaseHelper.getUnsyncedJourneys())
        // Prepare the JSON object
        val jsonRequest = JsonArrayRequest(
            Request.Method.POST,
            "$serverAddress/api/app/sync/journeys",
            JSONArray(requestJSON),
            Response.Listener<JSONArray> {response ->
                val journeys = mutableListOf<Journey>()
                for (i in 0 until response.length()) {
                    journeys.add(Gson().fromJson(response[i].toString(), Journey::class.java))
                }
                databaseHelper.updateJourneys(journeys)
                listener.onSyncSuccess()
            },
            Response.ErrorListener {
                listener.onSyncFailure()
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }

    fun syncReadings(listener: SyncListener) {
        // Get the unsynced readings from the database and convert them to JSON
        val requestJSON = Gson().toJson(databaseHelper.getUnsyncedReadings())
        // Prepare the JSON object
        Log.d("Readings", requestJSON)
        val jsonRequest = JsonArrayRequest(
            Request.Method.POST,
            "$serverAddress/api/app/sync/readings",
            JSONArray(requestJSON),
            Response.Listener<JSONArray> {response ->
                val readingIDs = mutableListOf<Int>()
                for (i in 0 until response.length()) {
                    readingIDs.add(response.getInt(i))
                }
                databaseHelper.updateReadings(readingIDs)
                listener.onSyncSuccess()
            },
            Response.ErrorListener {
                listener.onSyncFailure()
            })
        // Make the request
        requestQueue.add(jsonRequest)
    }
}