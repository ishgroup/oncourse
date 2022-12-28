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

import React, { useRef } from "react";
import clsx from "clsx";
import AttachmentIcon from '@mui/icons-material/Attachment';
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import EditInPlaceFieldBase from "./EditInPlaceFieldBase";
import { makeAppStyles } from "../../../styles/makeStyles";
import { FieldClasses } from "../../../../model/common/Fields";
import InputAdornment from "@mui/material/InputAdornment";

interface Props {
  ref?: any;
  input?: Partial<WrappedFieldInputProps>;
  meta?: Partial<WrappedFieldMetaProps>;
  label?: string,
  disabled?: boolean,
  className?: string,
  onKeyPress?: any,
  placeholder?: string,
  warning?: string,
  labelAdornment?: any,
  preformatDisplayValue?: any,
  fieldClasses?: FieldClasses,
  rightAligned?: boolean,
  disableInputOffsets?: boolean,
  onKeyDown?: any,
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
    label,
    disabled,
    className,
    onKeyPress,
    placeholder = "No value",
    labelAdornment,
    fieldClasses = {},
    rightAligned,
    onKeyDown,
    inline,
    warning
  }: Props) => {

  const classes = useStyles();
  
  const inputRef = useRef<HTMLInputElement>();
  const fileRef = useRef<HTMLInputElement>();

  const handleFileSelect = e => {
    if (e.target.files[0]) {
      input.onChange(e.target.files[0]);
      fileRef.current.value = null;
      inputRef.current.blur();
    }
  };
  
  const onFocus = () => {
    fileRef.current.click();
    setTimeout(() => {
      inputRef.current.blur();
    }, 200);
  };

  const InputProps = {
    onKeyPress,
    startAdornment: <InputAdornment position="start"><AttachmentIcon /></InputAdornment>,
    onFocus,
    inputProps: {
      onKeyDown,
      ref: inputRef
    },
    value: input.value ? input.value.name : "",
  };

  return (
    <div
      id={input.name}
      className={clsx(className, "outline-none", {
        [classes.inlineContainer]: inline
      })}
    >
      <input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />
      <div
        className={clsx({
          [classes.inlineMargin]: inline,
          [classes.rightAligned]: rightAligned
        })}
      >
        <EditInPlaceFieldBase
          ref={ref}
          name={input.name}
          value={input.value ? input.value.name : ""}
          error={error}
          invalid={invalid}
          inline={inline}
          label={label}
          warning={warning}
          fieldClasses={fieldClasses}
          endAdornmentClass={classes.endAdornment}
          rightAligned={rightAligned}
          shrink={Boolean(label || input.value)}
          labelAdornment={labelAdornment}
          placeholder={placeholder}
          InputProps={InputProps}
          disabled={disabled}
        />
      </div>
    </div>
  );
};

export default EditInPlaceField;