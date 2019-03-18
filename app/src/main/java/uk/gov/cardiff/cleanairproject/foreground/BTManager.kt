package uk.gov.cardiff.cleanairproject.foreground

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.util.*

class BTManager : BTListener {


    companion object{
//        private lateinit var instance: BTManager

//        val managerInstance: BTManager
//        get() {
//            if(instance == null){
//                instance = BTManager()
//            }
//            return instance
//        }
        fun sendCommand(): BTManager = BTManager()
        fun connect(): BTManager = BTManager()
        fun disconnect(): BTManager = BTManager()

        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        var m_address: String = "98:D3:61:FD:59:65"
    }

    override fun sendCommand(input: String) {
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

    override fun connect(context:Context){
        ConnectToDevice(context).execute()
        sendCommand("1")
        Log.d("BT", "Connected")
    }

    override fun disconnect() {
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
                    Log.d("BTSocket", "")
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