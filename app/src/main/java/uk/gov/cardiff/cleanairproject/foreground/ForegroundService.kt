package uk.gov.cardiff.cleanairproject.foreground

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import uk.gov.cardiff.cleanairproject.MainActivity
import uk.gov.cardiff.cleanairproject.R

import java.util.concurrent.Executors

import java.util.concurrent.TimeUnit.SECONDS

class ForegroundService : Service() {

    private lateinit var binder: Binder
    private lateinit var locationManager: LocationManager
    private var locationGPS: Location? = null
    private var scheduler = Executors.newScheduledThreadPool(1)

    override fun onCreate() {
        super.onCreate()
        binder = Binder()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                START_FOREGROUND_SERVICE -> startForegroundService()
                STOP_FOREGROUND_SERVICE -> stopForegroundService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.")
        // Start getting the GPS location coordinates
        getCurrentLocation()
        // Prepare the notification on tap intent
        val onTapIntent = Intent(this, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val onTapPendingIntent = PendingIntent.getActivity(this, 0, onTapIntent, 0)
        // Prepare the notification stop intent
        val stopIntent = Intent(this, ForegroundService::class.java)
            .setAction(STOP_FOREGROUND_SERVICE)
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0)
        // Get the notification manager
        val manager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Prepare the notification
        val builder = NotificationCompat.Builder(this, "notify_001")
            .setContentIntent(onTapPendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_content))
            .setColor(resources.getColor(R.color.colorPrimary, null))
            .setSound(null)
            .setVibrate(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_pause, resources.getString(R.string.notification_action), stopPendingIntent)
        // Prepare the notification channel for Oreo and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "notify_001"
            val channelName = "Readings Channel"
            val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.enableVibration(false)
            manager.createNotificationChannel(channel)
            builder.setChannelId(channelID)
        }
        // Start foreground service.
        startForeground(1, builder.build())
        readings(builder, manager)
    }

    private fun stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.")
        // Stops the scheduler from creating more notifications
        scheduler.shutdownNow()
        // Stop foreground service and remove the notification
        stopForeground(true)
        // Stop the foreground service
        stopSelf()
    }


    private fun getCurrentLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            Log.d("GPS", "Trying")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0F, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if(location != null) {
                        locationGPS = location
                        Log.d("GPS", "Longitude:" + locationGPS!!.longitude + " Latitude:" + locationGPS!!.latitude)
                    }
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }
                override fun onProviderEnabled(provider: String?) {
                }
                override fun onProviderDisabled(provider: String?){
                }
            })
            Log.d("GPS", "It worked")
        } catch(ex: SecurityException){
            Log.d("GPS", "No location available")
        }

    }

    private fun readings(builder: NotificationCompat.Builder, manager: NotificationManager) {
        scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleWithFixedDelay({
            //notification content can be edited here
//            builder.setContentText((Math.random() * 100).toInt().toString())
            if(locationGPS != null) {
//                builder.setContentText("Longitude:" + locationGPS?.longitude + " Latitude:" + locationGPS?.latitude)
            }
            manager.notify(1, builder.build())

        }, 3, 3, SECONDS)
    }

    companion object {
        private const val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
        const val START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
    }

    inner class Binder : android.os.Binder() {
        val service: ForegroundService
            get() = this@ForegroundService
    }

}
