import * as React from 'react'

import { StyleSheet, View, Text } from 'react-native'
import BluetoothDevices from 'react-native-bluetooth-devices'

export default function App() {

  React.useEffect(() => {
    BluetoothDevices.startScan()
    BluetoothDevices.addEventListener("onConnectedDevices", (res) => {
      alert(JSON.stringify(res))
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
