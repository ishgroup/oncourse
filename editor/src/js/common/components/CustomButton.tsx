import React from 'react';
import Button from "@material-ui/core/Button";
import {withStyles} from "@material-ui/core/styles";
import {darken, fade} from "@material-ui/core/styles/colorManipulator";
import clsx from "clsx";

const buttonsStyles: any = theme => ({
  submitButton: {
    color: theme.palette.primary.contrastText,
    backgroundColor: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: darken(theme.palette.primary.main, 0.1),
    },
  },
  cancelButton: {
    color: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: "rgba(248, 169, 74, 0.2)",
    },
  },
  deleteButton: {
    color: theme.palette.error.main,
  },
});

const CustomButton = (props: any) => {
  const {children, classes, styles, styleType, ...others} = props;

  const getClass = () => {
    if (styleType === "submit") return classes.submitButton;
    if (styleType === "cancel") return classes.cancelButton;
    if (styleType === "delete") return classes.deleteButton;
  }

  return (
    <Button
      className={clsx(getClass(), styles)}
      {...others}
    >
      {children}
    </Button>
  )
};

export default (withStyles(buttonsStyles)(CustomButton));