import { Octicons } from '@expo/vector-icons'; import {
  createDrawerNavigator,
  DrawerContentScrollView,
  DrawerItemList,
  DrawerItem,
} from '@react-navigation/drawer';
import {createStackNavigator} from '@react-navigation/stack';
import { View, Text, Button, TouchableHighlight } from 'react-native';
import * as React from 'react';

import Colors from '../constants/Colors';
import useColorScheme from '../hooks/useColorScheme';
import TabOneScreen from '../screens/TabOneScreen';
import TabTwoScreen from '../screens/TabTwoScreen';
import {BottomTabParamList, TabOneParamList, TabTwoParamList} from '../types';



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
