import React, { useState } from 'react';
import { useFormikContext } from 'formik';
import { View } from 'react-native';
import { Button, Caption, TextInput } from 'react-native-paper';
import { useCommonStyles } from '../../hooks/styles';
import { useAppSelector } from '../../hooks/redux';
import TextField from '../../components/fields/TextField';
import { LoginValues } from '../../model/Login';
import { Connect } from './connect';

const LoginContent = () => {
  const [hidePassword, setHidePassword] = useState(true);
  const loading = useAppSelector((state) => state.login.loading);
  const cs = useCommonStyles();

  const {
    handleSubmit, isValid, setFieldValue, values
  } = useFormikContext<LoginValues>();

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
        loading={values.submitBy === 'SignIn' && loading}
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
        loading={values.submitBy === 'LoginEmail' && loading}
      >
        Email me login link
      </Button>
      <View style={[cs.flexCenter, cs.mt2, cs.mb1]}>
        <Caption>
          Or connect using
        </Caption>
      </View>
      <Connect />
    </View>
  );
};

export default React.memo(LoginContent);
