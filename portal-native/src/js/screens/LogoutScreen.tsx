import React from 'react';
import { View } from 'react-native';
import { Button } from 'react-native-paper';
import { useAppDispatch } from '../hooks/redux';
import { setIsLogged } from '../actions/LoginActions';
import { removeToken } from '../utils/SessionStorage';

const LogoutScreen = ({}) => {
  const dispatch = useAppDispatch();

  const logOut = () => {
    dispatch(setIsLogged(false));
    removeToken();
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <View style={{ width: 120 }}>
        <Button
          mode="contained"
          dark
          onPress={logOut}
        >
          Log out
        </Button>
      </View>
    </View>
  );
};

export default LogoutScreen;
