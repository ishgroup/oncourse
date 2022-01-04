import React, { useEffect, useMemo, useState } from 'react';
import {
  useAnalyticsApi,
  useAuthorize,
  useDataChart
} from 'react-use-analytics-api';
import {
  Alert, AlertTitle, Button, Grid, Link, TextField, Typography
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { Dispatch } from 'redux';
import { useAppSelector } from '../../redux/hooks/redux';
import { renderSelectItems } from '../../utils';
import { getGtmAndGaAccounts } from '../../redux/actions/Google';

interface Props {
  googleAnalyticsId: string,
  gaWebPropertyId: string,
  googleProfileEmail: string;
  hasNoGAAccounts: boolean;
  dispatch: Dispatch;
}

const Analytics = ({
  gaWebPropertyId, googleAnalyticsId, googleProfileEmail, hasNoGAAccounts, dispatch
}: Props) => {
  const [authorizeCalled, setAuthorizeCalled] = useState(false);
  const [profileId, setProfileId] = useState(null);

  const gaWebProfiles = useAppSelector((state) => state.google.gaWebProfiles);

  const {
    ready, gapi, authorized, error
  } = useAnalyticsApi();

  const gaProfileItems = useMemo(() => renderSelectItems(
    {
      items: (gaWebProfiles || {})[gaWebPropertyId],
      valueKey: 'id',
      labelKey: 'name'
    }
  ), [gaWebPropertyId, gaWebProfiles]);

  useEffect(() => {
    if (gaWebProfiles && gaWebPropertyId && gaWebProfiles[gaWebPropertyId]) {
      setProfileId(gaWebProfiles[gaWebPropertyId][0]?.id);
    }
  }, [gaWebProfiles, gaWebPropertyId]);

  const query = {
    ids: `ga:${profileId}`,
    'start-date': '28daysAgo',
    'end-date': 'today',
    metrics: 'ga:sessions',
    dimensions: 'ga:date',
  };

  const chart = {
    container: 'data-chart-container',
    type: 'LINE',
    options: {
      title: 'Sessions (28 Days)',
      width: 600
    },
  };

  useDataChart(authorized && profileId && gaWebPropertyId ? gapi : undefined, query, chart);

  const access_token = useAppSelector((state) => state.google?.token?.access_token);

  const authorize = useAuthorize(gapi, {
    serverAuth: {
      access_token
    }
  });

  useEffect(() => {
    if (ready && !error && !authorizeCalled && access_token) {
      authorize();
      setAuthorizeCalled(true);
    }
  }, [ready, error, authorizeCalled, authorize, access_token]);

  return (
    <div>
      {
        hasNoGAAccounts && (
          <Alert severity="error">
            <AlertTitle>Analytics account not found</AlertTitle>
            We have't found any Google analytics accounts associated with
            {' '}
            {googleProfileEmail}
            . Please create account
            {' '}
            <Link href="https://analytics.google.com/analytics/web">here</Link>
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
        )
      }

      {!hasNoGAAccounts && ready && (
        <div>
          {authorized && gaWebPropertyId && googleAnalyticsId && (
            <div>
              <Grid container>
                <Grid item xs={6}>
                  <TextField
                    select
                    fullWidth
                    margin="normal"
                    label="Selected view"
                    value={profileId || ''}
                    variant="standard"
                    onChange={(e) => setProfileId(e.target.value)}
                    helperText={gaProfileItems.length ? null : 'No profiles (views) found on selected property'}
                  >
                    {gaProfileItems}
                  </TextField>
                </Grid>
              </Grid>
              <div style={{ marginTop: '30px' }}>
                <div className="data-chart" id="data-chart-container" />
              </div>
              <div id="view-selector-container" />
            </div>
          )}
          {
            !authorized && <Typography color="textSecondary">Authorizie with google to see analytics data</Typography>
          }
          {
            !hasNoGAAccounts && (!gaWebPropertyId || !googleAnalyticsId) && <Typography color="textSecondary">Select or create web property in configure section to see analytics data</Typography>
          }
        </div>
      )}
      {error && <div className="error-color">{error.toString()}</div>}
    </div>
  );
};

export default Analytics;
