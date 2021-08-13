import React, { useEffect, useState } from 'react';
import { Button, TextInput } from 'react-native-paper';
import { useFormikContext } from 'formik';
import { View } from 'react-native';
import { useCommonStyles } from '../../hooks/styles';
import TextField from '../../components/fields/TextField';
import { LoginValues } from '../../model/Login';
import { useAppSelector } from '../../hooks/redux';
import NewPasswordField from '../../components/fields/NewPasswordField';

const ResetPassword = () => {
  const [hidePasswordConfirm, setHidePasswordConfirm] = useState(true);

  const loading = useAppSelector((state) => state.login.loading);

  const {
    handleSubmit, isValid, values, touched, setFieldTouched, isSubmitting
  } = useFormikContext<LoginValues>();

  const cs = useCommonStyles();

  useEffect(() => {
    if (!touched.confirmPassword && values.password) {
      setFieldTouched('confirmPassword');
    }
  }, [values.password]);

  return (
    <View style={[cs.mtAuto, cs.mbAuto]}>
      <NewPasswordField />
      <TextField
        name="confirmPassword"
        label="Confirm password"
        secureTextEntry={hidePasswordConfirm}
        right={(
          <TextInput.Icon
            onPress={() => setHidePasswordConfirm((pr) => !pr)}
            name={hidePasswordConfirm ? 'eye' : 'eye-off'}
          />
        )}
      />
      <Button
        style={cs.mt3}
        mode="contained"
        dark
        onPress={handleSubmit}
        disabled={!isValid || isSubmitting}
        loading={loading}
      >
        Reset password
      </Button>
    </View>
  );
};

export default React.memo(ResetPassword);
