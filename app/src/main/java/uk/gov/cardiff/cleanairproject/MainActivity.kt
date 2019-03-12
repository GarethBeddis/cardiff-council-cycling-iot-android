package uk.gov.cardiff.cleanairproject

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_main.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Log

import uk.gov.cardiff.cleanairproject.foreground.ForegroundService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playPauseFab = playPauseButton as FloatingMusicActionButton
        playPauseFab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)


        playPauseButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {

                if(playPauseFab.currentMode.isShowingPlayIcon){
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.START_FOREGROUND_SERVICE
                    startService(intent)
                }else {
                    val intent = Intent(this@MainActivity, ForegroundService::class.java)
                    intent.action = ForegroundService.STOP_FOREGROUND_SERVICE
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




    //The BroadcastReceiver that listens for bluetooth broadcasts
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                ... //Device found
//            }
//            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                ... //Device is now connected
//            }
//            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                ... //Done searching
//            }
//            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
//                ... //Device is about to disconnect
//            }
//            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                ... //Device has disconnected
//            }
//        }
//    };

}
