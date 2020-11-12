/**
 * Content wrapper component
 **/

import React from "react";
import { withStyles } from "@material-ui/core/styles";

const styles: any = theme => ({
  content: {
    backgroundColor: theme.palette.background.default,
    width: "100%",
    padding: theme.spacing(3),
    height: `calc(100% - ${theme.spacing(8)}px)`,
    marginTop: theme.spacing(8),
    "overflow-y": "auto"
  }
});

const Content = props => {
  const { classes, children } = props;

  return <main className={classes.content}>{children}</main>;
};

export default withStyles(styles)(Content);
