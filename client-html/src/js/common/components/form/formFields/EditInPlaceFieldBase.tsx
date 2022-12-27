/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo, useState } from "react";
import clsx from "clsx";
import { FormControl, FormHelperText, Input, InputAdornment, InputLabel } from "@mui/material";
import { FormControlProps } from "@mui/material/FormControl/FormControl";
import { InputProps } from "@mui/material/Input/Input";
import { makeAppStyles } from "../../../styles/makeStyles";
import { countWidth } from "../../../utils/DOM";
import { FieldClasses } from "../../../../model/common/Fields";
import WarningMessage from "../fieldMessage/WarningMessage";

interface Props {
  name: string;
  value: string;
  ref?: any;
  className?: string;
  endAdornmentClass?: string;
  label?: React.ReactNode;
  labelAdornment?: React.ReactNode;
  editIcon?: React.ReactNode;
  FormControlProps?: Partial<FormControlProps>;
  InputProps?: Partial<InputProps>,
  fieldClasses?: FieldClasses,
  placeholder?: string;
  warning?: string;
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
    "&.Mui-focused $inputEndAdornment": {
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
  },
  inputEndAdornment: {
    display: "flex",
    fontSize: "24px",
    color: theme.palette.primary.main,
    alignItems: "flex-end",
    alignSelf: "flex-end",
    marginBottom: "5px"
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
    editIcon,
    error,
    placeholder,
    hideArrows,
    warning,
    endAdornmentClass
  }: Props) => {
  
  const classes = useStyles();

  const [inputNode, setInputNode] = useState<HTMLInputElement>();

  useEffect(() => {
    if (ref && inputNode) {
      ref = inputNode;
    }
  }, [ref, inputNode]);
  
  const onAdornmentClick = () => {
    inputNode.focus();
  };
  
  const inputWidth = useMemo(() => inline && inputNode
    ? countWidth(value || placeholder, inputNode)
    : null,
  [inputNode, inline, value, placeholder]);

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
      inputRef={setInputNode}
      inputProps={{
        placeholder,
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
      endAdornment={!disabled &&
        <InputAdornment
          position="end"
          onClick={onAdornmentClick}
          className={clsx(classes.inputEndAdornment, endAdornmentClass, {
            ["fsInherit"]: inline,
            ["d-none"]: (rightAligned || inline),
            ["invisible"]: !(rightAligned || inline)
          })}
        >
          {editIcon}
        </InputAdornment>
      }
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
      {warning && <WarningMessage warning={warning} />}
    </FormHelperText>
  </FormControl>
  );
};

export default EditInPlaceFieldBase;