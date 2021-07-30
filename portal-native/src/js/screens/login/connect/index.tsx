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

  const dispatch = useAppDispatch();

  const onConnectSuccsess = (sSOToken, sSOProvider: SSOproviders) => {
    dispatch(signIn({
      sSOProvider,
      sSOToken,
      verificationUrl
    }));

    if (verificationUrl) {
      dispatch(setLoginUrl(null));
    }
  };

  return (
    <View style={[cs.flexRow, cs.justifyContentCenter]}>
      <GoogleConnect
        styles={styles}
        onSuccsess={(auth) => onConnectSuccsess(auth, 'Google')}
      />
      <MicrosoftConnect
        styles={styles}
        onSuccsess={(auth) => onConnectSuccsess(auth, 'Microsoft')}
      />
      <FacebookConnect
        styles={styles}
        onSuccsess={(auth) => onConnectSuccsess(auth, 'Facebook')}
      />
    </View>
  );
};
