/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { Warning } from '@mui/icons-material';
import { Typography } from '@mui/material';
import { makeAppStyles } from '../../styles/makeStyles';

const useStyles = makeAppStyles()((theme) => ({
  icon: {
    fontSize: theme.spacing(2),
    display: 'inline-block',
    verticalAlign: 'middle',
    marginRight: theme.spacing(0.5),
  },
  warningColor: {
    color: theme.palette.warning.main
  }
}));

interface Props {
  className?: string;
  warning: string;
}

const WarningMessage = ({ className, warning }: Props) => {
  const { classes, cx } = useStyles();

  return (
    <pre className={className}>
      <Warning className={cx(classes.icon, classes.warningColor)} />
      <Typography variant="caption" className={classes.warningColor}>
          {warning}
      </Typography>
    </pre>
  );
};

export default WarningMessage;
