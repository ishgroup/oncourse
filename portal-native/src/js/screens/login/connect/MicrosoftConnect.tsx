import React from 'react';
import { Image, TouchableOpacity } from 'react-native';
import { useMicrosoftConnect } from '../../../hooks/sso';

export const MicrosoftConnect = ({ onSuccsess, styles, clientId }) => {
  const [request, promptAsync] = useMicrosoftConnect({ onSuccsess, clientId });
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../../../assets/images/microsoft.png')}
      />
    </TouchableOpacity>
  );
};
