/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import {
 FormControl, FormHelperText, Input, InputLabel 
} from "@mui/material";
import ButtonBase from "@mui/material/ButtonBase";
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
import HtmlEditor from "./HtmlEditor";
import { useStyles } from "./style";
import {
 addContentMarker, CONTENT_MODES, getContentMarker, getEditorModeLabel, removeContentMarker 
} from "./utils";
import WysiwygEditor from "./WysiwygEditor";

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
              variant="body1"
              component="div"
              onClick={onEditButtonFocus}
              className={clsx( classes.editable, {
                [fieldClasses.placeholder ? fieldClasses.placeholder : "placeholderContent"]: !value,
                [fieldClasses.text]: value,
              })}
            >
              <span className={clsx(contentMode === "md" ? classes.previewFrame : "centeredFlex overflow-hidden")}>
                {
                  value
                    ? contentMode === "md"
                      ? <div dangerouslySetInnerHTML={{ __html: markdown2html(removeContentMarker(value)) }} />
                      : removeContentMarker(value)
                    : placeholder || "No value"
                }
              </span>
              {!disabled
              && <Edit color="primary" className={classes.hoverIcon} />}
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
