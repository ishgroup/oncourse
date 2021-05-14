/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import {CircularProgress} from "@material-ui/core";
import {createStyles, makeStyles} from "@material-ui/core/styles";
import {AppTheme} from "../../models/Theme";

export const useStyles = makeStyles((theme: AppTheme) =>
  createStyles({
    root: {
      alignSelf: "center",
      marginLeft: "auto",
      marginRight: "auto"
    },
  }),
);

const LoadingIndicator: React.FC<any> = () => {
  const classes = useStyles();

  return <div className={classes.root}>
    <CircularProgress  size={40} thickness={5} />
  </div>
}

export default LoadingIndicator;
