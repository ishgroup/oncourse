import React from 'react';
import {
  createDrawerNavigator,
  DrawerContentScrollView,
} from '@react-navigation/drawer';
import { Drawer } from 'react-native-paper';
import { DrawerContentComponentProps } from '@react-navigation/drawer/src/types';
import { View, Text } from 'react-native';
import Header from './Header';
import { RootDrawerParamList } from '../../../../types';
import { DashboardScreen } from '../../screens/DashboardScreen';

function StubScreen({ route }) {
  return (
    <View style={{ flex: 1, justifyContent: 'flex-start', alignItems: 'center' }}>
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text>
          {route.name}
          {' '}
          screen
        </Text>
      </View>
    </View>
  );
}

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

export default function DrawerNavigation() {
  return (
    <DrawerNav.Navigator
      screenOptions={{
        headerShown: true,
        header: (headerProps) => (
          <Header {...headerProps} />
        ),
      }}
      drawerContent={CustomDrawerContent}
    >
      <DrawerNav.Screen name="Dashboard" component={DashboardScreen} />
      <DrawerNav.Screen name="Timetable" component={StubScreen} />
      <DrawerNav.Screen name="Resourses" component={StubScreen} />
      <DrawerNav.Screen name="My profile" component={StubScreen} />
      <DrawerNav.Screen name="Subscriptions" component={StubScreen} />
      <DrawerNav.Screen name="History" component={StubScreen} />
      <DrawerNav.Screen name="Approvals" component={StubScreen} />
      <DrawerNav.Screen name="Logout" component={StubScreen} />
    </DrawerNav.Navigator>
  );
}
