/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Error from "@material-ui/icons/Error";
import Typography from "@material-ui/core/Typography";
import withStyles from "@material-ui/core/styles/withStyles";
import clsx from "clsx";
import styles from "./styles";

interface Props {
  classes?: any;
  className?: string;
  message: string;
}

const ErrorMessage: React.FC<Props> = ({ classes, className, message }) => (
  <div className={clsx(classes.root, className)}>
    <Error color="error" className={classes.icon} />
    <Typography variant="caption" className="errorColor">
      {message}
    </Typography>
  </div>
  );

export default withStyles(styles)(ErrorMessage);
