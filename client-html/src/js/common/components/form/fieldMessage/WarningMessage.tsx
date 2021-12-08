/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Warning from "@mui/icons-material/Warning";
import Typography from "@mui/material/Typography";
import withStyles from "@mui/styles/withStyles";
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
