package uk.gov.cardiff.cleanairproject.sync

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.Volley
import uk.gov.cardiff.cleanairproject.BuildConfig
import uk.gov.cardiff.cleanairproject.sql.DatabaseHelper

class SyncManager(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private val serverAddress = BuildConfig.API_URL
    private val requestQueue = Volley.newRequestQueue(context)

    fun isSyncRequired(): Boolean {
        Log.d("UNSYNCED JOURNEY COUNT", databaseHelper.getUnsyncedJourneys().size.toString())
        Log.d("UNSYNCED READING COUNT", databaseHelper.getUnsyncedReadings().size.toString())
        return databaseHelper.getUnsyncedJourneys().isNotEmpty() && databaseHelper.getUnsyncedReadings().isNotEmpty()
    }
}