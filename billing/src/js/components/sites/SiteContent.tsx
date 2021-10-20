/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from 'react';
import {
  Autocomplete,
  Chip,
  Grid,
  TextField,
  Typography,
  Button,
  Link,
  AlertTitle,
  Alert
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { Cx } from 'tss-react/src/types';
import { SiteDTO } from '@api/model';
import { FormikErrors } from 'formik/dist/types';
import { renderSelectItemsWithEmpty } from '../../utils';
import AutosizeInput from '../common/AutosizeInput';
import { TemplateField } from '../common/TemplateChoser';
import { SiteValues } from '../../models/Sites';
import WarningMessage from '../common/WarningMessage';

interface Props {
  cx: Cx,
  classes: any;
  isNew: boolean;
  collegeKey: string;
  site: SiteValues;
  onClickDelete?: any;
  initial: SiteDTO;
  error: SiteValues;
  setFieldValue: (field: string, value: any, shouldValidate?: boolean | undefined) => Promise<FormikErrors<SiteValues>> | Promise<void>;
  setFieldError: any;
  handleChange: any;
  gaAccountItems: any;
  gtmAccountItems: any;
  gaWebPropertyItems: any;
  gtmContainerItems: any;
  loggedWithGoogle: boolean;
  googleProfileEmail: string;
}

const SiteContent = (
  {
    cx,
    classes,
    isNew,
    collegeKey,
    site,
    initial,
    error,
    setFieldValue,
    setFieldError,
    handleChange,
    gaAccountItems,
    gaWebPropertyItems,
    gtmAccountItems,
    gtmContainerItems,
    loggedWithGoogle,
    googleProfileEmail
  }: Props
) => {
  const hasNoGAAccounts = gaAccountItems.length === 1;
  const hasNoGTMAccounts = gtmAccountItems.length === 1;
  // const concurentAccounts = !isNew && site?.configuredByInfo?.email !== googleProfileEmail;
  const concurentAccounts = false;

  const initialMatchPattern = initial && initial.key.includes(`${collegeKey}-`);

  const onKeyChange: any = (e) => {
    const { value } = e.target;
    const newValue = initialMatchPattern ? `${collegeKey}-${value}` : value;
    let shouldValidte = true;
    if (initial && initial.key !== newValue && !newValue.match(`${collegeKey}-`)) {
      shouldValidte = false;
      setFieldError('key', `Location should start with ${collegeKey}-`);
    }
    if (initial && initial.key !== newValue && newValue.match(`${collegeKey}-`) && !newValue.match(new RegExp(`${collegeKey}-[^\\s]`))) {
      shouldValidte = false;
      setFieldError('key', 'Location has invalid format');
    }
    setFieldValue('key', newValue, shouldValidte);
  };

  const onAddDomain = async (params) => {
    await setFieldValue('domains', { ...site.domains, [params.inputProps.value]: '' });
    if (!site.domains.length && !site.primaryDomain) setFieldValue('primaryDomain', params.inputProps.value);
  };

  useEffect(() => {
    if (site.primaryDomain && !Object.keys(site.domains).includes(site.primaryDomain)) {
      setFieldValue('primaryDomain', null);
    }
  }, [site.domains]);

  useEffect(() => {
    if (!site.gtmAccountId && site.gtmContainerId) {
      setFieldValue('gtmContainerId', null);
    }
  }, [site.gtmAccountId]);

  useEffect(() => {
    if (!site.googleAnalyticsId && site.gtmAccountId) {
      setFieldValue('gtmAccountId', null);
    }
  }, [site.googleAnalyticsId]);

  const domainItems = useMemo(() => renderSelectItemsWithEmpty(
    { items: Object.keys(site.domains) }
  ), [site.domains]);

  const domainWarnings = useMemo(() => Object.keys(site.domains).map((d) => (site.domains[d]
    ? <WarningMessage warning={`${d}: ${site.domains[d]}`} />
    : null)),
  [site.domains]);

  return (
    <Grid container>
      <Grid item xs={12}>
        <h4 className={classes.coloredHeaderText}>Domain setup</h4>
      </Grid>
      <Grid item xs={6} className={classes.pr}>
        <TextField
          name="name"
          value={site?.name}
          onChange={handleChange}
          error={Boolean(error.name)}
          helperText={error.name}
          variant="standard"
          label="Website page title prefix"
          fullWidth
        />
      </Grid>
      <Grid item xs={6}>
        <Typography variant="caption" color={error.key ? 'error' : 'textSecondary'} className={classes.floatLabel}>
          Site
          location
        </Typography>
        <div className={classes.textFieldWrapper2}>
          {isNew ? (
            <>
              <Typography>
                https://
                {isNew || initialMatchPattern ? `${collegeKey}-` : ''}
              </Typography>
              <Typography variant="body1" component="span">
                <AutosizeInput
                  value={initialMatchPattern ? site.key.replace(`${collegeKey}-`, '') : site.key}
                  error={Boolean(error.key)}
                  onChange={onKeyChange}
                />
              </Typography>
              <Typography>.oncourse.cc</Typography>
            </>
          ) : (
            <Typography variant="body1">
              https://
              {isNew ? `${collegeKey}-` : ''}
              {site.key}
              .oncourse.cc
            </Typography>
          )}

        </div>
        {Boolean(error.key) && (<Typography className={classes.errorMessage}>{error.key}</Typography>)}
      </Grid>
      <Grid item xs={6} className={classes.pr}>
        <Autocomplete
          size="small"
          options={[]}
          value={Object.keys(site.domains)}
          classes={{
            root: classes.domainsRoot,
            inputRoot: classes.domainsInput,
            hasClearIcon: classes.hasClearIcon
          }}
          renderInput={(params: any) => (
            <TextField
              {...params}
              error={Boolean(error.domains)}
              InputProps={{
                ...params.InputProps,
                margin: 'none',
                endAdornment: params.inputProps.value
                  ? (
                    <Chip
                      size="small"
                      label="Add"
                      clickable
                      onClick={() => onAddDomain(params)}
                    />
                  )
                  : null
              }}
              helperText={Array.isArray(error.domains) ? error.domains.find((d) => d) : error.domains}
              variant="standard"
              label="Site domains"
              margin="normal"
              multiline
            />
          )}
          onChange={(e, v) => setFieldValue(
            'domains',
            v.reduce((p, c: string) => ({ ...p, [c]: '' }), {})
          )}
          filterSelectedOptions
          multiple
          freeSolo
        />
      </Grid>
      <Grid item xs={6} marginTop="auto">
        <TextField
          select
          fullWidth
          margin="normal"
          label="Primary hostname"
          value={site.primaryDomain || ''}
          onChange={handleChange}
          name="primaryDomain"
          variant="standard"
        >
          {domainItems}
        </TextField>
      </Grid>
      <Grid item xs={12}>
        {domainWarnings}
      </Grid>
      {isNew && (
      <Grid item xs={6} className={classes.pr}>
        <TemplateField
          label="Site template"
          name="webSiteTemplate"
          onChange={(val) => setFieldValue('webSiteTemplate', val)}
          value={site.webSiteTemplate}
          helperText={error.webSiteTemplate}
          error={Boolean(error.webSiteTemplate)}
          variant="standard"
          margin="normal"
          fullWidth
        />
      </Grid>
      )}

      <Grid item xs={12}>
        <div className="centeredFlex mt-2 mb-1">
          <h4 className={cx(classes.coloredHeaderText, 'm-0')}>Google services setup</h4>
        </div>
      </Grid>

      {
       !concurentAccounts && !loggedWithGoogle && (
       <>
         <Alert severity="warning">
           <AlertTitle>Not logged into Google</AlertTitle>
           Please login with LOGIN button above to see google services settings and analytics
         </Alert>
       </>
       )
      }

      {
        !concurentAccounts && loggedWithGoogle && hasNoGAAccounts && (
        <Alert severity="error">
          <AlertTitle>Analytics account not found</AlertTitle>
          We have't found any Google analytics accounts associated with
          {' '}
          {googleProfileEmail}
          . Please create account
          {' '}
          <Link href="#">here</Link>
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
        !concurentAccounts && loggedWithGoogle && !hasNoGAAccounts && hasNoGTMAccounts && (
          <Alert severity="error">
            <AlertTitle>Tag manager account not found</AlertTitle>
            We have't found any Google Tag manager accounts associated with
            {' '}
            {googleProfileEmail}
            . Please create account
            {' '}
            <Link href="#">here</Link>
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
        loggedWithGoogle && concurentAccounts && (
          <Alert severity="error">
            <AlertTitle>No access</AlertTitle>
            You have no access to the Google Tag Manager configuration for this website. Please ask
            {' '}
            {site?.configuredByInfo?.firstname}
            {' '}
            {site?.configuredByInfo?.lastname}
            user with
            {' '}
            {site?.configuredByInfo?.email}
            {' '}
            to add you to the Google Tag Manager authorised users.
            You can also set up this integration again from scratch.
          </Alert>
        )
      }

      {loggedWithGoogle && !hasNoGAAccounts && !hasNoGTMAccounts
         && (
           <>
             <Grid item xs={6} className={classes.pr}>
               <TextField
                 select
                 fullWidth
                 margin="normal"
                 label="Analytics account"
                 variant="standard"
                 value={site.googleAnalyticsId || ''}
                 onChange={handleChange}
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
                 disabled={!site.googleAnalyticsId}
                 error={Boolean(error.gaWebPropertyId)}
                 helperText={error.gaWebPropertyId || (site.googleAnalyticsId ? '' : 'Select Analytics account first')}
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
                 onChange={handleChange}
                 value={site.gtmAccountId || ''}
                 disabled={!site.googleAnalyticsId}
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
                 disabled={!site.gtmAccountId}
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
                 disabled={!site.gtmAccountId}
                 onChange={handleChange}
                 name="googleMapsApiKey"
                 helperText={
                   site.gtmAccountId
                     ? (
                       <>
                         Paste Google maps API key from
                         {' '}
                         <Link href="#">here</Link>
                         {' '}
                         to enable google maps on site
                       </>
                     ) : 'Select Tag manager account first'
                }
               >
                 {gtmContainerItems}
               </TextField>
             </Grid>
           </>
         )}
    </Grid>
  );
};

export default SiteContent;
