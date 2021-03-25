import {Theme} from "@material-ui/core";
import {SimplePaletteColorOptions} from "@material-ui/core/styles/createPalette";

type StringKeyObject<V> = { [key: string]: V };
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