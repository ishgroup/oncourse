import { Theme } from "@mui/material";
import { Palette, SimplePaletteColorOptions, TypeText } from '@mui/material/styles/createPalette';
import { StringKeyObject } from "./CommomObjects";

enum ThemeValuesEnum {
  "default",
  "dark",
  "monochrome",
  "highcontrast",
  "christmas"
}

export type ThemeValues = keyof typeof ThemeValuesEnum;

export const DefaultThemeKey: ThemeValues = "default";

export const DarkThemeKey: ThemeValues = "dark";
export const MonochromeThemeKey: ThemeValues = "monochrome";
export const HighcontrastThemeKey: ThemeValues = "highcontrast";
export const ChristmasThemeKey: ThemeValues = "christmas";

type ColorGetter = string | ((props: {}) => string);

type TextExtended = TypeText & { primaryEditable: string; hint: string; };

export interface AppTheme extends Theme {
  heading: StringKeyObject<ColorGetter>;
  blog: StringKeyObject<StringKeyObject<ColorGetter>>;
  share: StringKeyObject<StringKeyObject<ColorGetter>>;
  statistics: StringKeyObject<StringKeyObject<ColorGetter>>;
  appBar: StringKeyObject<StringKeyObject<ColorGetter>>;
  appBarButton: StringKeyObject<StringKeyObject<ColorGetter>>;
  tabList: StringKeyObject<StringKeyObject<ColorGetter>>;
  table: StringKeyObject<SimplePaletteColorOptions>;
  palette: Palette & { text: TextExtended };
}
