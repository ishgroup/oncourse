/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import { Grid, Typography } from '@mui/material';

const Billing = () => (
  <Grid container>
    <Grid item xs={6}>
      <Typography variant="caption" color="textSecondary">
        Licenced concurent users
      </Typography>
      <Typography variant="body1">
        12
      </Typography>
    </Grid>
    <Grid item xs={6}>
      <Typography variant="caption" color="textSecondary">
        Plan
      </Typography>
      <Typography variant="body1">
        Enterprise
      </Typography>
    </Grid>
  </Grid>
);

export default Billing;
