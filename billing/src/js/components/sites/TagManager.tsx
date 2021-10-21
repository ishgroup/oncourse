/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import { Grid, Typography } from '@mui/material';
import { GTMContainer } from '../../models/Google';

interface Props {
  gtmContainer: GTMContainer;
}

const TagManager = ({ gtmContainer }: Props) => (
  <Grid container>
    <Grid item xs={6}>
      <Typography variant="caption" color="textSecondary">
        Used container
      </Typography>
      <Typography variant="body1">
        {gtmContainer?.name ? `${gtmContainer.name} (${gtmContainer.publicId})` : <span className="text-placeholder">Not specified</span>}
      </Typography>
    </Grid>
    <Grid item xs={6}>

    </Grid>
  </Grid>
);

export default TagManager;
