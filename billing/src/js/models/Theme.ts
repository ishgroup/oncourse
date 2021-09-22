import { Theme } from '@mui/material';
import { SimplePaletteColorOptions } from '@mui/material/styles/createPalette';

export type StringKeyObject<V> = { [key: string]: V };

enum ThemeValuesEnum {
  'default',
  'dark',
  'monochrome',
  'highcontrast',
  'christmas'
}

export type ThemeValues = keyof typeof ThemeValuesEnum;

export const DefaultThemeKey: ThemeValues = 'default';

export const DarkThemeKey: ThemeValues = 'dark';
export const MonochromeThemeKey: ThemeValues = 'monochrome';
export const HighcontrastThemeKey: ThemeValues = 'highcontrast';
export const ChristmasThemeKey: ThemeValues = 'christmas';

export interface AppTheme extends Theme {
  heading: StringKeyObject<string>;
  blog: StringKeyObject<StringKeyObject<string>>;
  share: StringKeyObject<StringKeyObject<string>>;
  statistics: StringKeyObject<StringKeyObject<string>>;
  appBarButton: StringKeyObject<StringKeyObject<string>>;
  tabList: StringKeyObject<StringKeyObject<string>>;
  table: StringKeyObject<SimplePaletteColorOptions>;
}
