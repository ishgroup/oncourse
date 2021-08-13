import React, { useState, useMemo } from 'react';
import { HelperText, TextInput } from 'react-native-paper';
import debounce from 'lodash.debounce';
import TextField from './TextField';
import { useThemeContext } from '../../styles';
import LoginService from '../../services/LoginService';

interface Props {
  name?: string;
  label?: string;
}

const NewPasswordField = (
  {
    name = 'password',
    label = 'Password'
  }:Props
) => {
  const [complexityMessage, setComplexityMessage] = useState(null);
  const [complexity, setComplexity] = useState(null);
  const [hidePassword, setHidePassword] = useState(true);
  const theme = useThemeContext();

  const complexityColor = useMemo(() => {
    if (typeof complexity === 'number') {
      if (complexity < 2) {
        return theme.colors.error;
      }
      if (complexity > 3) {
        return 'green';
      }
    }
    return theme.colors.primary;
  }, [complexity]);

  const appliedTheme = useMemo(() => {
    if (complexityColor) {
      const updatedTheme = {
        ...theme,
        colors: {
          ...theme.colors
        }
      };
      updatedTheme.colors.primary = complexityColor;
      return updatedTheme;
    }
    return null;
  }, [theme, complexityColor, complexity]);

  const helperText = useMemo(() => {
    if (complexityMessage) {
      return (
        <HelperText type="info" style={{ color: complexityColor }}>
          {complexityMessage}
        </HelperText>
      );
    }
    return null;
  }, [complexityMessage, complexityColor]);

  const validate = async (value, resolve) => {
    try {
      const response = await LoginService.checkPassword(value);
      if (response) {
        setComplexityMessage(response.feedback);
        setComplexity(response.score);
        resolve(response.score > 1 ? undefined : response.feedback);
      }
      resolve(undefined);
    } catch (error) {
      resolve(undefined);
    }
  };

  const debouncedValidate = useMemo(() => debounce(validate, 300), []);

  return (
    <TextField
      name={name}
      label={label}
      secureTextEntry={hidePassword}
      theme={appliedTheme}
      helperText={helperText}
      right={(
        <TextInput.Icon
          onPress={() => setHidePassword((pr) => !pr)}
          name={hidePassword ? 'eye' : 'eye-off'}
        />
      )}
      validate={(value) => new Promise((resolve) => debouncedValidate(value, resolve))}
    />
  );
};

export default NewPasswordField;
