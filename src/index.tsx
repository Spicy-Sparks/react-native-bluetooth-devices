import { NativeModules } from 'react-native';

type BluetoothDevicesType = {
  multiply(a: number, b: number): Promise<number>;
};

const { BluetoothDevices } = NativeModules;

export default BluetoothDevices as BluetoothDevicesType;
