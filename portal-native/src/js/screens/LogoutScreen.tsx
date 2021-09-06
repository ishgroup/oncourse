import React from 'react';
import { Image, View } from 'react-native';
import { Button } from 'react-native-paper';
import { useAppDispatch, useAppSelector } from '../hooks/redux';
import { setIsLogged, signOut } from '../actions/LoginActions';

const LogoutScreen = () => {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.login.user || {});

  const logOut = () => {
    dispatch(signOut());
    dispatch(setIsLogged(false));
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Image
        width={200}
        height={200}
        source={{
          uri: user.profilePicture
        }}
      />
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
