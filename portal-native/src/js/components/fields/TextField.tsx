// react-native-paper TextInput wrapper for Formik implementation

import React, { useEffect, useMemo, useState } from 'react';
import debounce from 'lodash.debounce';
import { TextInputProps } from 'react-native-paper/src/components/TextInput/TextInput';
import { useField } from 'formik';
import { StyleSheet, View } from 'react-native';
import { HelperText, TextInput } from 'react-native-paper';
import { AnyArgFunction } from '../../model/CommonFunctions';

const styles = StyleSheet.create({
  errorText: {
    height: 22
  },
  borders: {
    borderColor: 'rgba(0,0,0,24)'
  }
});

interface Props extends Partial<TextInputProps> {
  name: string;
  // TODO Change with native implementation when formik 3.0.0 will be released
  format?: AnyArgFunction;
  parse?: AnyArgFunction;
}

const TextField = (props: Props) => {
  const {
    label,
    name,
    format,
    parse,
    ...rest
  } = props;

  const [field, meta, helpers] = useField(name);
  const [textValue, setTextValue] = useState(field.value);
  const [isMounted, setIsMounted] = useState(false);

  const eventHandler = (text) => {
    helpers.setValue(text);
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
    if (!meta.touched && field.value) {
      helpers.setTouched(true);
    }
  }, [field.value, meta.touched, name]);

  const hasError = Boolean(meta.touched && meta.error);

  const displayedValue = useMemo(() => (format ? format(textValue) : textValue), [textValue, format]);

  const onChangeText = (val) => setTextValue(parse ? parse(val) : val);

  return (
    <View>
      <TextInput
        error={hasError}
        label={label}
        value={displayedValue}
        onChangeText={onChangeText}
        style={styles.borders}
        {...rest}
      />
      <HelperText type="error" style={styles.errorText}>
        {hasError && meta.error}
      </HelperText>
    </View>
  );
};

export default React.memo(TextField);
