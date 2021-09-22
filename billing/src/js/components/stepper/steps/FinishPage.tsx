/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from 'react';
import { Typography } from '@mui/material';
import CheckCircle from '@mui/icons-material/CheckCircle';
import { makeAppStyles } from '../../../styles/makeStyles';

const useStyles = makeAppStyles()({
  finishTitle: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: 20,
  }
});

const FinishPage = () => {
  const { classes } = useStyles();

  return (
    <div>
      <Typography variant="h3" component="h3" className={classes.finishTitle}>
        <CheckCircle fontSize="large" />
&nbsp;All done!
      </Typography>
      <Typography variant="subtitle1" component="div">
        Servers are being created now and your system prepared. You'll get an email in a few minutes with your login details.
      </Typography>
    </div>
  );
};

export default FinishPage;
