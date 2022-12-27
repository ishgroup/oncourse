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
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import EditInPlaceFieldBase from "./EditInPlaceFieldBase";
import { makeAppStyles } from "../../../styles/makeStyles";
import { FieldClasses } from "../../../../model/common/Fields";

interface Props {
  ref?: any;
  input?: Partial<WrappedFieldInputProps>;
  meta?: Partial<WrappedFieldMetaProps>;
  type?: "password" | "percentage" | "number",
  label?: string,
  maxLength?: number,
  disabled?: boolean,
  min?: number,
  max?: number,
  step?: string,
  className?: string,
  onKeyPress?: any,
  placeholder?: string,
  warning?: string,
  labelAdornment?: any,
  preformatDisplayValue?: any,
  truncateLines?: number,
  fieldClasses?: FieldClasses,
  rightAligned?: boolean,
  disableInputOffsets?: boolean,
  onKeyDown?: any,
  multiline?: boolean,
  inline?: boolean,
}

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

const EditInPlaceField = (
  {
    ref,
    input,
    meta: { error, invalid },
    type,
    label,
    maxLength,
    disabled,
    min,
    max,
    step,
    className,
    onKeyPress,
    placeholder = "No value",
    labelAdornment,
    truncateLines,
    fieldClasses = {},
    rightAligned,
    onKeyDown,
    multiline,
    inline,
    warning
  }: Props) => {

  const classes = useStyles();

  const InputProps = {
    multiline,
    onKeyPress,
    onBlur: input.onBlur,
    onFocus: input.onFocus,
    maxRows: truncateLines,
    inputProps: {
      maxLength,
      step,
      min,
      max,
      onKeyDown,
      type: type !== "password" ? (type === "percentage" ? "number" : type) : undefined,
    },
    value: input.value,
    onChange: v => (type === "number" && max && Number(v) > Number(max) ? null : input.onChange(v))
  };

  return (
    <div
      id={input.name}
      className={clsx(className, "outline-none", {
        [classes.inlineContainer]: inline
      })}
    >
      <div
        className={clsx({
          [classes.inlineMargin]: inline,
          [classes.rightAligned]: rightAligned
        })}
      >
        <EditInPlaceFieldBase
          ref={ref}
          name={input.name}
          value={input.value}
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
          InputProps={InputProps}
        />
      </div>
    </div>
  );
};

export default EditInPlaceField;