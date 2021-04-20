# react-native-bluetooth-devices

Get and connect to bluetooth devices using React Native

## Installation

```sh
npm install react-native-bluetooth-devices
```

## Usage

```js
import BluetoothDevices from "react-native-bluetooth-devices";

// ...
BluetoothDevices.startScan()
BluetoothDevices.addEventListener("onConnectedDevices", (res: {
  devices: Array<DeviceType>
}) => {

  console.log(res.devices)

  BluetoothDevices.connectToDevice(res.devices[0].uuid)
  BluetoothDevices.disconnectFromDevice(res.devices[0].uuid)
})
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
