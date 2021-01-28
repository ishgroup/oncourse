import { Theme } from "@material-ui/core";
import { SimplePaletteColorOptions } from "@material-ui/core/styles/createPalette";

export type StringKeyObject<V> = { [key: string]: V };

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

export interface AppTheme extends Theme {
  heading: StringKeyObject<ColorGetter>;
  blog: StringKeyObject<StringKeyObject<ColorGetter>>;
  share: StringKeyObject<StringKeyObject<ColorGetter>>;
  statistics: StringKeyObject<StringKeyObject<ColorGetter>>;
  appBarButton: StringKeyObject<StringKeyObject<ColorGetter>>;
  tabList: StringKeyObject<StringKeyObject<ColorGetter>>;
  table: StringKeyObject<SimplePaletteColorOptions>;
}
