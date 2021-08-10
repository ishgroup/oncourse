import React from 'react';
import { createDrawerNavigator, DrawerContentScrollView } from '@react-navigation/drawer';
import { Drawer } from 'react-native-paper';
import { DrawerContentComponentProps } from '@react-navigation/drawer/src/types';
import Header from './Header';
import { TimetableScreen } from '../../screens/timetable/TimetableScreen';
import LogoutScreen from '../../screens/LogoutScreen';
import { RootDrawerParamList } from '../../model/Navigation';

const CustomDrawerContent = (props: DrawerContentComponentProps) => (
  <DrawerContentScrollView {...props}>
    <Drawer.Section>
      {props.state.routes.map((r, index) => (
        <Drawer.Item
          label={r.name}
          key={r.key}
          active={index === props.state.index}
          onPress={() => props.navigation.navigate(r.name)}
        />
      ))}
    </Drawer.Section>
  </DrawerContentScrollView>
);

const DrawerNav = createDrawerNavigator<RootDrawerParamList>();

const DrawerNavigation = () => (
  <DrawerNav.Navigator
    screenOptions={{
      headerShown: true,
      unmountOnBlur: true,
      header: Header,
    }}
    drawerContent={CustomDrawerContent}
  >
    <DrawerNav.Screen options={{ headerShown: false }} name="Timetable" component={TimetableScreen} />
    <DrawerNav.Screen name="Logout" component={LogoutScreen} />
  </DrawerNav.Navigator>
);

export default DrawerNavigation;
