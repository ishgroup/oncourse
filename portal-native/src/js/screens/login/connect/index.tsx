import { View } from 'react-native';
import React from 'react';
import { SSOproviders } from '@api/model';
import { GoogleConnect } from './GoogleConnect';
import { MicrosoftConnect } from './MicrosoftConnect';
import { FacebookConnect } from './FacebookConnect';
import { useCommonStyles } from '../../../hooks/styles';
import { useStyles } from '../styles';
import { useAppDispatch, useAppSelector } from '../../../hooks/redux';
import { setLoginUrl, signIn } from '../../../actions/LoginActions';

export const Connect = () => {
  const cs = useCommonStyles();
  const styles = useStyles();
  const verificationUrl = useAppSelector((state) => state.login.verificationUrl);
  const thirdParty = useAppSelector((state) => state.thirdParty);

  const dispatch = useAppDispatch();

  const onConnectSuccsess = (ssOToken, ssOProvider: SSOproviders, codeVerifier: string) => {
    dispatch(signIn({
      ssOProvider,
      ssOToken,
      verificationUrl,
      codeVerifier
    }));

    if (verificationUrl) {
      dispatch(setLoginUrl(null));
    }
  };

  return (
    <View style={[cs.flexRow, cs.justifyContentCenter]}>
      {thirdParty?.Google?.clientId && (
        <GoogleConnect
          styles={styles}
          clientId={thirdParty.Google.clientId}
          onSuccsess={(auth, verifier) => onConnectSuccsess(auth, 'Google', verifier)}
        />
      )}
      {thirdParty?.Microsoft?.clientId && (
        <MicrosoftConnect
          styles={styles}
          clientId={thirdParty.Microsoft.clientId}
          onSuccsess={(auth, verifier) => onConnectSuccsess(auth, 'Microsoft', verifier)}
        />
      )}
      {thirdParty?.Facebook?.clientId && (
        <FacebookConnect
          styles={styles}
          clientId={thirdParty.Facebook.clientId}
          onSuccsess={(auth, verifier) => onConnectSuccsess(auth, 'Facebook', verifier)}
        />
      )}
    </View>
  );
};
