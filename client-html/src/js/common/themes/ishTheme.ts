/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { createTheme } from '@mui/material/styles';
import { Components } from "@mui/material/styles/components";
import { PaletteMode } from "@mui/material";
import { theme } from "./appTheme";
import {
  AppTheme,
  DarkThemeKey,
  DefaultThemeKey,
  HighcontrastThemeKey,
  MonochromeThemeKey,
  ThemeValues
} from "../../model/common/Theme";

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
    MuiInputLabel: {
      styleOverrides: {
        shrink: {
          "& .inputAdornmentIcon": {
            transform: 'scale(1.25)'
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
          '&.Mui-disabled': {
            fontWeight: 300,
            WebkitTextFillColor: 'inherit'
          },
          "&::placeholder": {
            opacity: 0.15
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
        }
      }
    },
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          fontFeatureSettings:
            '"dlig" 0, "numr" 0, "dnom" 0, "tnum" 0, "case" 0, "zero" 0, "frac" 0, '
            + '"sups" 0, "subs" 0, "cpsp" 0, "salt" 0, "ss01" 0, "ss02" 0, "ss03" 0, '
            + '"cv01", "cv02", "cv03", "cv04", "cv05", "cv06", "cv07", "cv08", "cv09", '
            + '"cv10", "cv11", "calt", "liga", "kern"',
          fontSize: "0.875rem"
        }
      }
    },
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
    hint: "rgba(34, 34, 34, 0.38)",
    grey: "#434343",
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
  mode: "dark" as PaletteMode,
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
    hint: "rgba(255, 255, 255, 0.38)",
    grey: "rgba(255, 255, 255, 0.65)",
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
  mode: "light" as PaletteMode,
  common: { black: "#000", white: "#fff" },
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
  error: {
    light: "#e57373", main: "#f44336", dark: "#d32f2f", contrastText: "#fff"
  },
  text: {
    primary: "#222222",
    primaryEditable: "#181818",
    secondary: "rgba(34, 34, 34, 0.54)",
    disabled: "rgba(34, 34, 34, 0.38)",
    hint: "rgba(34, 34, 34, 0.38)",
    grey: "#434343",
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
    hint: "rgba(34, 34, 34, 0.38)",
    grey: "#434343",
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

export const useAppTheme = (): AppTheme => ({
  ...getTheme()
});