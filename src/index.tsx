import { NativeModules, NativeEventEmitter, EmitterSubscription } from 'react-native';

type BluetoothDevicesType = {
  multiply(a: number, b: number): Promise<number>;
};

const { BluetoothDevices } = NativeModules;

console.log(BluetoothDevices, NativeModules)

const emitter = new NativeEventEmitter(BluetoothDevices);

BluetoothDevices.addEventListener = function (
  event: string,
  listener: (event: any) => any
): EmitterSubscription {
  return emitter.addListener(event, listener);
};

BluetoothDevices.removeEventListener = function (listener: EmitterSubscription) {
  return emitter.removeSubscription(listener);
};

BluetoothDevices.removeAllListeners = function (event: string) {
  return emitter.removeAllListeners(event);
};

export default BluetoothDevices as BluetoothDevicesType;
