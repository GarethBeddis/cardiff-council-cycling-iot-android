package uk.gov.cardiff.cleanairproject.sensors

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

import android.bluetooth.BluetoothDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import uk.gov.cardiff.cleanairproject.data.model.Journey
import uk.gov.cardiff.cleanairproject.data.model.Reading
import uk.gov.cardiff.cleanairproject.data.sql.DatabaseHelper
import uk.gov.cardiff.cleanairproject.sync.SyncService
import java.util.*

class SensorService : Service() {

    private lateinit var binder: Binder
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var databaseHelper: DatabaseHelper

    private var sensorCallback: SensorServiceCallback? = null
    private var notification: NotificationCompat.Builder? = null
    private var bluetoothDevice: SimpleBluetoothDeviceInterface? = null
    private var locationGPS: Location? = null
    private var journey: Journey? = null

    var connected = false

    // Lifecycle functions
    override fun onCreate() {
        super.onCreate()
        binder = Binder()
        databaseHelper = DatabaseHelper(this)
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
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    // Start and Stop Functions
    private fun startForegroundService() {
        // Start getting the GPS location coordinates
        getCurrentLocation()
        // Get the notification manager
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Build the notification
        notification = buildNotification()
        // Set the notification channel for Oreo and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "notify_001"
            val channel = NotificationChannel(channelID, "Readings", NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
            notification?.setChannelId(channelID)
        }
        // Start foreground service.
        isRunning = true
        startForeground(1, notification?.build())
        // Let the activity know the service has started
        if (this.sensorCallback != null) {
            this.sensorCallback?.onSensorServiceStarted()
        }
        // Connect to the Arduino
        connectToBluetooth()
    }
    private fun stopForegroundService() {
        // Disconnect sensors if they're connected
        if (bluetoothDevice != null) {
            bluetoothManager.closeDevice(bluetoothDevice)
        }
        // Stop location updates
        locationManager.removeUpdates(locationListener)
        // Delete the journey if there are no readings, otherwise start synchronisation
        if (journey != null) {
            startService(Intent(this, SyncService::class.java))
        }
        // Stop the foreground service
        connected = false
        isRunning = false
        sensorCallback?.onSensorServiceStopped()
        stopForeground(true)
        stopSelf()
    }

    // Notifications
    private fun buildNotification():NotificationCompat.Builder {
        // Prepare the notification on tap intent
        val onTapIntent = Intent(this, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val onTapPendingIntent = PendingIntent.getActivity(this, 0, onTapIntent, 0)
        // Prepare the notification stop intent
        val stopIntent = Intent(this, SensorService::class.java)
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

    // Bluetooth
    private fun connectToBluetooth() {
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
        if (isRunning) {
            // Create a journey in the database
            journey = databaseHelper.addJourney(
                Journey(
                    RemoteId = 0,
                    Synced = false
                )
            )
            // Set the device and listeners
            bluetoothDevice = connectedDevice.toSimpleDeviceInterface()
            bluetoothDevice?.setListeners(
                this::onMessageReceived,
                null,
                this::onError
            )
            bluetoothDevice?.sendMessage("OK")
            // Update the notification text
            notification?.setContentTitle(resources.getString(R.string.notification_title_connected))
            notificationManager.notify(1, notification?.build())
            // Let the main activity know that the device is connected
            connected = true
            sensorCallback?.onConnected()
        } else {
            // If the service is no longer running, close the connection
            bluetoothManager.closeDevice(connectedDevice.toSimpleDeviceInterface())
        }
    }
    private fun onMessageReceived(message: String) {
        newReading(message)
    }
    private fun onError(error: Throwable) {
        // Handle errors - This is hopefully just the sensors being disconnected
        Log.e("Bluetooth", error.message)
        if (isRunning) {
            stopForegroundService()
        }
    }

    // Readings
    private fun newReading(data: String) {
        if (locationGPS != null) {
            try {
                // Convert the JSON data
                val jsonData = JSONObject(data).getJSONObject("cleanAir")
                // Add the reading to the database
                databaseHelper.addReading(Reading(
                    JourneyId = journey!!.id,
                    JourneyRemoteId = null,
                    NoiseReading = jsonData.getDouble("db"),
                    No2Reading = jsonData.getDouble("no2"),
                    PM10Reading = jsonData.getDouble("pm100"),
                    PM25Reading = jsonData.getDouble("pm25"),
                    TimeTaken = Calendar.getInstance().timeInMillis,
                    Longitude = locationGPS!!.longitude,
                    Latitude = locationGPS!!.latitude
                ))
                // Send the reading to the activity if it's connected
                sensorCallback?.onReading(
                    locationGPS?.longitude,
                    locationGPS?.latitude,
                    jsonData.getInt("no2"),
                    jsonData.getInt("pm25"),
                    jsonData.getInt("pm100"),
                    jsonData.getInt("db")
                )
            } catch (error: JSONException) {
                Log.e("Invalid JSON", error.toString())
            }
        }
    }
    private fun getCurrentLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if(location != null) {
                    locationGPS = location
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onProviderDisabled(provider: String?){}
        }
        // Request Updates
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0F, locationListener)
        } catch(ex: SecurityException){
            stopForegroundService()
        }
    }

    // Bindings
    inner class Binder : android.os.Binder() {
        val service: SensorService
            get() = this@SensorService
    }
    fun setCallBack(sensorCallback: SensorServiceCallback?) {
        this.sensorCallback = sensorCallback
    }

    // Static variables
    companion object {
        const val START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        var isRunning = false
    }
}
