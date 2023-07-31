/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ButtonBase, FormControl, FormHelperText, Input, InputLabel } from "@mui/material";
import ClickAwayListener from '@mui/material/ClickAwayListener';
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import Edit from "@mui/icons-material/Edit";
import clsx from "clsx";
import React, { useRef, useState } from "react";
import markdown2html from '@ckeditor/ckeditor5-markdown-gfm/src/markdown2html/markdown2html.js';
import { Field, WrappedFieldProps } from "redux-form";
import {
  HtmlEditor,
  WysiwygEditor,
  addContentMarker,
  CONTENT_MODES,
  getContentMarker,
  getEditorModeLabel,
  removeContentMarker, makeAppStyles,
} from "ish-ui";

const useStyles = makeAppStyles(theme => ({
  hoverIcon: {
    opacity: 0.5,
    visibility: "hidden",
    marginLeft: theme.spacing(1)
  },
  editable: {
    display: "inline-flex",
    cursor: "text",
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
      cursor: "text",
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
    fontSize: "16px",
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

const EditorResolver = ({ contentMode, draftContent, onChange, wysiwygRef }) => {
  switch (contentMode) {
    case "md": {
      return (
        <WysiwygEditor
          value={draftContent}
          onChange={onChange}
          wysiwygRef={wysiwygRef}
        />
      );
    }
    case "textile":
    case "html":
    default: {
      return (
        <HtmlEditor
          value={draftContent}
          onChange={onChange}
          mode={contentMode}
        />
      );
    }
  }
};

interface Props {
  disabled?: boolean;
  fieldClasses?: any;
  label?: string;
  placeholder?: string;
}

const FormEditor: React.FC<Props & WrappedFieldProps> = (
  {
    input: { value, name, onChange },
    meta,
    disabled,
    label,
    placeholder,
    fieldClasses = {}
  }
) => {
  const wysiwygRef = useRef<any>();

  const [contentMode, setContentMode] = useState(getContentMarker(value));
  const [isEditing, setIsEditing] = useState(false);
  const [modeMenu, setModeMenu] = useState(null);
  const classes = useStyles();

  const modeMenuOpen = e => {
    setModeMenu(e.currentTarget);
  };

  const modeMenuClose = () => {
    setModeMenu(null);
  };

  const onEditButtonFocus = () => {
    setIsEditing(true);
  };

  const onClickAway = e => {
    const isBalloon = e.target.closest(".ck-balloon-panel");
    if (isEditing && !isBalloon) {

      const sourceEdit = document.querySelector<HTMLButtonElement>('.ck-source-editing-button');

      if (wysiwygRef.current?.plugins.get("SourceEditing").isSourceEditingMode && sourceEdit) {
        sourceEdit.click();
      }

      setTimeout(() => {
        setIsEditing(false);
      }, 200);
    }
  };

  return (
    <ClickAwayListener
      onClickAway={onClickAway}
      mouseEvent="onMouseDown"
    >
      <FormControl id={name} error={meta && meta.invalid} variant="standard" fullWidth>
        <InputLabel
          shrink
          classes={{
            root: fieldClasses.label
          }}
          htmlFor={`input-${name}`}
        >
          {label}
        </InputLabel>
        {isEditing ? (
          <div
            id="editorRoot"
            className={
              clsx(
                classes.editorArea,
                { "ace-wrapper": contentMode === "html" || contentMode === "textile" },
                label && "mt-2"
              )
            }
          >
            <div className="content-mode-wrapper">
              <Tooltip title="Change content mode" disableFocusListener>
                <ButtonBase
                  onClick={modeMenuOpen}
                  aria-owns={modeMenu ? "mode-menu" : null}
                  className="content-mode"
                >
                  {getEditorModeLabel(contentMode)}
                </ButtonBase>
              </Tooltip>
              <Menu
                id="theme-menu"
                anchorEl={modeMenu}
                open={Boolean(modeMenu)}
                onClose={modeMenuClose}
              >
                {CONTENT_MODES.map(mode => (
                  <MenuItem
                    id={mode.id}
                    key={mode.id}
                    onClick={() => {
                      setContentMode(mode.id);
                      onChange(addContentMarker(removeContentMarker(value), mode.id));
                      modeMenuClose();
                    }}
                    selected={contentMode === mode.id}
                  >
                    {mode.title}
                  </MenuItem>
                ))}
              </Menu>
            </div>
            <EditorResolver
              contentMode={contentMode}
              draftContent={removeContentMarker(value)}
              onChange={v => onChange(addContentMarker(removeContentMarker(v), contentMode))}
              wysiwygRef={wysiwygRef}
            />
          </div>
        ) : (
          <Typography
            variant="body2"
            component="div"
            onClick={onEditButtonFocus}
            className={clsx(classes.editable, {
              [fieldClasses.placeholder ? fieldClasses.placeholder : "placeholderContent"]: !value,
              [fieldClasses.text]: value,
            })}
          >
              <span className={clsx(contentMode === "md" ? classes.previewFrame : "centeredFlex overflow-hidden")}>
                {
                  value
                    ? contentMode === "md"
                      ? <div dangerouslySetInnerHTML={{ __html: markdown2html(removeContentMarker(value)) }}/>
                      : removeContentMarker(value)
                    : placeholder || "No value"
                }
              </span>
            {!disabled
              && <Edit color="primary" className={classes.hoverIcon}/>}
          </Typography>
        )}
        <FormHelperText>
          <span className="shakingError">
            {meta.error}
          </span>
        </FormHelperText>
        <Input
          type="hidden"
          id={`input-${name}`}
          name={name}
          classes={{
            root: "d-none"
          }}
          inputProps={{
            value: removeContentMarker(value)
          }}
        />
      </FormControl>
    </ClickAwayListener>
  );
};

export const FormEditorField = ({ name, label, placeholder }: { name: string, label?: string, placeholder?: string }) => (
  <Field
    name={name}
    label={label}
    placeholder={placeholder}
    component={FormEditor}
  />
);
