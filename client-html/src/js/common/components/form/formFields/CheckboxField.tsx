/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Redux Form field checkbox component creator
 * */

import React, { useCallback } from "react";
import Checkbox from "@mui/material/Checkbox";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import { CheckboxFieldProps } from "../../../../model/common/Fields";

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

export const CheckboxField = ({ input, color, disabled, stringValue, className, uncheckedClass, onChangeHandler, stopPropagation }: CheckboxFieldProps) => {
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
