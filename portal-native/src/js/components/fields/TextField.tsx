// react-native-paper TextInput wrapper for Formik implementation

import React, { useEffect, useMemo, useState } from 'react';
import _get from 'lodash.get';
import debounce from 'lodash.debounce';
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

  const [textValue, setTextValue] = useState(null);
  const [isMounted, setIsMounted] = useState(false);

  const {
    values, errors, touched, setFieldValue, setFieldTouched, isSubmitting
  } = useFormikContext();

  const value = _get(values, name);
  const isTouched = _get(touched, name);
  const errorMessage = _get(errors, name);

  const eventHandler = (text) => {
    setFieldValue(name, text);
  };

  const debouncedEventHandler = useMemo(
    () => debounce(eventHandler, 300),
    []
  );

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    if (isMounted) {
      debouncedEventHandler(textValue);
    }
  }, [textValue]);

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
        value={textValue}
        onChangeText={setTextValue}
        {...rest}
      />
      <HelperText type="error" style={{ height: 22 }}>
        {hasError && errorMessage}
      </HelperText>
    </View>
  );
};

export default React.memo(TextField);
