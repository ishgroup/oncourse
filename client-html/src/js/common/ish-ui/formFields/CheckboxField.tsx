/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/**
 * Redux Form field checkbox component creator
 * */

import React, { useCallback } from "react";
import Checkbox from "@mui/material/Checkbox";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import { CheckboxFieldProps, FieldInputProps } from "../model/Fields";

const getValue = (value, stringValue) => {
  if (!stringValue) {
    return !!value;
  }

  switch (value) {
    case "false":
      return false;
    case "true":
      return true;
    default:
      return Boolean(value);
  }
};

const styles = theme => ({
  root: {
    width: theme.spacing(4),
    height: theme.spacing(4),
    margin: theme.spacing(0.5)
  }
});

const StyledCheckboxBase = props => {
  const {
   classes, uncheckedClass, className, ...rest
  } = props;

  return (
    <Checkbox
      classes={{
        root: clsx(classes.root, uncheckedClass, className),
      }}
      {...rest}
    />
  );
};

export const StyledCheckbox = withStyles(styles)(StyledCheckboxBase);

export function CheckboxField<IP extends FieldInputProps> ({ input, color, disabled, stringValue, className, uncheckedClass, onChangeHandler, stopPropagation }: CheckboxFieldProps<IP>) {
  const onChange = useCallback(
    (e, value) => {
      let checked = value;

      if (stringValue) {
        checked = checked.toString();
      }
      if (typeof onChangeHandler === "function") {
        onChangeHandler(e, checked);
      }
      if (!e.isDefaultPrevented()) {
        input.onChange(checked);
      }
    },
    [stringValue, input, onChangeHandler]
  );

  return (
    <StyledCheckbox
      checked={getValue(input.value, stringValue)}
      onChange={onChange}
      color={color}
      disabled={disabled}
      className={className}
      uncheckedClass={uncheckedClass}
      onClick={stopPropagation ? e => e.stopPropagation() : undefined}
      id={`input-${input.name}`}
      name={input.name}
    />
  );
};
