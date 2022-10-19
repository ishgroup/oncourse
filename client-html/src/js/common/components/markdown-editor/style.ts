/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { makeStyles } from "@mui/styles";
import { AppTheme } from "../../../model/common/Theme";

export const useStyles = makeStyles((theme: AppTheme) => ({
  hoverIcon: {
    opacity: 0.5,
    visibility: "hidden",
    marginLeft: theme.spacing(1)
  },
  editable: {
    display: "inline-flex",
    color: theme.palette.text.primaryEditable,
    minHeight: "32px",
    padding: "4px 0 4px",
    marginTop: theme.spacing(2),
    fontWeight: 400,
    justifyContent: "space-between",
    alignItems: "flex-end",
    "&:hover $hoverIcon": {
      visibility: "visible"
    },
    "&:before": {
      borderBottom: '1px solid transparent',
      left: 0,
      bottom: "4px",
      content: "' '",
      position: "absolute",
      right: 0,
      transition: theme.transitions.create("border-bottom-color", {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
      pointerEvents: "none"
    },
    "&:hover:before": {
      borderBottom: `1px solid ${theme.palette.primary.main}`
    },
  },
  editorArea: {
    "&#editorRoot": {
      "& .ck-dropdown__button .ck-button__label": {
        fontSize: "14px"
      },
      cursor: "pointer",
      overflowY: "auto",
      position: "relative",
      "& .ck-list__item .ck-button": {
        height: "unset",
        padding: "5px 10px"
      },
      "& .ck-list__item .ck-heading_heading3": {
        fontSize: "15px"
      },
      "& .ck-list__item .ck-heading_heading4": {
        fontSize: "14px"
      },
      "& .ck-toolbar": {
        height: "45px",
        background: theme.table.contrastRow.main
      },
      "& .ck-list__item": {
        fontSize: "15px"
      },
      "& .ck-dropdown": {
        fontSize: "15px"
      },
      "& .ck.ck-dropdown .ck-button__label": {
        fontFamily: "Inter, sans-serif",
        overflow: "visible",
        width: "120px"
      },
      "& .content-mode-wrapper": {
        position: "absolute",
        right: "10px",
        top: "6px",
        padding: "5px",
        zIndex: 1000,
        "& .content-mode": {
          maxWidth: "85px",
          border: 0,
          boxShadow: "none",
          backgroundColor: "black",
          color: "white",
          padding: "2px",
          fontSize: "9px",
          "&:hover": {
            backgroundColor: theme.palette.primary.main,
            color: theme.palette.primary.contrastText,
          },
        }
      },
      "& .ck-editor": {
        "& .ck-content": {
          resize: "vertical",
          maxHeight: "80vh",
          minHeight: "200px",
          fontFamily: "Inter, sans-serif",
          fontWeight: 400,
          fontSize: '14px',
          color: "black"
        },
        "& .ck-content .table": {
          marginLeft: 0
        },
        "& .ck-toolbar": {
          padding: "3px"
        },
        "& .ck-source-editing-button": {
          marginLeft: "auto",
          marginRight: "44px"
        },
        "& .ck-source-editing-button > *": {
          visibility: 'hidden'
        },
        "& .ck-source-editing-button .ck_source_edit_custom": {
          visibility: 'visible'
        },
        "& .ck-source-editing-button .ck_code_icon_custom": {
          width: "24px"
        },
      },
      "&.ace-wrapper": {
        border: `1px solid ${theme.palette.divider}`,
        paddingTop: "44px",
        background: theme.table.contrastRow.main,
        "& .ace_editor": {
          borderTop: `1px solid ${theme.palette.divider}`
        }
      }
    }
  },
  previewFrame: {
    width: "100%",
    maxHeight: "300px",
    overflow: "auto",
    fontSize: "14px",
    "& > div": {
      width: "100%"
    },
    "& h1,h2,h3,h4": {
      all: "revert"
    },
    "& table": {
      marginLeft: 0,
      marginRight: 'auto',
      borderCollapse: "collapse",
      borderSpacing: 0,
      border: '1px double #b3b3b3',
      "& th": {
        textAlign: 'left',
        fontWeight: 700,
        background: 'hsla(0,0%,0%,5%)',
        minWidth: '2em',
        padding: '0.4em',
        border: '1px solid #bfbfbf'
      },
      "& td": {
        minWidth: '2em',
        padding: '0.4em',
        border: '1px solid #bfbfbf'
      },
    }
  },
  readonly: {
    fontWeight: 300,
    pointerEvents: "none"
  },
  textField: {
    paddingLeft: "0",
    // @ts-ignore
    paddingBottom: `${theme.spacing(2) - 3}`,
    margin: 0
  },
  "@global": {
    ".ck.ck-balloon-panel.ck-balloon-panel_visible": {
      zIndex: 2000
    }
  }
}));
