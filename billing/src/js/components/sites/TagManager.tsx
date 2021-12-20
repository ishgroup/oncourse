/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import { Grid, Typography } from '@mui/material';
import { GTMContainer } from '../../models/Google';
import Loading from '../common/Loading';

interface Props {
  gtmContainer: GTMContainer;
  gtmContainerId: string;
  loading: boolean;
}

const TagManager = ({ gtmContainer, gtmContainerId, loading }: Props) => (
  <Grid container>
    {loading && <Loading />}
    {!loading && (
    <Grid item xs={6}>
      <Typography variant="caption" color="textSecondary">
        Used container
      </Typography>
      <Typography variant="body1">
        {gtmContainer?.name
          ? `${gtmContainer.name} (${gtmContainer.publicId})`
          : (gtmContainerId || <span className="text-placeholder">Not specified</span>)}
      </Typography>
    </Grid>
    )}
  </Grid>
);

export default TagManager;
