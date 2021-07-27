import React, { useState } from 'react';
import { useFormikContext } from 'formik';
import { Image, TouchableOpacity, View } from 'react-native';
import { Button, Caption, TextInput } from 'react-native-paper';
import { useCommonStyles } from '../../hooks/styles';
import { useAppDispatch, useAppSelector } from '../../hooks/redux';
import TextField from '../../components/fields/TextField';
import useGoogleConnect from '../../hooks/useGoogleConnect';
import useFacebookConnect from '../../hooks/useFacebookConnect';
import useMicrosoftConnect from '../../hooks/useMicrosoftConnect';
import { useStyles } from './styles';
import { LoginValues } from '../../model/Login';

const GoogleConnect = ({ onSuccsess }) => {
  const [request, promptAsync] = useGoogleConnect({ onSuccsess });
  const styles = useStyles();
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../../assets/images/google-color.png')}
      />
    </TouchableOpacity>
  );
};

const FacebookConnect = ({ onSuccsess }) => {
  const [request, promptAsync] = useFacebookConnect({ onSuccsess });
  const styles = useStyles();
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../../assets/images/facebook-square-color.png')}
      />
    </TouchableOpacity>
  );
};

const MicrosoftConnect = ({ onSuccsess }) => {
  const [request, promptAsync] = useMicrosoftConnect({ onSuccsess });
  const styles = useStyles();
  return (
    <TouchableOpacity disabled={!request} onPress={() => promptAsync()}>
      <Image
        style={styles.socialNetworkImage}
        source={require('../../../assets/images/microsoft.png')}
      />
    </TouchableOpacity>
  );
};

const LoginContent = () => {
  const [hidePassword, setHidePassword] = useState(true);
  const loading = useAppSelector((state) => state.login.loading);
  const cs = useCommonStyles();
  const dispatch = useAppDispatch();

  const {
    handleSubmit, isValid, setFieldValue
  } = useFormikContext<LoginValues>();

  const onConnectSuccsess = (authentication) => {
    // dispatch(connect(authentication));
  };

  const onLoginClick = () => {
    setFieldValue('submitBy', 'SignIn');
    // Timeout to give values time to update
    setTimeout(() => {
      handleSubmit();
    });
  };

  const onLoginEmailClick = () => {
    setFieldValue('submitBy', 'LoginEmail');
    // Timeout to give values time to update
    setTimeout(() => {
      handleSubmit();
    });
  };

  return (
    <View style={[cs.mtAuto, cs.mbAuto]}>
      <TextField
        name="email"
        label="Email"
        style={cs.bgTransp}
      />
      <TextField
        name="password"
        label="Password"
        style={cs.bgTransp}
        secureTextEntry={hidePassword}
        right={(
          <TextInput.Icon
            onPress={() => setHidePassword((pr) => !pr)}
            name={hidePassword ? 'eye' : 'eye-off'}
          />
        )}
      />
      <Button
        style={cs.mt3}
        mode="contained"
        dark
        onPress={onLoginClick}
        disabled={!isValid}
        loading={loading}
      >
        Log in
      </Button>
      <View style={[cs.flexCenter, cs.mt1, cs.mb1]}>
        <Caption>
          Or
        </Caption>
      </View>
      <Button
        mode="contained"
        dark
        onPress={onLoginEmailClick}
        disabled={!isValid}
        loading={loading}
      >
        Email me login link
      </Button>
      <View style={[cs.flexCenter, cs.mt2, cs.mb1]}>
        <Caption>
          Or connect using
        </Caption>
      </View>
      <View style={[cs.flexRow, cs.justifyContentCenter]}>
        <GoogleConnect
          onSuccsess={onConnectSuccsess}
        />
        <MicrosoftConnect
          onSuccsess={onConnectSuccsess}
        />
        <FacebookConnect
          onSuccsess={onConnectSuccsess}
        />
      </View>
    </View>
  );
};

export default React.memo(LoginContent);
