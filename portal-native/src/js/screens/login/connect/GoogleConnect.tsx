import React from 'react';
import { Image, TouchableOpacity } from 'react-native';
import { useGoogleConnect } from '../../../hooks/sso';

export const GoogleConnect = ({ onSuccsess, styles }) => {
  const [request, promptAsync] = useGoogleConnect({ onSuccsess });
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../../../assets/images/google-color.png')}
      />
    </TouchableOpacity>
  );
};
