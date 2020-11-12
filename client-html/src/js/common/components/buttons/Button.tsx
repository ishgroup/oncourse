/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Wrapper component for Material UI button
 * */

import React from "react";
import MuiButton from "@material-ui/core/Button";
import clsx from "clsx";
import { withStyles, createStyles } from "@material-ui/core/styles";
import CircularProgress from "@material-ui/core/CircularProgress";

const styles = theme => createStyles({
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

const Button = props => {
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
