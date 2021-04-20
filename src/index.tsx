import { NativeModules, NativeEventEmitter, EmitterSubscription } from 'react-native'

type BluetoothDevicesType = {
  startScan(): void
  connectToDevice(deviceId: string): void
  disconnectFromDevice(deviceId: string): void
  addEventListener(event: string, listener: (event: any) => any): EmitterSubscription
  removeEventListener(listener: EmitterSubscription): void
  removeAllListeners(event: string): void
}

const { BluetoothDevices } = NativeModules

const emitter = new NativeEventEmitter(BluetoothDevices)

BluetoothDevices.addEventListener = function (
  event: string,
  listener: (event: any) => any
): EmitterSubscription {
  return emitter.addListener(event, listener)
}

BluetoothDevices.removeEventListener = function (listener: EmitterSubscription) {
  return emitter.removeSubscription(listener)
}

BluetoothDevices.removeAllListeners = function (event: string) {
  return emitter.removeAllListeners(event)
}

export default BluetoothDevices as BluetoothDevicesType
