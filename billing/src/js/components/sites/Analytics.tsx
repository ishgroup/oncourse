import * as React from 'react';
import {
  useAnalyticsApi,
  useAuthorize,
  useDataChart,
  useViewSelector,
} from 'react-use-analytics-api';
import { Typography } from '@mui/material';
import { useAppSelector } from '../../redux/hooks/redux';
import Loading from '../common/Loading';

function Analytics() {
  const {
    ready, gapi, authorized, error
  } = useAnalyticsApi();
  const [viewId, setViewId] = React.useState<any>();
  const viewSelectorContainerId = 'view-selector-container';
  useViewSelector(
    authorized ? gapi : undefined,
    viewSelectorContainerId,
    (viewId) => setViewId(viewId)
  );
  const query = {
    ids: viewId,
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
      width: 500
    },
  };
  useDataChart(authorized ? gapi : undefined, query, chart);

  const [authorizeCalled, setAuthorizeCalled] = React.useState(false);

  const access_token = useAppSelector((state) => state.google?.token?.access_token);

  const authorize = useAuthorize(gapi, {
    serverAuth: {
      access_token
    }
  });

  React.useEffect(() => {
    if (ready && !error && !authorizeCalled && access_token) {
      authorize();
      setAuthorizeCalled(true);
    }
  }, [ready, error, authorizeCalled, authorize, access_token]);

  return (
    <div>
      {!ready && <Loading />}
      {ready && (
        <div>
          {authorized && (
            <div>
              <div style={{ marginTop: '30px' }}>
                <div className="data-chart" id="data-chart-container" style={{ width: '500px' }} />
              </div>
              <div id={viewSelectorContainerId} />
            </div>
          )}
          {
            !authorized && <Typography variant="caption" color="textSecondary">Authorizie with google to see data</Typography>
          }
        </div>
      )}
      {error && <div className="error-color">{error.toString()}</div>}
    </div>
  );
}

export default Analytics;
