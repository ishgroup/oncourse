/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo, useRef } from "react";
import clsx from "clsx";
import { FormControl, FormHelperText, Input, InputLabel } from "@mui/material";
import { FormControlProps } from "@mui/material/FormControl/FormControl";
import { InputProps } from "@mui/material/Input/Input";
import { makeAppStyles } from "../../../styles/makeStyles";
import { countWidth } from "../../../utils/DOM";

interface Props {
  name: string;
  value: string;
  ref?: React.Ref<any>;
  className?: string;
  label?: React.ReactNode;
  labelAdornment?: React.ReactNode;
  endAdornment?: React.ReactNode;
  FormControlProps?: Partial<FormControlProps>;
  InputProps?: Partial<InputProps>,
  fieldClasses?: {
    label?: string;
    underline?: string;
    text?: string;
  },
  placeholder?: string;
  error?: string;
  rightAligned?: boolean;
  inline?: boolean;
  invalid?: boolean;
  shrink?: boolean;
  disabled?: boolean;
  hideArrows?: boolean;
}

const useStyles = makeAppStyles(theme => ({
  label: {
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    maxWidth: "100%",
    marginRight: theme.spacing(0.5)
  },
  rightLabel: {
    left: "unset",
    right: theme.spacing(-2),
    "& $label": {
      marginRight: 0
    }
  },
  inputWrapper: {
    paddingBottom: 0,
    "& textarea": {
      paddingBottom: "5px"
    },
    "&:hover .d-none": {
      display: "flex",
    },
    "&:hover .invisible": {
      visibility: "visible",
    },
    "&.Mui-focused .MuiInputAdornment-positionEnd": {
      opacity: 1,
    }
  },
  hideArrows: {
    "&::-webkit-outer-spin-button": {
      "-webkit-appearance": "none",
      margin: 0
    },
    "&::-webkit-inner-spin-button": {
      "-webkit-appearance": "none",
      margin: 0
    },
    "-moz-appearance": "textfield"
  },
  inlineInput: {
    padding: 0,
    fontSize: "inherit",
  },
  readonly: {
    fontWeight: 300,
    pointerEvents: "none"
  }
}));

const EditInPlaceFieldBase = (
  {
    ref,
    value,
    invalid,
    className,
    inline,
    FormControlProps,
    InputProps,
    label,
    fieldClasses = {},
    rightAligned,
    shrink,
    name,
    disabled,
    labelAdornment,
    endAdornment,
    error,
    placeholder,
    hideArrows
  }: Props) => {
  
  const classes = useStyles();

  const inputNode = useRef();

  useEffect(() => {
    if (ref && inputNode.current) {
      ref = inputNode.current;
    }
  }, [ref]);
  
  const inputWidth = useMemo(() => inline && inputNode.current
    ? countWidth(value || placeholder, inputNode.current)
    : null, 
  [inputNode.current, inline, value, placeholder]);
  
  return (
    <FormControl
      fullWidth
      error={invalid}
      variant="standard"
      margin="none"
      className={className}
      {...FormControlProps || {}}
    >
    {
      label && (
        <InputLabel
          classes={{
            root: clsx(
              fieldClasses.label,
              "d-flex",
              "overflow-visible",
              rightAligned && classes.rightLabel
            ),
          }}
          shrink={shrink}
          htmlFor={`input-${name}`}
        >
          <span className={classes.label}>
            {label}
          </span>
          {labelAdornment}
        </InputLabel>
      )
    }
    <Input
      {...InputProps || {}}
      inputProps={{
        placeholder,
        ref: inputNode,
        className: clsx(fieldClasses.text, {
          [classes.inlineInput]: inline,
          [classes.readonly]: disabled,
          [classes.hideArrows]: hideArrows,
          "text-end": rightAligned
        }),
        style: {
          width: inline && !invalid ? inputWidth : undefined
        },
        ...InputProps?.inputProps || {}
      }}
      classes={{
        root: clsx(fieldClasses.text, classes.inputWrapper, inline && classes.inlineInput),
        underline: fieldClasses.underline
      }}
      disabled={disabled}
      endAdornment={endAdornment}
      id={`input-${name}`}
      name={name}
    />

    <FormHelperText
      classes={{
        root: clsx(rightAligned && "text-end"),
        error: "shakingError"
      }}
    >
      {error}
    </FormHelperText>
  </FormControl>
  );
};

export default EditInPlaceFieldBase;