import React, { useEffect, useState } from 'react';
import { Button, TextInput } from 'react-native-paper';
import { useFormikContext } from 'formik';
import { useCommonStyles } from '../../hooks/styles';
import TextField from '../../components/fields/TextField';
import { LoginValues } from '../../model/Login';
import { useAppSelector } from '../../hooks/redux';

const ResetPassword = () => {
  const [hidePassword, setHidePassword] = useState(true);
  const [hidePasswordConfirm, setHidePasswordConfirm] = useState(true);

  const loading = useAppSelector((state) => state.login.loading);

  const {
    handleSubmit, isValid, values, touched, setFieldTouched
  } = useFormikContext<LoginValues>();

  const cs = useCommonStyles();

  useEffect(() => {
    if (!touched.confirmPassword && values.password) {
      setFieldTouched('confirmPassword');
    }
  }, [values.password]);


  return (
    <>
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
      <TextField
        name="confirmPassword"
        label="Confirm password"
        style={cs.bgTransp}
        secureTextEntry={hidePasswordConfirm}
        right={(
          <TextInput.Icon
            onPress={() => setHidePasswordConfirm((pr) => !pr)}
            name={hidePasswordConfirm ? 'eye' : 'eye-off'}
          />
        )}
      />
      <Button
        mode="contained"
        dark
        onPress={handleSubmit}
        disabled={!isValid}
        loading={loading}
      >
        Create account
      </Button>
    </>
  );
};

export default React.memo(ResetPassword);
