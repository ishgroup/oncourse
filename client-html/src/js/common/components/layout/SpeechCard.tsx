import React from "react";
import { createStyles, withStyles } from "@mui/styles";
import Card from "@mui/material/Card";
import clsx from "clsx";

const styles = theme =>
  createStyles({
    wrapper: {
      position: "relative",
      paddingRight: `${theme.spacing(5)}`,
      marginRight: `${theme.spacing(2)}`,
      marginTop: `${theme.spacing(3)}`,
      zIndex: 2
    },
    wrapperLeft: {
      position: "relative",
      paddingLeft: `${theme.spacing(5)}`,
      marginLeft: `${theme.spacing(2)}`,
      marginTop: `${theme.spacing(3)}`,
      zIndex: 2
    },
    contentWrapper: {
      padding: `${theme.spacing(2)}`,
      background: theme.palette.background.default,
      position: "relative",
      overflow: "visible",
      width: "16em"
    },
    triangleInnerLeft: {
      "&:after": {
        content: "''",
        background: theme.palette.background.default,
        top: "15px",
        left: "-20px",
        width: "40px",
        height: "20px",
        position: "absolute",
        transform: "rotate(-16deg) skewX(60deg)"
      }
    },
    triangleInnerRight: {
      "&:after": {
        content: "''",
        background: theme.palette.background.default,
        top: "16px",
        right: "-20px",
        width: "40px",
        height: "20px",
        position: "absolute",
        transform: "rotate(-16deg) skewX(60deg)"
      }
    },
    triangleOuterLeft: {
      position: "absolute",
      top: "15px",
      left: "20px",
      width: "40px",
      height: "20px",
      boxShadow:
        "-12px 2px 8px 0px rgba(0, 0, 0, 0.2), -6px 4px 8px 0px rgba(0, 0, 0, 0.14), -2px 1px 1px 1px rgba(0, 0, 0, 0.12)",
      background: theme.palette.background.default,
      transform: "rotate(-16deg) skewX(60deg)"
    },
    triangleOuterRight: {
      position: "absolute",
      top: "16px",
      right: "20px",
      width: "40px",
      height: "20px",
      boxShadow:
        "-7px 4px 6px 1px rgba(0, 0, 0, 0.2), 0px 1px 16px 4px rgba(0, 0, 0, 0.14), 0px 0px 0px -3px rgba(0, 0, 0, 0.12)",
      background: theme.palette.background.default,
      transform: "rotate(-16deg) skewX(60deg)"
    },
    content: {
      position: "relative",
      zIndex: 1
    }
  });

const SpeechCard = props => {
  const {classes, className, children, leftSide} = props;

  return (
    <div
      className={clsx(className, {
        [classes.wrapper]: !leftSide,
        [classes.wrapperLeft]: leftSide
      })}
    >
      <div
        className={clsx({
          [classes.triangleOuterRight]: !leftSide,
          [classes.triangleOuterLeft]: leftSide
        })}
      />

      <Card
        elevation={10}
        className={clsx(classes.contentWrapper, {
          [classes.triangleInnerRight]: !leftSide,
          [classes.triangleInnerLeft]: leftSide
        })}
      >
        <div className={classes.content}>{children}</div>
      </Card>
    </div>
  );
};

export default withStyles(styles)(SpeechCard);
