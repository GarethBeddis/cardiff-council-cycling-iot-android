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
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice

import java.util.concurrent.Executors

import java.util.concurrent.TimeUnit.SECONDS
import android.bluetooth.BluetoothDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ForegroundService : Service() {

    private lateinit var binder: Binder
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var bluetoothManager: BluetoothManager

    private var callback: ServiceCallback? = null
    private var notification: NotificationCompat.Builder? = null
    private var locationGPS: Location? = null
    private var scheduler = Executors.newScheduledThreadPool(1)
    private var bluetoothDevice: SimpleBluetoothDeviceInterface? = null

    var connected = false

    override fun onCreate() {
        super.onCreate()
        binder = Binder()
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    fun setCallBack(callback: ServiceCallback?) {
        this.callback = callback
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
        // Get the notification manager
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Build the notification
        notification = buildNotification()
        // Set the notification channel for Oreo and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "notify_001"
            val channelName = "Readings"
            val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
            notification?.setChannelId(channelID)
        }
        // Start foreground service.
        isRunning = true
        if(this.callback != null) {
            this.callback?.onServiceStarted()
        }
        startForeground(1, notification?.build())
        subscribeToBluetooth()
        subscribeToReadings()
    }

    private fun buildNotification():NotificationCompat.Builder {
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
        return NotificationCompat.Builder(this, "notify_001")
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
    }

    private fun stopForegroundService() {
        // Disconnect sensors if they're connected
        if (bluetoothDevice != null) {
            bluetoothManager.closeDevice(bluetoothDevice)
        }
        // Stops the scheduler and location updates
        scheduler.shutdownNow()
        locationManager.removeUpdates(locationListener)
        // Let the activity know the service has stopped
        if(callback != null) {
            callback?.onServiceStopped()
        }
        // Stop the foreground service
        isRunning = false
        stopForeground(true)
        stopSelf()
    }

    private fun getCurrentLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if(location != null) {
                    locationGPS = location
                    Log.d("GPS", "Longitude:" + locationGPS!!.longitude + " Latitude:" + locationGPS!!.latitude)
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onProviderDisabled(provider: String?){}
        }
        // Request Updates
        try {
            Log.d("GPS", "Trying")
            // Move event listening object
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0F, locationListener)
            Log.d("GPS", "It worked")
        } catch(ex: SecurityException){
            Log.d("GPS", "No location available")
        }
    }

    private fun subscribeToReadings() {
        scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleWithFixedDelay({
            if(locationGPS != null && this.callback != null) {
                this.callback?.onReading(locationGPS?.longitude)
            }
        }, 3, 3, SECONDS)
    }

    private fun subscribeToBluetooth() {
        bluetoothManager = BluetoothManager.getInstance()
        var sensorHub: BluetoothDevice? = null
        val pairedDevices = bluetoothManager.pairedDevicesList
        for (device in pairedDevices) {
            if (device.name == "Clean Air Sensor Hub") {
                sensorHub = device
            }
        }
        @SuppressWarnings("unused")
        if (sensorHub != null) {
            bluetoothManager.openSerialDevice(sensorHub.address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError)
        }
    }

    private fun onConnected(connectedDevice: BluetoothSerialDevice) {
        // Set the device and listeners
        bluetoothDevice = connectedDevice.toSimpleDeviceInterface()
        bluetoothDevice?.setListeners(
            this::onMessageReceived,
            null,
            this::onError)
        bluetoothDevice?.sendMessage("OK")
        // Update the notification text
        notification?.setContentTitle(resources.getString(R.string.notification_title_connected))
        notificationManager.notify(1, notification?.build())
        // Let the main activity know that the device is connected
        connected = true
        if(callback != null) {
            callback?.onConnected()
        }
    }

    private fun onMessageReceived(message: String) {
        Log.d("Bluetooth Message", message)
    }

    private fun onError(error: Throwable) {
        // Handle errors - This is hopefully just the sensors being disconnected
        Log.e("Bluetooth", error.message)
        stopForegroundService()
    }

    companion object {
        private const val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
        const val START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        var isRunning = false
    }

    inner class Binder : android.os.Binder() {
        val service: ForegroundService
            get() = this@ForegroundService
    }
}
