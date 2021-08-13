import React, { useContext } from 'react';
import { DefaultTheme } from 'react-native-paper';
import { Theme } from 'react-native-paper/src/types';
import { SPACING_UNIT } from '../constants/Layout';

export const spacing = (unit: number) => unit * SPACING_UNIT;

export type IshTheme = {
  colors: {
    secondary: string;
  } & Theme['colors'],
  spacing: typeof spacing;
} & Theme;

export const LightTheme: IshTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: '#fbb619',
    secondary: '#37caad',
    background: '#fff',
  },
  spacing
};

export const DarkTheme: IshTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: '#333',
    secondary: '#272c34',
  },
  spacing
};

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
