/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import MuiButton from "@material-ui/core/Button";
import clsx from "clsx";
import { withStyles, createStyles } from "@material-ui/core/styles";
import CircularProgress from "@material-ui/core/CircularProgress";

const styles = (theme: any) => createStyles({
  leftIcon: {
    marginRight: theme.spacing(1),
    fontSize: 20
  },
  rightIcon: {
    marginLeft: theme.spacing(1),
    fontSize: 20
  },
  loading: {
    color: "transparent"
  },
  loadingIndicatorContainer: {
    width: "100%",
    height: "100%",
    position: "absolute",
    left: "0",
    top: "0",
    display: "flex",
    alignItems: "center",
    justifyContent: "center"
  }
});

const Button = (props: any) => {
  const {
    classes,
    leftIcon,
    rightIcon,
    text,
    size = "medium",
    variant = "contained",
    color = "default",
    onClick,
    type,
    disabled,
    loading = false,
    className,
    children,
    rootClasses,
    disabledClasses,
    datatype
  } = props;

  return (
    <MuiButton
      datatype={datatype}
      size={size}
      variant={variant}
      color={loading ? "default" : color}
      classes={{
        root: clsx(rootClasses, className),
        disabled: disabledClasses,
        label: loading ? classes.loading : undefined
      }}
      onClick={onClick}
      type={type}
      disabled={disabled || loading}
    >
      {leftIcon && leftIcon(text && classes.leftIcon)}
      {text}
      {loading && (
        <div className={classes.loadingIndicatorContainer}>
          <CircularProgress size={24} thickness={5} className={classes.buttonProgress} />
        </div>
      )}
      {rightIcon && rightIcon(text && classes.rightIcon)}
      {children}
    </MuiButton>
  );
};

export default withStyles(styles)(Button);