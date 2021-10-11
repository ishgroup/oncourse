/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo, useState } from 'react';
import {
  Accordion,
  Autocomplete,
  AccordionDetails,
  AccordionSummary,
  Chip,
  Grid,
  IconButton, MenuItem,
  TextField,
  Typography,
  Switch,
  Alert,
  AlertTitle,
  Button, Collapse
} from '@mui/material';
import { Delete, ExpandMore } from '@mui/icons-material';
import { Cx } from 'tss-react/src/types';
import { SiteDTO } from '@api/model';
import { FormikErrors } from 'formik/dist/types';
import { stopPropagation, renderSelectItemsWithEmpty } from '../../../../utils';
import AutosizeInput from '../../../common/AutosizeInput';
import { TemplateField } from '../../../common/TemplateChoser';
import { NumberArgFunction } from '../../../../models/CommonFunctions';
import { SiteValues } from '../../../../models/Sites';
import { useAppSelector } from '../../../../redux/hooks/redux';

interface Props {
  cx: Cx,
  classes: any;
  isNew: boolean;
  expanded: boolean | number;
  handleExpand: any;
  collegeKey: string;
  site: SiteDTO;
  onClickDelete: any;
  index: number;
  initial: any;
  error: SiteDTO;
  initialMatchPattern: boolean;
  setFieldValue: (field: string, value: any, shouldValidate?: boolean | undefined) => Promise<FormikErrors<SiteValues>> | Promise<void>;
  setFieldError: any;
  values: SiteValues;
  handleChange: any;
}

export const SiteContent = (
  {
    cx,
    classes,
    isNew,
    expanded,
    handleExpand,
    collegeKey,
    site,
    onClickDelete,
    index,
    initial,
    error,
    setFieldValue,
    setFieldError,
    values,
    handleChange,
    initialMatchPattern
  }: Props
) => {
  const [GTMEnabled, setGTMEnabled] = useState(Boolean(site.gtmContainerId));
  const [GAEnabled, setGAEnabled] = useState(Boolean(site.googleAnalyticsId));

  const {
    profile,
    gtmAccounts,
    gaAccounts,
    gtmContainers
  } = useAppSelector((state) => state.google);

  const loggedWithGoogle = Boolean(profile);

  const onChangeGTMSwitch = (e, val) => {
    setGTMEnabled(val);
  };

  const onChangeGASwitch = (e, val) => {
    setGAEnabled(val);
  };

  const onKeyChange: any = (e) => {
    const { value } = e.target;
    const name = `sites[${index}].key`;
    const newValue = initialMatchPattern ? `${collegeKey}-${value}` : value;
    let shouldValidte = true;
    if (initial && initial.key !== newValue && !newValue.match(`${collegeKey}-`)) {
      shouldValidte = false;
      setFieldError(name, `Location should start with ${collegeKey}-`);
    }
    if (initial && initial.key !== newValue && newValue.match(`${collegeKey}-`) && !newValue.match(new RegExp(`${collegeKey}-[^\\s]`))) {
      shouldValidte = false;
      setFieldError(name, 'Location has invalid format');
    }
    setFieldValue(name, newValue, shouldValidte);
  };

  const onAddDomain = async (params) => {
    await setFieldValue(`sites[${index}].domains`, [...site.domains, params.inputProps.value]);
    if (!site.domains.length && !site.primaryDomain) setFieldValue(`sites[${index}].primaryDomain`, params.inputProps.value);
  };

  useEffect(() => {
    if (site.primaryDomain && !site.domains.includes(site.primaryDomain)) {
      setFieldValue(`sites[${index}].primaryDomain`, null);
    }
  }, [site.domains]);

  useEffect(() => {
    if (!site.gtmAccountId && site.gtmContainerId) {
      setFieldValue(`sites[${index}].gtmContainerId`, null);
    }
  }, [site.gtmAccountId]);

  const domainItems = useMemo(() => renderSelectItemsWithEmpty(site.domains), [site.domains]);

  const gtmAccountItems = useMemo(() => renderSelectItemsWithEmpty(
    gtmAccounts,
    'accountId',
    'name'
  ), [gtmAccounts]);

  const gtmContainerItems = useMemo(() => renderSelectItemsWithEmpty(
    (gtmContainers || {})[site.gtmAccountId],
    'containerId',
    'name'
  ), [gtmContainers, site.gtmAccountId]);

  return (
    <Accordion
      classes={{
        root: classes.root,
        expanded: classes.rootExpanded
      }}
      defaultExpanded={isNew}
      elevation={0}
      TransitionProps={{ unmountOnExit: true, mountOnEnter: true }}
      expanded={expanded === (site.id || index)}
      onChange={handleExpand(site.id || index)}
    >
      <AccordionSummary
        expandIcon={<ExpandMore />}
        classes={{
          root: classes.summaryRoot,
          focusVisible: classes.summaryFocused,
          expandIconWrapper: classes.expandIcon,
          content: classes.summaryContent
        }}
      >
        <Typography variant="body1">
          https://
          {isNew ? `${collegeKey}-` : ''}
          {site.key}
          .oncourse.cc
        </Typography>
        <div>
          <IconButton className={classes.deleteIcon} onClick={onClickDelete} size="large">
            <Delete fontSize="inherit" />
          </IconButton>
        </div>
      </AccordionSummary>
      <AccordionDetails>
        <Grid container>
          <Grid item xs={6} className={classes.pr}>
            <TextField
              name={`sites[${index}].name`}
              value={values?.sites[index]?.name}
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
              value={site.domains}
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
              onChange={(e, v) => setFieldValue(`sites[${index}].domains`, v)}
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
              name={`sites[${index}].primaryDomain`}
              variant="standard"
            >
              {domainItems}
            </TextField>
          </Grid>
          {isNew && (
          <Grid item xs={6} className={classes.pr}>
            <TemplateField
              label="Site template"
              name={`sites[${index}].webSiteTemplate`}
              onChange={(val) => setFieldValue(`sites[${index}].webSiteTemplate`, val)}
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
            <div className="centeredFlex mt-2">
              <h4 className={cx(classes.coloredHeaderText, 'm-0')}>Google Tag Manager</h4>
              <Switch
                checked={GTMEnabled}
                onChange={onChangeGTMSwitch}
                disabled={!loggedWithGoogle}
                color="primary"
              />
            </div>

            <Collapse in={GTMEnabled}>
              <Typography variant="caption" color="textSecondary">
                You can select account and container associated with
                {' '}
                {profile?.email}
                {' '}
                from inputs below or let us create all required configurations
              </Typography>
              <div className="d-flex justify-content-end">
                <Button disableElevation size="small" variant="contained">Configure</Button>
              </div>
              <Grid container>
                <Grid item xs={6} className={classes.pr}>
                  <TextField
                    select
                    fullWidth
                    margin="normal"
                    label="Used account"
                    value={site.gtmAccountId || ''}
                    variant="standard"
                    onChange={handleChange}
                    name={`sites[${index}].gtmAccountId`}
                  >
                    {gtmAccountItems}
                  </TextField>
                </Grid>

                <Grid item xs={6}>
                  <TextField
                    select
                    fullWidth
                    margin="normal"
                    label="Used container"
                    value={site.gtmContainerId || ''}
                    variant="standard"
                    disabled={!site.gtmAccountId}
                    onChange={handleChange}
                    name={`sites[${index}].gtmContainerId`}
                  >
                    {gtmContainerItems}
                  </TextField>
                </Grid>
              </Grid>

              {/* <Alert severity="error"> */}
              {/*  <AlertTitle>No access</AlertTitle> */}
              {/*  Your account has no access to Google Tag Manager configuration. Current configuration is created by sam@gmail.com user. */}
              {/*  You can contact him for access or completelly remove existing settings and create your own. This action can not be undone */}
              {/*  <div className="d-flex justify-content-end"> */}
              {/*    <Button disableElevation size="small" variant="contained">Overrite settings</Button> */}
              {/*  </div> */}
              {/* </Alert> */}

            </Collapse>
          </Grid>

          <Grid item xs={12}>
            <div className="centeredFlex mt-2 mb-2">
              <h4 className={cx(classes.coloredHeaderText, 'm-0')}>Google Analytics</h4>
              <Switch
                checked={GAEnabled}
                onChange={onChangeGASwitch}
                disabled={!loggedWithGoogle}
                color="primary"
              />
            </div>
          </Grid>

          {/* <Grid item xs={6} className={classes.pr}> */}
          {/*  <TextField */}
          {/*    select */}
          {/*    fullWidth */}
          {/*    margin="normal" */}
          {/*    label="Used account" */}
          {/*    value="Account 1" */}
          {/*    variant="standard" */}
          {/*  > */}
          {/*    <MenuItem value="Account 1"> */}
          {/*      Account 1 */}
          {/*    </MenuItem> */}
          {/*  </TextField> */}
          {/* </Grid> */}

          {/* <Grid item xs={12}> */}
          {/*  <img */}
          {/*    src={analytics} */}
          {/*    loading="lazy" */}
          {/*  /> */}

          {/* </Grid> */}

        </Grid>
      </AccordionDetails>
    </Accordion>
  );
};
