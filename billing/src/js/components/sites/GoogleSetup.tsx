/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import {
  Grid,
  TextField,
  Button,
  Link,
  AlertTitle,
  Alert
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { FormikErrors } from 'formik/dist/types';
import { SiteValues } from '../../models/Sites';
import { useAppDispatch } from '../../redux/hooks/redux';
import { getGtmAndGaAccounts } from '../../redux/actions/Google';

interface Props {
  classes: any;
  site: SiteValues;
  error: SiteValues;
  setFieldValue: (field: string, value: any, shouldValidate?: boolean | undefined) => Promise<FormikErrors<SiteValues>> | Promise<void>;
  handleChange: any;
  gaAccountItems: any;
  gtmAccountItems: any;
  gaWebPropertyItems: any;
  gtmContainerItems: any;
  googleProfileEmail: string;
  concurentAccounts: boolean;
}

const GoogleSetup = (
  {
    classes,
    site,
    error,
    setFieldValue,
    handleChange,
    gaAccountItems,
    gaWebPropertyItems,
    gtmAccountItems,
    gtmContainerItems,
    googleProfileEmail,
    concurentAccounts
  }: Props
) => {
  const hasNoGAAccounts = gaAccountItems.length === 1;
  const hasNoGTMAccounts = gtmAccountItems.length === 1;

  const dispatch = useAppDispatch();

  const onGtmAccountIdChange = (e) => {
    if (!e.target.value && site.gtmContainerId) {
      setFieldValue('gtmContainerId', null);
    }
    handleChange(e);
  };

  const onAnalyticsIdChange = (e) => {
    if (!e.target.value) {
      if (site.gaWebPropertyId) {
        setFieldValue('gaWebPropertyId', null);
      }
    }
    handleChange(e);
  };

  return (
    <Grid container>
      <Grid item xs={12}>
        <div className="centeredFlex mt-2 mb-1">
          <h4 className="coloredHeaderText m-0">Google services setup</h4>
        </div>
      </Grid>
      {
        !concurentAccounts && hasNoGAAccounts && (
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

      {
        !concurentAccounts && !hasNoGAAccounts && hasNoGTMAccounts && (
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
              >
                Try again
              </Button>
            </div>
          </Alert>
        )
      }

      {
        concurentAccounts && (
          <Alert severity="error">
            <AlertTitle>No access</AlertTitle>
            You have no access to the Google Tag Manager configuration for this website. Please ask
            {' '}
            {site?.configuredByInfo?.firstname}
            {' '}
            {site?.configuredByInfo?.lastname}
            {' '}
            user with
            {' '}
            {site?.configuredByInfo?.email}
            {' '}
            email to add you to the Google Tag Manager authorised users.
            You can also set up this integration again from scratch.
          </Alert>
        )
      }

      {
        !hasNoGAAccounts && !hasNoGTMAccounts && (
        <>
          <Grid item xs={6} className={classes.pr}>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Analytics account"
              variant="standard"
              value={site.googleAnalyticsId || ''}
              onChange={onAnalyticsIdChange}
              name="googleAnalyticsId"
            >
              {gaAccountItems}
            </TextField>
          </Grid>

          <Grid item xs={6}>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Analytics web property"
              variant="standard"
              value={site.gaWebPropertyId || ''}
              onChange={handleChange}
              name="gaWebPropertyId"
              helperText={site.googleAnalyticsId ? 'Leave empty to let us create property configuration' : 'Select Tag manager account first'}
            >
              {gaWebPropertyItems}
            </TextField>
          </Grid>

          <Grid item xs={6} className={classes.pr}>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Tag manager account"
              variant="standard"
              name="gtmAccountId"
              onChange={onGtmAccountIdChange}
              value={site.gtmAccountId || ''}
              error={Boolean(error.gtmAccountId)}
              helperText={error.gtmAccountId || (site.googleAnalyticsId ? '' : 'Select Analytics account first')}
            >
              {gtmAccountItems}
            </TextField>
          </Grid>

          <Grid item xs={6}>
            <TextField
              select
              fullWidth
              margin="normal"
              label="Tag manager container"
              value={site.gtmContainerId || ''}
              variant="standard"
              onChange={handleChange}
              name="gtmContainerId"
              helperText={site.gtmAccountId ? 'Leave empty to let us create container configuration' : 'Select Tag manager account first'}
            >
              {gtmContainerItems}
            </TextField>
          </Grid>

          <Grid item xs={6} className={classes.pr}>
            <TextField
              fullWidth
              margin="normal"
              label="Google maps API key"
              value={site.googleMapsApiKey || ''}
              variant="standard"
              onChange={handleChange}
              name="googleMapsApiKey"
              helperText={
                site.gtmAccountId
                  ? (
                    <>
                      Create Google maps API key
                      {' '}
                      <Link href="https://developers.google.com/maps/documentation/javascript/get-api-key#creating-api-keys">here</Link>
                      {' '}
                      and paste it to enable google maps on site
                    </>
                  ) : 'Select Tag manager account first'
              }
            />
          </Grid>
        </>
        )
       }
    </Grid>
  );
};

export default GoogleSetup;
