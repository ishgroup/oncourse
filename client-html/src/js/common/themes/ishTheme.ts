import createMuiTheme from "@material-ui/core/styles/createMuiTheme";
import { theme } from "./appTheme";
import {
  ChristmasThemeKey,
  DarkThemeKey,
  DefaultThemeKey,
  HighcontrastThemeKey,
  MonochromeThemeKey
} from "../../model/common/Theme";
import { LSGetItem } from "../utils/storage";
import { APPLICATION_THEME_STORAGE_NAME } from "../../constants/Config";

const createOverrides = palette => ({
  overrides: {
    MuiButtonBase: {
      root: {
        userSelect: "auto",
      }
    },
    MuiCollapse: {
      entered: {
        height: "auto",
        overflow: "visible"
      }
    },
    MuiFormControl: {
      root: {
        maxWidth: "100%",
      },
    },
    MuiFormControlLabel: {
      label: {
        fontSize: "14px",
        "&$disabled": {
          color: "unset"
        }
      },
      disabled: {}
    },
    MuiInput: {
      underline: {
        borderBottom: "2px solid transparent",
        "&:before": {
          borderBottom: "2px solid transparent!important",
        },
        "&:hover:not($disabled)": {
          borderBottom: `1px solid ${palette.primary.main}`,
          marginBottom: "1px",
        },
      },
    },
    MuiSelect: {
      select: {
        "&:focus": {
          backgroundColor: "none",
        },
      },
    },
    MuiInputBase: {
      root: {
        maxWidth: "100%",
        width: "100%",
      },
      input: {
        textOverflow: "ellipsis",
        color: palette.text.primaryEditable,
        fontWeight: 400,
        "&:hover:not($disabled):not(&:focus)": {
          color: palette.primary.main,
          MuiSelect: {
            icon: {
              color: palette.primary.main,
            },
          },
        },
      },
      inputMultiline: {
        lineHeight: "1.5em"
      },
    },
    MuiCssBaseline: {
      "@global": {
        body: {
          fontFeatureSettings:
            '"dlig" 0, "numr" 0, "dnom" 0, "tnum" 0, "case" 0, "zero" 0, "frac" 0, '
            + '"sups" 0, "subs" 0, "cpsp" 0, "salt" 0, "ss01" 0, "ss02" 0, "ss03" 0, '
            + '"cv01", "cv02", "cv03", "cv04", "cv05", "cv06", "cv07", "cv08", "cv09", '
            + '"cv10", "cv11", "calt", "liga", "kern"'
        }
      }
    },
    MuiPickerDTTabs: {
      tabs: {
        color: palette.primary.contrastText
      }
    },
    PrivateTabIndicator: {
      colorSecondary: {
        backgroundColor: palette.primary.contrastText
      }
    },
    MuiMenuItem: {
      root: {
        fontWeight: 400,
        "@media (min-width: 600px)": {
          minHeight: "36px"
        }
      }
    },
    MuiTableCell: {
      root: {
        fontWeight: 400
      },
      head: {
        fontWeight: 600,
        color: palette.text.secondary,
        lineHeight: "1.3rem",
        fontSize: "0.75rem",
        padding: "14px 40px 14px 16px"
      }
    },
    MuiTableSortLabel: {
      root: {
        "&:hover": {
          color: palette.text.primary
        },
        "&$active": {
          color: palette.text.primary
        }
      },
      active: {},
      icon: {
        fontSize: "18px"
      }
    }
  }
});

const commonTypography = {
  typography: {
    fontFamily: "Inter, sans-serif",
    fontSize: 14,
    fontWeightLight: 300,
    fontWeightRegular: 400,
    fontWeightMedium: 600,
    body2: {
      fontWeight: 300
    },
    body1: {
      fontWeight: 300
    },
    caption: {
      fontWeight: 300
    }
  }
};

// Default Theme

const defaultThemePalette = {
  primary: {
    main: "#f7941d",
    contrastText: "#fff"
  },
  background: {
    default: "#fbf9f0"
  },
  secondary: {
    main: "#4b6390"
  },
  text: {
    secondary: "rgba(0, 0, 0, 0.54)",
    primary: "rgba(0, 0, 0, 0.87)",
    primaryEditable: "rgba(0, 0, 0, 0.95)",
  }
};

export const defaultTheme = createMuiTheme({
  palette: defaultThemePalette,
  ...commonTypography,
  ...theme.default,
  ...createOverrides(defaultThemePalette)
} as any);

// Dark Theme

const darkThemePalette = {
  type: "dark",
  common: { black: "#000", white: "#fff" },
  primary: {
    main: "#f7941d",
    dark: "rgba(255, 143, 0, 1)",
    contrastText: "rgba(255, 255, 255, 1)"
  },
  background: {
    default: "#1b1b1b",
    paper: "#313131"
  },
  secondary: {
    light: "rgba(75, 99, 144, 1)",
    main: "#9bbeff",
    dark: "rgba(75, 99, 144, 1)",
    contrastText: "#fff"
  },
  error: {
 light: "#e57373", main: "#f44336", dark: "#d32f2f", contrastText: "#fff"
},
  text: {
    primary: "rgba(255, 255, 255, 0.87)",
    primaryEditable: "rgba(255, 255, 255, 0.95)",
    secondary: "rgba(255, 255, 255, 0.65)",
    disabled: "rgba(255, 255, 255, 0.38)",
    hint: "rgba(255, 255, 255, 0.38)"
  }
};

export const darkTheme = createMuiTheme({
  palette: darkThemePalette,
  ...commonTypography,
  ...theme.dark,
  ...createOverrides(darkThemePalette)
} as any);

// Monochrome Theme

const monochromeThemePalette = {
  type: "light",
  common: { black: "#000", white: "#fff" },
  primary: {
    main: "#eaeaea",
    dark: "#76838f",
    contrastText: "#222222"
  },
  secondary: {
    light: "#9bbeff",
    main: "#3e8ef7",
    dark: "#255ea9",
    contrastText: "#fff"
  },
  error: {
    light: "#e57373", main: "#f44336", dark: "#d32f2f", contrastText: "#fff"
  },
  text: {
    primary: "#222222",
    primaryEditable: "#181818",
    secondary: "rgba(34, 34, 34, 0.54)",
    disabled: "rgba(34, 34, 34, 0.38)",
    hint: "rgba(34, 34, 34, 0.38)"
  }
};

export const monochromeTheme = createMuiTheme({
  palette: monochromeThemePalette,
  ...commonTypography,
  ...theme.monochrome,
  ...createOverrides(monochromeThemePalette)
} as any);

// High Contrast Theme

const highcontrastThemePalette = {
  common: { black: "#111111", white: "#fff" },
  primary: {
    main: "#111111",
    dark: "#76838f",
    contrastText: "#fff"
  },
  secondary: {
    light: "#9bbeff",
    main: "#111111",
    dark: "#111111",
    contrastText: "#fff"
  },
  background: {
    default: "#f2f2f2"
  },
  error: {
    light: "#e57373", main: "#f44336", dark: "#d32f2f", contrastText: "#fff"
  },
  text: {
    primary: "#111111",
    primaryEditable: "#070707",
    secondary: "#111111",
    disabled: "rgba(34, 34, 34, 0.38)",
    hint: "rgba(34, 34, 34, 0.38)"
  },
  action: {
    selected: "rgba(0, 0, 0, 0.3)"
  },
  divider: "rgba(0, 0, 0, 0.40)"
};

export const highcontrastTheme = createMuiTheme({
  palette: highcontrastThemePalette,
  ...commonTypography,
  ...theme.highcontrast,
  ...createOverrides(highcontrastThemePalette)
} as any);

// High Contrast Theme

const christmasThemePalette = {
  type: "light",
  common: { black: "#111111", white: "#fff" },
  primary: {
    main: "#B3000C",
    dark: "#76838f",
    contrastText: "#fff"
  },
  secondary: {
    light: "#9bbeff",
    main: "#165B33",
    dark: "#111111",
    contrastText: "#fff"
  },
  background: {
    default: "#fff"
  },
  error: {
    light: "#e57373", main: "#f44336", dark: "#d32f2f", contrastText: "#fff"
  },
  text: {
    primary: "#111111",
    primaryEditable: "#070707",
    secondary: "#111111",
    disabled: "rgba(34, 34, 34, 0.38)",
    hint: "rgba(34, 34, 34, 0.38)"
  },
  action: {
    disabled: "rgba(255, 255, 255, 0.26)"
  }
};

export const christmasTheme = createMuiTheme({
  palette: christmasThemePalette,
  ...commonTypography,
  ...theme.christmas,
  ...createOverrides(christmasThemePalette)
} as any);

export const currentTheme = themeName => {
  switch (themeName) {
    case DarkThemeKey: {
      return darkTheme;
    }
    case DefaultThemeKey: {
      return defaultTheme;
    }
    case MonochromeThemeKey: {
      return monochromeTheme;
    }
    case HighcontrastThemeKey: {
      return highcontrastTheme;
    }
    case ChristmasThemeKey: {
      return christmasTheme;
    }
    default: {
      return defaultTheme;
    }
  }
};

export const getTheme = theme => {
  let actualTheme = theme;

  const storageThemeName = LSGetItem(APPLICATION_THEME_STORAGE_NAME);
  if (storageThemeName) {
    actualTheme = currentTheme(storageThemeName);
  }

  return actualTheme;
};
