import { createTheme } from '@mui/material/styles';
import { theme } from "./appTheme";
import {
  AppTheme,
  ChristmasThemeKey,
  DarkThemeKey,
  DefaultThemeKey,
  HighcontrastThemeKey,
  MonochromeThemeKey,
  ThemeValues
} from "../../model/common/Theme";
import { Components } from "@mui/material/styles/components";

const createOverrides = (palette):{ components: Components } => ({
  components: {
    MuiButtonBase: {
      styleOverrides: {
        root: {
          userSelect: "auto",
        }
      }
    },
    MuiCollapse: {
      styleOverrides: {
        entered: {
          height: "auto",
          overflow: "visible"
        }
      }
    },
    MuiFormControl: {
      styleOverrides: {
        root: {
          maxWidth: "100%",
        }
      }
    },
    MuiFormControlLabel: {
      styleOverrides: {
        label: {
          fontSize: "14px",
          "&$disabled": {
            color: "unset"
          }
        },
        disabled: {}
      }
    },
    MuiInput: {
      styleOverrides: {
        underline: {
          "&:before": {
            borderBottom: `1px solid transparent`,
          },
          "&:hover:not(.Mui-disabled):not(.primaryContarstUnderline):before": {
            borderBottom: `1px solid ${palette.primary.main}`
          },
          "&.primaryContarstUnderline:hover:not(.Mui-disabled):before": {
            borderBottom: `1px solid ${palette.primary.contrastText}`
          }
        }
      }
    },
    MuiSelect: {
      styleOverrides: {
        select: {
          "&:focus": {
            backgroundColor: "none",
          },
        },
      }
    },
    MuiFormLabel: {
      styleOverrides: {
        root: {
          lineHeight: 1.2,
          whiteSpace: 'nowrap'
        },
      }
    },
    MuiInputBase: {
      styleOverrides: {
        root: {
          maxWidth: "100%",
          width: "100%",
          MuiInput: {
            root: {
              "&:before": {
                borderBottom: `1px solid transparent`,
              },
              "&:hover:not(.Mui-disabled):not(.primaryContarstUnderline):before": {
                borderBottom: `1px solid ${palette.primary.main}`
              },
              "&.primaryContarstUnderline:hover:not(.Mui-disabled):before": {
                borderBottom: `1px solid ${palette.primary.contrastText}`
              }
            },
          },
        },
        input: {
          "&::placeholder": {
            color: palette.text.disabled,
            fill: palette.text.disabled,
          },
          textOverflow: "ellipsis",
          color: palette.text.primaryEditable,
          fontWeight: 400,
          "&:hover:not(.Mui-disabled):not(&:focus):not(.primaryContarstHover)": {
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
      }
    },
    MuiCssBaseline: {
      styleOverrides: {
        "@global": {
          body: {
            fontFeatureSettings:
              '"dlig" 0, "numr" 0, "dnom" 0, "tnum" 0, "case" 0, "zero" 0, "frac" 0, '
              + '"sups" 0, "subs" 0, "cpsp" 0, "salt" 0, "ss01" 0, "ss02" 0, "ss03" 0, '
              + '"cv01", "cv02", "cv03", "cv04", "cv05", "cv06", "cv07", "cv08", "cv09", '
              + '"cv10", "cv11", "calt", "liga", "kern"'
          }
        }
      }
    },
    // MuiPickerDTTabs: {
    //   styleOverrides: {
    //     tabs: {
    //       color: palette.primary.contrastText
    //     }
    //   }
    // },
    // PrivateTabIndicator: {
    //   styleOverrides: {
    //     colorSecondary: {
    //       backgroundColor: palette.primary.contrastText
    //     }
    //   }
    // },
    MuiMenuItem: {
      styleOverrides: {
        root: {
          fontWeight: 400,
          "@media (min-width: 600px)": {
            minHeight: "36px"
          }
        }
      }
    },
    MuiTableSortLabel: {
      styleOverrides: {
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
    },
    MuiAutocomplete: {
      styleOverrides: {
        inputRoot: {
          flexWrap: 'inherit',
        }
      }
    },
    MuiFormHelperText: {
      styleOverrides: {
        root: {
          marginLeft: 0
        }
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
    disabled: "rgba(34, 34, 34, 0.38)",
    hint: "rgba(34, 34, 34, 0.38)"
  }
};

export const defaultTheme = createTheme({
  palette: defaultThemePalette,
  ...commonTypography,
  ...theme.default,
  ...createOverrides(defaultThemePalette)
}) as AppTheme;

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

export const darkTheme = createTheme({
  palette: darkThemePalette,
  ...commonTypography,
  ...theme.dark,
  ...createOverrides(darkThemePalette)
}) as AppTheme;

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

export const monochromeTheme = createTheme({
  palette: monochromeThemePalette,
  ...commonTypography,
  ...theme.monochrome,
  ...createOverrides(monochromeThemePalette)
}) as AppTheme;

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

export const highcontrastTheme = createTheme({
  palette: highcontrastThemePalette,
  ...commonTypography,
  ...theme.highcontrast,
  ...createOverrides(highcontrastThemePalette)
}) as AppTheme;

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

export const christmasTheme = createTheme({
  palette: christmasThemePalette,
  ...commonTypography,
  ...theme.christmas,
  ...createOverrides(christmasThemePalette)
}) as AppTheme;

export const currentTheme = (themeName: ThemeValues): AppTheme => {
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

export const getTheme = (): AppTheme => {
  let actualTheme = defaultTheme;

  try {
    const storageThemeName = localStorage.getItem('theme') as ThemeValues;
    if (storageThemeName) {
      actualTheme = currentTheme(storageThemeName);
    }
  } catch (e) {
    console.error(e);
    return actualTheme;
  }

  return actualTheme;
};

export const useTheme = () => ({
  ...getTheme()
});