import * as React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import LinkingConfiguration from '../../constants/LinkingConfiguration';
import NotFoundScreen from '../../screens/NotFoundScreen';
import { RootStackParamList } from '../../../../types';
import DrawerNavigation from './DrawerNavigation';

const Stack = createStackNavigator<RootStackParamList>();

function RootNavigator() {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false
      }}
      initialRouteName="Root"
    >
      <Stack.Screen name="Root" component={DrawerNavigation} />
      <Stack.Screen name="NotFound" component={NotFoundScreen} options={{ title: 'Oops!' }} />
    </Stack.Navigator>
  );
}

export default function Navigation() {
  return (
    <NavigationContainer
      linking={LinkingConfiguration}
    >
      <RootNavigator />
    </NavigationContainer>
  );
}
