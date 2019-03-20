package uk.gov.cardiff.cleanairproject

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.AsyncTask
import android.util.Log

import uk.gov.cardiff.cleanairproject.foreground.ForegroundService
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        var m_address: String = "98:D3:61:FD:59:65"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectToDevice(this).execute()

        val playPauseFab = playPauseButton as FloatingMusicActionButton
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)


        playPauseButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {

                if(playPauseFab.currentMode.isShowingPlayIcon){
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.START_FOREGROUND_SERVICE
                    sendCommand("1")
                    startService(intent)
                }else {
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.STOP_FOREGROUND_SERVICE
                    sendCommand("0")
                    startService(intent)
                }
            }
        })

        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        registerReceiver(receiver, filter)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action
            val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            when(action){
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val deviceName = device.name
                    val deviceMAC = device.address
                    Log.d("BLUETOOTH", "Connected to a device")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver);
    }

    fun sendCommand(input: String) {
        Log.d("BTSocket", m_bluetoothSocket.toString())
        if (m_bluetoothSocket != null) {
            try{
                Log.d("BTSocket", input)
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                Log.d("BT", "Disconnected")
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    class ConnectToDevice(context: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = context
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    Log.d("BTSocket", device.name)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
        }
    }

}
