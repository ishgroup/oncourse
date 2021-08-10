import React, { useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { StyleSheet, View } from 'react-native';
import { ActivityIndicator } from 'react-native-paper';
import LinkingConfiguration from '../../constants/LinkingConfiguration';
import NotFoundScreen from '../../screens/NotFoundScreen';
import DrawerNavigation from './DrawerNavigation';
import LoginScreen from '../../screens/login/LoginScreen';
import { useAppDispatch, useAppSelector } from '../../hooks/redux';
import { useLinkingRedirects } from '../../hooks/linking';
import { getToken, setToken } from '../../utils/SessionStorageUtils';
import { setIsLogged } from '../../actions/LoginActions';
import { getClientIds } from '../../actions/ThirdPartyActions';
import { RootStackParamList } from '../../model/Navigation';
import ClassRollScreen from '../../screens/class/ClassRollScreen';
import { getUserSessions } from '../../actions/SessionActions';

const style = StyleSheet.create({
  loader: {
    flex: 1,
    justifyContent: 'center'
  }
});

const Stack = createStackNavigator<RootStackParamList>();

export default function Navigation() {
  useLinkingRedirects();
  const isLogged = useAppSelector((state) => state.login.isLogged);
  const dispatch = useAppDispatch();

  const [loginChecked, setloginChecked] = useState(false);

  const checkLogin = async () => {
    const token = await getToken();
    if (token && !isLogged) {
      dispatch(setIsLogged(true));
      return setToken(token);
    }
  };

  useEffect(() => {
    dispatch(getClientIds());
    checkLogin().then(() => {
      setloginChecked(true);
    });
  }, []);

  useEffect(() => {
    if (isLogged) {
      dispatch(getUserSessions());
    }
  }, [isLogged]);

  return loginChecked ? (
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
        <Stack.Screen name="ClassRoll" component={ClassRollScreen} options={{ presentation: 'modal' }} />
      </Stack.Navigator>
    </NavigationContainer>
  ) : <View style={style.loader}><ActivityIndicator size="large" /></View>;
}
