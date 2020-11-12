/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Warning from "@material-ui/icons/Warning";
import Typography from "@material-ui/core/Typography";
import withStyles from "@material-ui/core/styles/withStyles";
import clsx from "clsx";
import styles from "./styles";

interface Props {
  classes?: any;
  className?: string;
  warning: string;
}

const WarningMessage: React.FC<Props> = ({ classes, className, warning }) => (
  <div className={clsx(classes.root, className)}>
    <Warning className={clsx(classes.icon, "warningColor")} />
    <Typography variant="caption" className="warningColor">
      {warning}
    </Typography>
  </div>
);

export default withStyles(styles)(WarningMessage);
