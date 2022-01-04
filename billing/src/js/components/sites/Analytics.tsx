import React, { useEffect, useMemo, useState } from 'react';
import {
  useAnalyticsApi,
  useAuthorize,
  useDataChart
} from 'react-use-analytics-api';
import { Grid, TextField, Typography } from '@mui/material';
import { useAppSelector } from '../../redux/hooks/redux';
import Loading from '../common/Loading';
import { renderSelectItems } from '../../utils';

interface Props {
  googleAnalyticsId: string,
  gaWebPropertyId: string,
}

const Analytics = ({ gaWebPropertyId, googleAnalyticsId }: Props) => {
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
      {ready && (
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
                <div className="data-chart" id="data-chart-container"/>
              </div>
              <div id="view-selector-container" />
            </div>
          )}
          {
            !authorized && <Typography color="textSecondary">Authorizie with google to see analytics data</Typography>
          }
          {
            (!gaWebPropertyId || !googleAnalyticsId) && <Typography color="textSecondary">Select or create web property in configure section to see analytics data</Typography>
          }
        </div>
      )}
      {error && <div className="error-color">{error.toString()}</div>}
    </div>
  );
};

export default Analytics;
