import React, { useContext } from 'react';
import { DefaultTheme } from 'react-native-paper';
import { SPACING_UNIT } from '../constants/Layout';

const spacing = (unit: number) => unit * SPACING_UNIT;

export const LightTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: '#fbb619',
    secondary: '#37caad',
  },
  spacing
};

export const DarkTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: '#333',
    secondary: '#272c34',
  },
  spacing
};

export type IshTheme = typeof LightTheme;

export type ThemeType = 'light' | 'dark';

export const ThemeContext = React.createContext(LightTheme);

export const useThemeContext = () => useContext<IshTheme>(ThemeContext);

export const getThemeByType = (type: ThemeType) => {
  switch (type) {
    default:
    case 'light':
      return LightTheme;
    case 'dark':
      return DarkTheme;
  }
};
