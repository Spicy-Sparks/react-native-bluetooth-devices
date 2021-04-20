package com.reactnativebluetoothdevices

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import java.util.*

class BluetoothDevicesModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), LifecycleEventListener {

  var receiver: BroadcastReceiver? = null
  var connectedUuid: String? = null
  var btSocket: BluetoothSocket? = null

  override fun getName(): String {
    return "BluetoothDevices"
  }

  @ReactMethod
  fun startScan() {
    val adapter = BluetoothAdapter.getDefaultAdapter()
    val intentFilter = IntentFilter()
    intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
    intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
    intentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED)
    intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
    intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)
    intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    receiver = object : BroadcastReceiver() {
      @RequiresApi(Build.VERSION_CODES.M)
      override fun onReceive(context: Context, intent: Intent) {
        sendInformations()
      }
    }
    reactApplicationContext.registerReceiver(receiver, intentFilter)
    reactApplicationContext.addLifecycleEventListener(this)
    adapter.startDiscovery()
  }

  @ExperimentalStdlibApi
  @ReactMethod
  fun connectToDevice (uuid: String) {

    val UUID = UUID.nameUUIDFromBytes(uuid.encodeToByteArray());

    val adapter = BluetoothAdapter.getDefaultAdapter()
    val device: BluetoothDevice? = adapter?.bondedDevices?.reduce { final, deviceTmp ->
      if (deviceTmp != null && deviceTmp.uuids[0].uuid.equals(UUID))
        deviceTmp
      else final
    }

    if(device == null || device.getBondState() != BluetoothDevice.BOND_BONDED) {
      return
    }

    try {
      btSocket = device.createInsecureRfcommSocketToServiceRecord(UUID)
      btSocket?.connect()
    } catch (e: Exception) {
      connectedUuid = null
      btSocket = null
      return
    }
  }

  @ReactMethod
  fun disconnectFromDevice (uuid: String) {
    connectedUuid = null
    if(btSocket != null) {
      try {
        btSocket?.close()
        btSocket = null
      }
      catch (e: Exception) {}
    }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun sendInformations () {
    val payload = Arguments.createMap()
    val devicesResult = Arguments.createArray()
    val adapter = BluetoothAdapter.getDefaultAdapter()
    val pairedDevices: Set<BluetoothDevice>? = adapter?.bondedDevices
    pairedDevices?.forEach { device ->
      val deviceMap = Arguments.createMap()
      deviceMap.putString("portType", device.type.toString())
      // (type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO)
      deviceMap.putString("name", device.name)
      deviceMap.putString("uuid", device.uuids[0].uuid.toString())
      devicesResult.pushMap(deviceMap)
    }
    payload.putArray("devices", devicesResult)
    reactApplicationContext
      .getJSModule(RCTDeviceEventEmitter::class.java)
      .emit("onConnectedDevices", payload)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onHostResume() {
    sendInformations()
  }

  override fun onHostPause() {}

  override fun onHostDestroy() {
    reactApplicationContext.unregisterReceiver(receiver)
  }
}
