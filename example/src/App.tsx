import * as React from 'react'

import { StyleSheet, View, Text } from 'react-native'
import BluetoothDevices, { DeviceType } from 'react-native-bluetooth-devices'

export default function App() {

  React.useEffect(() => {
    BluetoothDevices.startScan()
    BluetoothDevices.addEventListener("onConnectedDevices", (res: {
      devices: Array<DeviceType>
    }) => {

      console.log(res.devices)

      BluetoothDevices.connectToDevice(res.devices[0].uuid)
      BluetoothDevices.disconnectFromDevice(res.devices[0].uuid)
    })
  }, [])

  return (
    <View style={styles.container}>
      <Text>Hello, Bluetooth!</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
})
