package uk.gov.cardiff.cleanairproject.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import uk.gov.cardiff.cleanairproject.data.sql.DatabaseHelper

class SyncService : Service() {

    private lateinit var binder: Binder
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var syncManager: SyncManager

    private var syncCallback: SyncServiceCallback? = null

    // Lifecycle
    override fun onCreate() {
        super.onCreate()
        binder = Binder()
        databaseHelper = DatabaseHelper(this)
        syncManager = SyncManager(this)
    }
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSyncService()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    // Start and Stop
    private fun startSyncService() {
        // Only continue if the service isn't already running
        if (!isRunning) {
            isRunning = true
            // Check if synchronisation is required
            if (syncManager.isJourneySyncRequired() || syncManager.isReadingSyncRequired()) {
                // Set the sync status to in progress
                setSyncStatus(SyncStates.IN_PROGRESS)
                // Sync Journeys
                syncManager.syncJourneys(object : SyncListener {
                    override fun onSyncSuccess() {
                        // Sync Readings
                        syncManager.syncReadings(object : SyncListener {
                            override fun onSyncSuccess() {
                                setSyncStatus(SyncStates.COMPLETE)
                                stopSyncService()
                            }
                            override fun onSyncFailure() {
                                setSyncStatus(SyncStates.WAITING)
                                stopSyncService()
                            }
                        })
                    }
                    override fun onSyncFailure() {
                        setSyncStatus(SyncStates.WAITING)
                        stopSyncService()
                    }
                })
            } else if (syncManager.isReadingSyncRequired()) {
                // Set the sync status to in progress
                setSyncStatus(SyncStates.IN_PROGRESS)
                // Sync Journeys
                syncManager.syncReadings(object : SyncListener {
                    override fun onSyncSuccess() {
                        setSyncStatus(SyncStates.COMPLETE)
                        stopSyncService()
                    }
                    override fun onSyncFailure() {
                        setSyncStatus(SyncStates.WAITING)
                        stopSyncService()
                    }
                })
            } else {
                setSyncStatus(SyncStates.COMPLETE)
                stopSyncService()
            }
        } else {
            stopSyncService()
        }
    }
    private fun stopSyncService() {
        syncCallback?.onSyncServiceStopped()
        isRunning = false
        stopSelf()
    }

    // Sync Status
    private fun setSyncStatus(state: SyncStates) {
        syncStatus = state
        syncCallback?.onSyncStateChange(state)
    }

    // Bindings
    inner class Binder : android.os.Binder() {
        val service: SyncService
            get() = this@SyncService
    }
    fun setCallBack(syncCallback: SyncServiceCallback?) {
        this.syncCallback = syncCallback
    }

    // Static variables
    companion object {
        var isRunning = false
        var syncStatus = SyncStates.COMPLETE
    }
}