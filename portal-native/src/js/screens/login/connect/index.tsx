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
      {thirdParty?.Google && (
        <GoogleConnect
          styles={styles}
          onSuccsess={(auth, verifier) => onConnectSuccsess(auth, 'Google', verifier)}
          {...thirdParty?.Google}
        />
      )}
      {thirdParty?.Microsoft && (
        <MicrosoftConnect
          styles={styles}
          onSuccsess={(auth, verifier) => onConnectSuccsess(auth, 'Microsoft', verifier)}
          {...thirdParty?.Microsoft}
        />
      )}
      {thirdParty?.Facebook && (
        <FacebookConnect
          styles={styles}
          onSuccsess={(auth, verifier) => onConnectSuccsess(auth, 'Facebook', verifier)}
          {...thirdParty?.Facebook}
        />
      )}
    </View>
  );
};
