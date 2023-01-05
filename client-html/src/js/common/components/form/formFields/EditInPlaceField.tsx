/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/**
 * Wrapper component for Material Select and Text Field with edit in plaxce functional
 * */

import React from "react";
import clsx from "clsx";
import { Edit } from "@mui/icons-material";
import EditInPlaceFieldBase from "./EditInPlaceFieldBase";
import { makeAppStyles } from "../../../styles/makeStyles";
import { EditInPlaceFieldProps } from "../../../../model/common/Fields";

const useStyles = makeAppStyles(theme => ({
  rightAligned: {
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-end"
  },
  inlineContainer: {
    display: "inline-block",
    marginLeft: theme.spacing(0.5)
  },
  inlineMargin: {
    marginRight: theme.spacing(0.5)
  },
  editIcon: {
    fontSize: "24px",
    color: theme.palette.divider,
    verticalAlign: "middle",
  },
  endAdornment: {
    opacity: 0.5
  }
}));

interface InputTypes {
  type?: "password" | "number" | "text"
}

const EditInPlaceField = (
  {
    inputRef,
    input,
    label,
    maxLength,
    disabled,
    min,
    max,
    step,
    className,
    onKeyPress,
    labelAdornment,
    truncateLines,
    rightAligned,
    defaultValue,
    onKeyDown,
    multiline,
    inline,
    warning,
    preformatDisplayValue,
    type = "text",
    meta: { error, invalid },
    placeholder = "No value",
    fieldClasses = {},
    InputProps = {}
  }: EditInPlaceFieldProps & InputTypes) => {

  const classes = useStyles();
  
  const onBlur = () => {
    input?.onBlur && input.onBlur(input.value);
  };

  return (
    <div
      id={input.name}
      className={clsx(className, "outline-none", {
        [classes.inlineMargin]: inline,
        [classes.rightAligned]: rightAligned,
        [classes.inlineContainer]: inline
      })}
    >
      <EditInPlaceFieldBase
        ref={inputRef}
        name={input.name}
        value={input.value || defaultValue}
        error={error}
        invalid={invalid}
        inline={inline}
        label={label}
        warning={warning}
        fieldClasses={fieldClasses}
        endAdornmentClass={classes.endAdornment}
        rightAligned={rightAligned}
        shrink={Boolean(label || input.value)}
        disabled={disabled}
        labelAdornment={labelAdornment}
        placeholder={placeholder}
        hideArrows={["percentage", "number"].includes(type)}
        editIcon={<Edit fontSize="inherit" />}
        InputProps={{
          multiline,
          onKeyPress,
          onBlur,
          onFocus: input.onFocus,
          maxRows: truncateLines,
          inputProps: {
            maxLength,
            step,
            min,
            max,
            onKeyDown,
            type,
          },
          value: preformatDisplayValue ? preformatDisplayValue(input.value || defaultValue) : input.value || defaultValue,
          onChange: v => (type === "number" && max && Number(v) > Number(max) ? null : input.onChange(v)),
          ...InputProps
        }}
      />
    </div>
  );
};

export default EditInPlaceField;