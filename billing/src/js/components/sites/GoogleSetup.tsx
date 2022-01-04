/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import {
  Grid,
  TextField,
  Link,
  AlertTitle,
  Alert
} from '@mui/material';
import HelpIcon from '@mui/icons-material/Help';
import { FormikErrors } from 'formik/dist/types';
import { SiteValues } from '../../models/Sites';

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
    concurentAccounts
  }: Props
) => {
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
          helperText={site.googleAnalyticsId ? null : 'Select Tag manager account first'}
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
          helperText={site.gtmAccountId ? null : 'Select Tag manager account first'}
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
                      Paste the API key to activate live mapping
                      {' '}
                      <Link
                        display="inline-block"
                        sx={{
                          verticalAlign: 'sub'
                        }}
                        href="https://www.ish.com.au/onCourse/doc/manual"
                      >
                        <HelpIcon fontSize="inherit" />
                      </Link>
                      {' '}
                    </>
                  ) : 'Select Tag manager account first'
              }
        />
      </Grid>
    </Grid>
  );
};

export default GoogleSetup;
