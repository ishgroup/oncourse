import createMuiTheme from "@material-ui/core/styles/createMuiTheme";
import {theme} from "./appTheme";

const createOverrides = palette => ({
  overrides: {
    MuiCollapse: {
      entered: {
        height: "auto",
        overflow: "visible",
      },
    },
    MuiFormControlLabel: {
      label: {
        fontSize: "14px",
        "&$disabled": {
          color: "unset",
        },
      },
      disabled: {},
    },
    MuiInputBase: {
      input: {
        ".cms-scope &": {
          font: "inherit",
          color: "currentColor",
          width: "100%",
          border: "0",
          height: "1.1876em",
          margin: "0",
          display: "block",
          padding: "6px 0 7px",
          minWidth: "0",
          background: "none",
          boxSizing: "content-box",
          letterSpacing: "inherit",
          "-webkit-tap-highlight-color": "transparent"
        },
      },
      inputMultiline: {
        lineHeight: "1.5em",
      },
    },
    MuiCssBaseline: {
      "@global": {
        body: {
          fontFeatureSettings:
            '"dlig" 0, "numr" 0, "dnom" 0, "tnum" 0, "case" 0, "zero" 0, "frac" 0, '
            + '"sups" 0, "subs" 0, "cpsp" 0, "salt" 0, "ss01" 0, "ss02" 0, "ss03" 0, '
            + '"cv01", "cv02", "cv03", "cv04", "cv05", "cv06", "cv07", "cv08", "cv09", '
            + '"cv10", "cv11", "calt", "liga", "kern"',
        },
      },
    },
    MuiPickerDTTabs: {
      tabs: {
        color: palette.primary.contrastText,
      },
    },
    PrivateTabIndicator: {
      colorSecondary: {
        backgroundColor: palette.primary.contrastText,
      },
    },
    MuiMenuItem: {
      root: {
        fontWeight: 400,
        "@media (min-width: 600px)": {
          minHeight: "36px",
        },
      },
    },
    MuiPaper: {
      root: {
        padding: "16px",
      },
    },
    MuiTableCell: {
      root: {
        fontWeight: 400,
      },
      head: {
        fontWeight: 600,
        color: palette.text.secondary,
        lineHeight: "1.3rem",
        fontSize: "0.75rem",
        padding: "14px 40px 14px 16px",
      },
    },
    MuiTableSortLabel: {
      root: {
        "&:hover": {
          color: palette.text.primary,
        },
        "&$active": {
          color: palette.text.primary,
        },
      },
      active: {},
      icon: {
        fontSize: "18px",
      },
    },
  },
});

const commonTypography = {
  typography: {
    fontFamily: "Inter, sans-serif",
    fontSize: 14,
    fontWeightLight: 300,
    fontWeightRegular: 400,
    fontWeightMedium: 600,
    body2: {
      fontWeight: 300,
    },
    body1: {
      fontWeight: 300,
    },
    caption: {
      fontWeight: 300,
    },
  },
};

// Default Theme
const defaultThemePalette = {
  primary: {
    main: "#f7941d",
    contrastText: "#fff",
  },
  background: {
    default: "#fbf9f0",
  },
  secondary: {
    main: "#4b6390",
  },
  text: {
    secondary: "rgba(0, 0, 0, 0.54)",
    primary: "rgba(0, 0, 0, 0.87)",
  },
};

export const defaultTheme = createMuiTheme({
  palette: defaultThemePalette,
  ...commonTypography,
  ...theme.default,
  ...createOverrides(defaultThemePalette),
} as any);
