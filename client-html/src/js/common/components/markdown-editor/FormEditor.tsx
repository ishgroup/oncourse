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
 FormControl, FormHelperText, InputLabel
} from "@mui/material";
import ButtonBase from "@mui/material/ButtonBase";
import ClickAwayListener from '@mui/material/ClickAwayListener';
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import Edit from "@mui/icons-material/Edit";
import clsx from "clsx";
import React, { useState } from "react";
import ReactMarkdown from "react-markdown";
import { Field, WrappedFieldProps } from "redux-form";
import HtmlEditor from "./HtmlEditor";
import MarkdownEditor from "./MarkdownEditor";
import { useStyles } from "./style";
import {
  addContentMarker, CONTENT_MODES, getContentMarker, getEditorModeLabel, removeContentMarker
} from "./utils";

const EditorResolver = ({ contentMode, draftContent, onChange }) => {
  switch (contentMode) {
    case "md": {
      return (
        <MarkdownEditor
          value={draftContent}
          onChange={onChange}
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
  hideLabel?: boolean;
  fieldClasses?: any;
  label?: string;
}

const FormEditor: React.FC<Props & WrappedFieldProps> = (
  {
    input: { value, name, onChange },
    meta,
    disabled,
    label,
    fieldClasses = {}
  }
) => {
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
    onChange(removeContentMarker(value));
    setIsEditing(true);
  };

  const onClickAway = e => {
    const isBalloon = e.target.closest(".ck-balloon-panel");
    if (isEditing && !isBalloon) {
      if (value.trim()) {
        onChange(addContentMarker(value, contentMode));
      }
      setIsEditing(false);
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
              draftContent={value}
              onChange={onChange}
            />
          </div>
          ) : (
            <Typography
              variant="body1"
              component="div"
              onClick={onEditButtonFocus}
              className={clsx( classes.editable, {
                [classes.previewFrame]: contentMode === "md",
                [fieldClasses.placeholder ? fieldClasses.placeholder : "placeholderContent"]: !value,
                [fieldClasses.text]: value,
              })}
            >
              <span className="centeredFlex overflow-hidden">
                {
                  value
                    ? contentMode === "md"
                      ? <ReactMarkdown source={removeContentMarker(value)} />
                      : removeContentMarker(value)
                    : "No value"
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
      </FormControl>
    </ClickAwayListener>
  );
};

export const FormEditorField = ({ name, label }) => (
  <Field
    name={name}
    label={label}
    component={FormEditor}
  />
);
