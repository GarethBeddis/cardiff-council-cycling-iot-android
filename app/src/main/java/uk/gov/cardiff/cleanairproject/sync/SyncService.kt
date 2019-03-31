package uk.gov.cardiff.cleanairproject.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import uk.gov.cardiff.cleanairproject.sql.DatabaseHelper

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
        isRunning = true
        syncCallback?.onSyncStateChange(SyncStates.IN_PROGRESS)
        syncManager.isSyncRequired()
        android.os.Handler().postDelayed({
            syncCallback?.onSyncStateChange(SyncStates.COMPLETE)
            stopSyncService()
        }, 1000)
    }
    private fun stopSyncService() {
        syncCallback?.onSyncServiceStopped()
        isRunning = false
        stopSelf()
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
    }
}