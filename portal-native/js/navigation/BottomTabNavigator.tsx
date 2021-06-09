import React from 'react';
import { Octicons } from '@expo/vector-icons';
import {
  createDrawerNavigator,
  DrawerContentScrollView,
  DrawerItemList
} from '@react-navigation/drawer';
import { View, Text, TouchableHighlight } from 'react-native';



function Feed({ navigation }) {
  return (
    <View style={{ flex: 1, justifyContent: "flex-start", alignItems: 'center' }}>
      <View style={{ height: 60, justifyContent: "flex-start", flexDirection: "row", width: "100%", backgroundColor: "#ddd" }}>
        <View style={{ justifyContent: "center", paddingLeft: 16 }}>
          <TouchableHighlight onPress={() => navigation.openDrawer()} underlayColor="#DDDDDD">
            <View>
              <Octicons name="three-bars" size={24} color="black" />
            </View>
          </TouchableHighlight>
        </View>
      </View>
      <View style={{ flex: 1, justifyContent: "center", alignItems: 'center' }}>
        <Text>Feed Screen</Text>
      </View>
    </View>
  );
}

function Notifications({ navigation }) {
  return (
  <View style={{ flex: 1, justifyContent: "flex-start", alignItems: 'center' }}>
    <View style={{ height: 60, justifyContent: "flex-start", flexDirection: "row", width: "100%", backgroundColor: "#ddd" }}>
      <View style={{ justifyContent: "center", paddingLeft: 16 }}>
        <TouchableHighlight onPress={() => navigation.openDrawer()} underlayColor="#DDDDDD">
          <View>
            <Octicons name="three-bars" size={24} color="black" />
          </View>
        </TouchableHighlight>
      </View>
    </View>
    <View style={{ flex: 1, justifyContent: "center", alignItems: 'center' }}>
      <Text>Notifications Screen</Text>
    </View>
  </View>
  );
}

function CustomDrawerContent(props) {
  return (
    <DrawerContentScrollView {...props}>
      <DrawerItemList {...props} />
    </DrawerContentScrollView>
  );
}

const Drawer = createDrawerNavigator();

export default function AppDrawer() {
  return (
    <Drawer.Navigator drawerContent={props => <CustomDrawerContent {...props} />}>
      <Drawer.Screen name="Feed" component={Feed} />
      <Drawer.Screen name="Notifications" component={Notifications} />
    </Drawer.Navigator>
  );
}
