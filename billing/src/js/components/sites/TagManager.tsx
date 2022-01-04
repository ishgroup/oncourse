/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import {
  Alert, AlertTitle, Button, Grid, Link, Typography
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { GTMContainer } from '../../models/Google';
import Loading from '../common/Loading';
import { Dispatch } from 'redux';
import { getGtmAndGaAccounts } from '../../redux/actions/Google';

interface Props {
  gtmContainer: GTMContainer;
  gtmContainerId: string;
  googleProfileEmail: string;
  hasNoGTMAccounts: boolean;
  dispatch: Dispatch;
}

const TagManager = ({
  gtmContainer, gtmContainerId, googleProfileEmail, hasNoGTMAccounts, dispatch
}: Props) => (
  <Grid container>
    {
      hasNoGTMAccounts && (
        <Grid item xs={12}>
          <Alert severity="error">
            <AlertTitle>Tag manager account not found</AlertTitle>
            We have't found any Google Tag manager accounts associated with
            {' '}
            {googleProfileEmail}
            . Please create account
            {' '}
            <Link href="https://tagmanager.google.com">here</Link>
            {' '}
            to use tagmanager and analytics features on your site
            <div className="d-flex justify-content-end">
              <Button
                disableElevation
                size="small"
                variant="contained"
                startIcon={<RefreshIcon />}
                onClick={() => dispatch(getGtmAndGaAccounts())}
              >
                Try again
              </Button>
            </div>
          </Alert>
        </Grid>
      )
    }
    {!hasNoGTMAccounts && (
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
