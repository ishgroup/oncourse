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
  Typography
} from '@mui/material';
import { SiteDTO } from '@api/model';
import { FormikErrors } from 'formik/dist/types';
import { renderSelectItemsWithEmpty } from '../../utils';
import AutosizeInput from '../common/AutosizeInput';
import { TemplateField } from '../common/TemplateChoser';
import { SiteValues } from '../../models/Sites';
import WarningMessage from '../common/WarningMessage';

interface Props {
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
}

const URLs = (
  {
    classes,
    isNew,
    collegeKey,
    site,
    initial,
    error,
    setFieldValue,
    setFieldError,
    handleChange
  }: Props
) => {
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
    if (!site.googleAnalyticsId) {
      if (site.gtmAccountId) {
        setFieldValue('gtmAccountId', null);
      }
      if (site.gaWebPropertyId) {
        setFieldValue('gaWebPropertyId', null);
      }
    }
  }, [site.googleAnalyticsId]);

  const domainItems = useMemo(() => renderSelectItemsWithEmpty(
    { items: Object.keys(site.domains).filter((d) => !site.domains[d]) }
  ), [site.domains]);

  const domainWarnings = useMemo(() => Object.keys(site.domains).map((d) => (site.domains[d]
    ? <WarningMessage warning={`${d}: ${site.domains[d]}`} />
    : null)),
  [site.domains]);

  return (
    <Grid container>
      <Grid item xs={12}>
        <h4 className="coloredHeaderText">Domain setup</h4>
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
          onChange={(e, v: string[]) => setFieldValue(
            'domains',
            { ...site.domains, [v.find((val) => !Object.keys(site.domains).includes(val))]: '' }
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
    </Grid>
  );
};

export default URLs;
