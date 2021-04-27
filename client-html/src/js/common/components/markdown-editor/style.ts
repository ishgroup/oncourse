import { makeStyles } from "@material-ui/core/styles";
import { AppTheme } from "../../../model/common/Theme";

export const useStyles = makeStyles((theme: AppTheme) => ({
  editorArea: {
    "&#editorRoot": {
      "& textarea.mde-text, .mde-tabs button": {
        fontFamily: "Inter, sans-serif",
        fontWeight: 400,
        fontSize: '14px',
        outline: "none"
      },
      "& .ck-dropdown__button .ck-button__label": {
        fontSize: "14px"
      },
      cursor: "pointer",
      overflow: "auto",
      position: "relative",
      "& .mde-textarea-wrapper, .mde-preview": {
        "& ol": {
          display: "block",
          listStyleType: "decimal",
          marginTop: "1em",
          marginBottom: "1em",
          marginLeft: "0",
          marginRight: "0",
          paddingLeft: "40px",
        },
        "& ul": {
          display: "block",
          listStyleType: "disc",
          marginTop: "1em",
          marginBottom: "1em",
          marginLeft: "0",
          marginRight: "0",
          paddingLeft: "40px",
        },
        "& h2": {
          display: "block",
          fontSize: "1.5em",
          marginTop: "0.83em",
          marginBottom: "0.83em",
          marginLeft: "0",
          marginRight: "0",
          fontWeight: "bold"
        },
        "& h3": {
          display: "block",
          fontSize: "1.17em",
          marginTop: "1em",
          marginBottom: "1em",
          marginLeft: "0",
          marginRight: "0",
          fontWeight: "bold"
        },
        "& h4": {
          display: "block",
          fontSize: "1em",
          marginTop: "1.33em",
          marginBottom: "1.33em",
          marginLeft: "0",
          marginRight: "0",
          fontWeight: "bold"
        }
      },
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
      "& .ck.ck-button.ck-dropdown__button": {
        width: "140px"
      },
      "& .mde-header": {
        background: theme.table.contrastRow.main,
        height: "45px",
        paddingLeft: "115px",
        minHeight: "45px",
        "& .mde-tabs": {
          position: "absolute",
          height: "45px",
          zIndex: 2,
          left: "0px"
        },
        "& .ck.ck-icon.ck-dropdown__arrow": {
          width: "11px",
          marginRight: "12px",
          marginBottom: "0.5px"
        },
        "& .ck-icon": {
          width: "19px"
        },
        "& ul.mde-header-group": {
          padding: "8px"
        },
        "& #mdeHeadersDropdown": {
          top: "-6px",
          "& .ck-dropdown__button": {
            outline: "none"
          },
          "& .paragraph": {
            marginTop: "1px"
          }
        }
      },
      "& .mde-preview": {
        "& .ck.ck-button.ck-dropdown__button": {
          marginRight: "12px",
          marginTop: "2px",
          width: "140px"
        },
        "& .ck.ck-button": {
          width: "31px"
        },
        "& .ck-list .ck-button.ck-button_with-text": {
          width: "100%"
        },
        "& .ck.ck-content.ck-editor__editable.ck-rounded-corners.ck-focused": {
          borderColor: "rgb(196, 196, 196)",
          boxShadow: "none"
        },
        "& .ck-list": {
          padding: 0,
          margin: 0
        },
        "& .mde-preview-content": {
          position: "relative",
          top: 0,
          padding: 0,
          marginTop: "-46px",
          left: "-1px",
          width: "calc(100% + 2px)",
          "& h1, h2, h3, h4, h5": {
            border: 0,
            all: "revert"
          }
        },
      },
      "& .content-mode-wrapper": {
        position: "absolute",
        right: "10px",
        top: "8px",
        padding: "5px",
        zIndex: "1000",
        "& .content-mode": {
          maxWidth: "85px",
          border: 0,
          boxShadow: "none",
          backgroundColor: "black",
          color: "white",
          padding: "2px",
          fontSize: "9px"
        }
      },
      "& .ck-editor": {
        "& .ck-toolbar__items": {
          marginLeft: "122px"
        },
        "& .ck-content": {
          resize: "vertical",
          maxHeight: "80vh",
          minHeight: "200px",
          borderBottom: 0,
          fontFamily: "Inter, sans-serif",
          fontWeight: 400,
          fontSize: '14px',
          color: "black"
        },
        "& .ck-toolbar": {
          padding: "3px"
        }
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
  editable: {
    "&:hover, &:hover .placeholderContent": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main
    }
  },
  previewFrame: {
    maxHeight: "300px",
    overflow: "auto",
    fontSize: "inherit",
    "& h1,h2,h3,h4": {
      all: "revert"
    }
  },
  readonly: {
    pointerEvents: "none"
  },
  textField: {
    paddingLeft: "0",
    paddingBottom: `${theme.spacing(2) - 3}px`,
    margin: 0
  },
  "@global": {
    ".ck.ck-balloon-panel": {
      zIndex: 2000
    }
  }
}));
