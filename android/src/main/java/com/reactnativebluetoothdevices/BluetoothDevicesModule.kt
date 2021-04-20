package com.reactnativebluetoothdevices

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter

class BluetoothDevicesModule(reactContext: ReactApplicationContext): ReactContextBaseJavaModule(reactContext), LifecycleEventListener  {

  private var receiver: BroadcastReceiver? = null

  private fun onChange(deviceName: String) {
    // Report device name (if not empty) to the host
    val payload = Arguments.createMap()
    val deviceList = Arguments.createArray()
    if (!deviceName.isEmpty()) {
      deviceList.pushString(deviceName)
    }
    payload.putArray("devices", deviceList)
    this.reactApplicationContext
      .getJSModule(RCTDeviceEventEmitter::class.java)
      .emit("onConnectedDevices", payload)
  }

  override fun initialize() {
    super.initialize()
    /*val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val intentFilter = IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
    intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    receiver = object : BroadcastReceiver() {
      private val LOG_TAG = "BluetoothHeadsetDetect"
      override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
          // Bluetooth headset connection state has changed
          val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
          val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED)
          if (state == BluetoothProfile.STATE_CONNECTED) {
            // Device has connected, report it
            onChange(device.name)
          } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
            // Device has disconnected, report it
            onChange("")
          }
        } else if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
          val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE,
            BluetoothProfile.STATE_DISCONNECTED)
          if (state == BluetoothProfile.STATE_DISCONNECTED) {
            // Bluetooth is disabled
            onChange("")
          }
        }
      }
    }*/

    // Subscribe for intents
    //reactApplicationContext.registerReceiver(receiver, intentFilter)
    // Subscribe for lifecycle
    reactApplicationContext.addLifecycleEventListener(this)
  }

  override fun getName(): String {
    return "BluetoothDevices"
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onHostResume() {
    val activity = currentActivity ?: return
    val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
    for (device in devices) {
      val type = device.type
      if (type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
        // Device is found
        val deviceName = device.productName.toString()
        onChange(deviceName)
        return
      }
    }
    // No devices found
    onChange("")
  }

  override fun onHostPause() {}

  override fun onHostDestroy() {}

}
