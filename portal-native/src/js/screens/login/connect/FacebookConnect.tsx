import React from 'react';
import { Image, TouchableOpacity } from 'react-native';
import { useFacebookConnect } from '../../../hooks/sso';

export const FacebookConnect = ({
  onSuccsess, styles, webClientId, iosClientId, androidClientId
}) => {
  const [request, promptAsync] = useFacebookConnect({
    onSuccsess, webClientId, iosClientId, androidClientId
  });
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../../../assets/images/facebook-square-color.png')}
      />
    </TouchableOpacity>
  );
};
