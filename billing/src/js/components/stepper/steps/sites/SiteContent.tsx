/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
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
  Button
} from '@mui/material';
import { Delete, ExpandMore } from '@mui/icons-material';
import { stopPropagation } from '../../../../utils';
import AutosizeInput from '../../../common/AutosizeInput';
import { TemplateField } from '../../../common/TemplateChoser';

const renderSelectItems = (domains) => domains.map((domain) => (
  <MenuItem key={domain} value={domain}>
    {domain}
  </MenuItem>
));

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
    initialMatchPattern,
    onKeyChange,
    onAddDomain,
    setFieldValue,
    values,
    handleChange,
    onSetPrimaryDomain
  }
) => (
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
            onClick={stopPropagation}
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
                    onChange={(e) => onKeyChange(e, index, isNew, initial, initialMatchPattern)}
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
                        onClick={() => onAddDomain(site, index, params)}
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
        <Grid item xs={6} marginTop="auto" className={classes.pr}>
          <TextField
            select
            fullWidth
            margin="normal"
            label="Primary hostname"
            value={site.primaryDomain || ''}
            onChange={(e) => onSetPrimaryDomain(e.target.value, index)}
            variant="standard"
          >
            {renderSelectItems(site.domains)}
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
          <div className="centeredFlex mt-2 mb-1">
            <h4 className={cx(classes.coloredHeaderText, 'm-0')}>Google Tag Manager</h4>
            <Switch defaultChecked color="primary" />
          </div>

          <Alert severity="error">
            <AlertTitle>Another user account found</AlertTitle>
            Google Tag Manager is already configured for sam@gmail.com user. Do you want to completelly remove existing settings and create your own? This action can not be undone
            <div className="d-flex justify-content-end">
              <Button disableElevation size="small" variant="contained">Proceed anyway</Button>
            </div>
          </Alert>
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

        {/* <Grid item xs={6} className={classes.pr}> */}
        {/*  <TextField */}
        {/*    select */}
        {/*    fullWidth */}
        {/*    margin="normal" */}
        {/*    label="Used container" */}
        {/*    value="Account 1" */}
        {/*    variant="standard" */}
        {/*  > */}
        {/*    <MenuItem value="Account 1"> */}
        {/*      Account 1 */}
        {/*    </MenuItem> */}
        {/*  </TextField> */}
        {/* </Grid> */}

        <Grid item xs={12}>
          <div className="centeredFlex mt-2 mb-2">
            <h4 className={cx(classes.coloredHeaderText, 'm-0')}>Google Analytics</h4>
            <Switch disabled color="primary" />
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
