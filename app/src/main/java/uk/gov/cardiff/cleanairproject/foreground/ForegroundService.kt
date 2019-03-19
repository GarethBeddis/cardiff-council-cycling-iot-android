package uk.gov.cardiff.cleanairproject.foreground

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import uk.gov.cardiff.cleanairproject.MainActivity
import uk.gov.cardiff.cleanairproject.R
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ForegroundService : Service() {

//    private val notManager: NotificationManager? = null

    companion object {

        private val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"

        val START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"

        val STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

        var scheduler = Executors.newScheduledThreadPool(1)
}

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action

            when (action) {
                START_FOREGROUND_SERVICE -> startForegroundService()
                STOP_FOREGROUND_SERVICE -> stopForegroundService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /* Used to build and start foreground service. */
    private fun startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.")

        // Create notification default intent.
        val intent = Intent(this, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Create notification builder.
        val notManager: NotificationManager

        val mBuilder = NotificationCompat.Builder(this, "notify_001")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.ic_logo_gradient)
        mBuilder.setContentTitle("Cardiff Clean Air Project")
        mBuilder.setContentText("")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setOnlyAlertOnce(true)

        notManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "notify_001"
            val serviceChannel = NotificationChannel(
                channelID,
                "Readings Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notManager.createNotificationChannel(serviceChannel)
            mBuilder.setChannelId(channelID)
        }

        // Start foreground service.
        startForeground(1, mBuilder.build())

//        readings(mBuilder, notManager)
    }

    private fun stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.")

        //stops the scheduler from creating more notifications
        scheduler.shutdownNow()

        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }

    private fun readings(builder: NotificationCompat.Builder, manager: NotificationManager) {
        scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleWithFixedDelay(Runnable(){
            //notification content can be edited here
            builder.setContentText((Math.random() * 100).toInt().toString())
            manager.notify(1, builder.build())
        },3,3, TimeUnit.SECONDS)
    }



}
