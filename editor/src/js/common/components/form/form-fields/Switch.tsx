/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Wrapper component for Material UI button
 * */

import React, {useCallback} from "react";
import MuiSwitch from "@material-ui/core/Switch";
import Typography from "@material-ui/core/Typography";
import {withStyles, createStyles} from "@material-ui/core/styles";
import clsx from "clsx";

const getValue = value => {
  switch (value) {
    case "false":
      return false;
    case "true":
      return true;
    default:
      return value;
  }
};

const styles = theme => createStyles({
  bar: {
      height: "22px",
      borderRadius: "12px",
      marginTop: "2px",
      marginLeft: "-7px",
      width: "38px",
    },
  checked: {
      "&$disabled + $bar ": {
        opacity: 0.12,
      },
    },
  icon: {
      boxShadow: theme.shadows[1],
      backgroundColor: theme.palette.common.white,
    },
  root: {
      alignItems: "center",
      "&$disabled": {
        opacity: 0.12,
      },
    },
  inline: {
      marginLeft: "-12px",
    },
  default: {
      top: theme.spacing(0.5),
      height: theme.spacing(4),
      width: theme.spacing(4),
      padding: 0,
      "&$disabled": {
        color: theme.palette.common.white,
      },
      "&$checked": {
        transform: `translateX(${theme.spacing(2)}px)`,
      },
      "&$checked + $bar": {
        opacity: 1,
      },
      "&$checked$disabled + $bar": {
        backgroundColor: theme.palette.primary.main,
        opacity: 0.6,
      },
    },
  focusVisible: {
      background: "red",
    },
  disabled: {},
});

const SwitchBase = props => {
  const {
    classes, color = "primary", inline, checked, ...custom
  } = props;

  return (
    <MuiSwitch
      color={color}
      checked={Boolean(checked)}
      classes={{
        track: classes.bar,
        root: clsx(classes.root, {[classes.inline]: inline}),
        checked: classes.checked,
        thumb: classes.icon,
        switchBase: classes.default,
        disabled: classes.disabled,
      }}
      {...custom}
    />
  );
};

export const Switch = withStyles(styles)(SwitchBase);

export const FormSwitch = props => {
  const {
 input, color, disabled, stringValue, label, className, inline, onChangeHandler,
} = props;

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
    [stringValue, input, onChangeHandler],
  );

  return (
    <>
      {label && (
        <Typography variant="caption" color="textSecondary">
          {label}
        </Typography>
      )}
      <Switch
        checked={getValue(input.value)}
        onChange={onChange}
        color={color}
        disabled={disabled}
        className={className}
        inline={inline}
      />
    </>
  );
};
