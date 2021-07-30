import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import LinkingConfiguration from '../../constants/LinkingConfiguration';
import NotFoundScreen from '../../screens/NotFoundScreen';
import { RootStackParamList } from '../../../../types';
import DrawerNavigation from './DrawerNavigation';
import LoginScreen from '../../screens/login/LoginScreen';
import { useAppDispatch, useAppSelector } from '../../hooks/redux';
import { useLinkingRedirects } from '../../hooks/linking';
import { getToken, setToken } from '../../utils/SessionStorage';
import { signInFulfilled } from '../../actions/LoginActions';
import { getClientIds } from '../../actions/ThirdPartyActions';

const Stack = createStackNavigator<RootStackParamList>();

export default function Navigation() {
  useLinkingRedirects();
  const isLogged = useAppSelector((state) => state.login.isLogged);
  const dispatch = useAppDispatch();

  const checkLogin = async () => {
    const token = await getToken();
    if (token && !isLogged) {
      dispatch(signInFulfilled());
      setToken(token);
    }
  };

  useEffect(() => {
    checkLogin();
    dispatch(getClientIds());
  }, []);

  return (
    <NavigationContainer
      linking={LinkingConfiguration}
    >
      <Stack.Navigator
        screenOptions={{
          headerShown: false
        }}
      >
        {isLogged
          ? <Stack.Screen name="Root" component={DrawerNavigation} />
          : <Stack.Screen name="Login" component={LoginScreen} />}
        <Stack.Screen name="NotFound" component={NotFoundScreen} options={{ title: 'Oops!' }} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
