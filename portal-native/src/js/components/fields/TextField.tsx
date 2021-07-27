// react-native-paper TextInput wrapper for Formik implementation

import React, { useEffect } from 'react';
import _get from 'lodash.get';
import { TextInputProps } from 'react-native-paper/src/components/TextInput/TextInput';
import { useFormikContext } from 'formik';
import { View } from 'react-native';
import { HelperText, TextInput } from 'react-native-paper';

interface Props extends Partial<TextInputProps> {
  name: string;
}

const TextField = (props: Props) => {
  const {
    label,
    name,
    ...rest
  } = props;

  const {
    values, errors, touched, handleChange, setFieldTouched, isSubmitting
  } = useFormikContext();

  const value = _get(values, name);
  const isTouched = _get(touched, name);
  const errorMessage = _get(errors, name);

  useEffect(() => {
    if (!isTouched && (value || isSubmitting)) {
      setFieldTouched(name, true);
    }
  }, [value, isTouched, isSubmitting, name]);

  const hasError = Boolean(isTouched && errorMessage);

  return (
    <View>
      <TextInput
        error={hasError}
        label={label}
        value={value}
        onChangeText={handleChange(name)}
        {...rest}
      />
      <HelperText type="error" style={{ height: 22 }}>
        {hasError && errorMessage}
      </HelperText>
    </View>
  );
};

export default TextField;
