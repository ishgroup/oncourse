/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { useEffect } from 'react';
import { useAnalyticsApi } from 'react-use-analytics-api';
import { useAppSelector } from '../../redux/hooks/redux';

const Analytics = () => {
  const {
    token,
    clientId
  } = useAppSelector((state) => state.google);

  const {
    ready, gapi, authorized, error
  } = useAnalyticsApi();

  useEffect(() => {
    if (clientId && token && ready && !authorized) {
      gapi.analytics.auth.authorize({
        serverAuth: {
          access_token: token
        }
      });
      console.log({ready, gapi, authorized, error});
    }
  }, [ready, clientId, token]);




  return null;
};

export default Analytics;
