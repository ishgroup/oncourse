/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { useEffect } from 'react';
import {
  Alert, AlertTitle,
  Button, Collapse, Grid, TextField, Typography
} from '@mui/material';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { BillingPlan } from '@api/model';
import { LoadingButton } from '@mui/lab';
import isEqual from 'lodash.isequal';
import { useAppDispatch, useAppSelector } from '../redux/hooks/redux';
import { renderSelectItems } from '../utils';
import { renderPlanLabel } from '../utils/Settings';
import { SettingsValues } from '../models/Settings';
import { updateSettings } from '../redux/actions/Settings';
import { usePrevious } from '../hooks/usePrevious';

const validationSchema = yup.object().shape({
  requestedBillingPlan: yup.string().nullable().when('showPlanChangeFields', {
    is: (val) => val,
    then: yup.string().nullable().required('Billing plan is required'),
  }),
  requestedUsersCount: yup.number()
    .when('showPlanChangeFields', {
      is: (val) => val,
      then: yup.number().nullable().required('Users count is required'),
      otherwise: yup.number().nullable().notRequired()
    })
    .when('requestedBillingPlan', {
      is: 'starter-21',
      then: yup.number().nullable().max(1, 'For Starter plan only one user is available'),
      otherwise: yup.number().nullable().min(2, 'Non Starter plan requires at least 2 users'),
    }),
  contactFullName: yup.string().nullable().required('Name is required'),
  contactEmail: yup.string().nullable().email('Please enter valid email').required('Email is reuired'),
});

const planItems = renderSelectItems(
  {
    items: Object.keys(BillingPlan),
    labelCondition: renderPlanLabel
  }
);

const Billing = () => {
  const settings = useAppSelector((state) => state.settings);
  const loading = useAppSelector((state) => state.loading);
  const dispatch = useAppDispatch();

  const {
    dirty,
    handleChange,
    handleSubmit,
    values,
    errors,
    isValid,
    resetForm,
    setValues,
    setFieldValue,
  } = useFormik<SettingsValues>({
    initialValues: { ...settings, showPlanChangeFields: false },
    validationSchema,
    onSubmit: ({ showPlanChangeFields, ...rest }) => {
      dispatch(updateSettings(rest));
    }
  });

  const prevSettings = usePrevious(settings);

  useEffect(() => {
    if (!isEqual(settings, prevSettings)) {
      resetForm({ values: { ...settings, showPlanChangeFields: false } });
    }
  }, [settings]);

  useEffect(() => {
    if (values.requestedBillingPlan === 'starter-21') {
      setFieldValue('requestedUsersCount', 1);
    }
  }, [values.requestedBillingPlan]);

  return (
    <form
      className="w-100"
      onSubmit={handleSubmit}
    >
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item xs={12}>
          <div className="centeredFlex mt-2 mb-1">
            <h4 className="coloredHeaderText m-0">Oncourse plan</h4>
          </div>
        </Grid>
        <Grid item xs={4}>
          <Typography variant="caption" color="textSecondary">
            Plan
          </Typography>
          <Typography variant="body1">
            {renderPlanLabel(settings.billingPlan)}
          </Typography>
        </Grid>
        <Grid item xs={4}>
          <Typography variant="caption" color="textSecondary">
            Licenced concurent users
          </Typography>
          <Typography variant="body1">
            {settings.billingPlan === 'starter-21' ? '1' : settings.usersCount}
          </Typography>
        </Grid>
        <Grid item xs={4} className="d-flex-end">
          {!values.showPlanChangeFields && !values.requestedBillingPlan && (
            <Button
              disableElevation
              size="small"
              variant="contained"
              onClick={() => setValues({
                ...values,
                requestedUsersCount: null,
                requestedBillingPlan: 'premium-21',
                showPlanChangeFields: true
              })}
            >
              Change
            </Button>
          )}
        </Grid>
        <Grid item xs={12}>
          <Collapse in={values.showPlanChangeFields}>
            <Grid container columnSpacing={3}>
              <Grid item xs={4}>
                <TextField
                  select
                  fullWidth
                  margin="normal"
                  variant="standard"
                  name="requestedBillingPlan"
                  label="New plan"
                  value={values.requestedBillingPlan || ''}
                  onChange={handleChange}
                  error={Boolean(errors.requestedBillingPlan)}
                  helperText={errors.requestedBillingPlan}
                >
                  {planItems}
                </TextField>
              </Grid>
              <Grid item xs={4}>
                <TextField
                  fullWidth
                  margin="normal"
                  variant="standard"
                  type="number"
                  name="requestedUsersCount"
                  label="New concurrent users"
                  value={values.requestedUsersCount || ''}
                  onChange={handleChange}
                  error={Boolean(errors.requestedUsersCount)}
                  helperText={errors.requestedUsersCount}
                  disabled={values.requestedBillingPlan === 'starter-21'}
                />
              </Grid>
              <Grid item xs={4} className="centeredFlex">
                {values.showPlanChangeFields && (
                  <Button
                    disableElevation
                    size="small"
                    variant="contained"
                    color="error"
                    onClick={() => {
                      setValues({
                        ...values,
                        requestedUsersCount: null,
                        requestedBillingPlan: null,
                        showPlanChangeFields: false
                      });
                    }}
                  >
                    Cancel
                  </Button>
                )}
              </Grid>
            </Grid>
          </Collapse>
          {!values.showPlanChangeFields && values.requestedBillingPlan && (
            <Alert severity="info" classes={{ message: 'w-100' }}>
              <AlertTitle>Plan changes requested</AlertTitle>
              Plan downgrades will take effect on the first of next month.
              New plan is for
              {' '}
              {values.requestedUsersCount}
              {' '}
              user
              {values.requestedUsersCount !== 1 ? 's' : ''}
              {' '}
              on the
              {' '}
              {renderPlanLabel(values.requestedBillingPlan)}
              {' '}
              plan.
              <div className="d-flex justify-content-end mt-2">
                <Button
                  size="small"
                  variant="contained"
                  color="error"
                  disableElevation
                  onClick={() => {
                    setValues({
                      ...values,
                      requestedUsersCount: null,
                      requestedBillingPlan: null,
                      showPlanChangeFields: false
                    });
                  }}
                >
                  Cancel
                </Button>
              </div>
            </Alert>
          )}
        </Grid>
        <Grid item xs={12}>
          <div className="centeredFlex mt-2 mb-1">
            <h4 className="coloredHeaderText m-0">Send invoices to</h4>
          </div>
        </Grid>
        <Grid item xs={4}>
          <TextField
            fullWidth
            margin="normal"
            variant="standard"
            name="contactFullName"
            label="Name"
            value={values.contactFullName || ''}
            onChange={handleChange}
            error={Boolean(errors.contactFullName)}
            helperText={errors.contactFullName}
          />
        </Grid>
        <Grid item xs={4}>
          <TextField
            fullWidth
            margin="normal"
            variant="standard"
            name="contactEmail"
            label="Email"
            value={values.contactEmail || ''}
            onChange={handleChange}
            error={Boolean(errors.contactEmail)}
            helperText={errors.contactEmail}
          />
        </Grid>
        <Grid item xs={4} />
        <Grid item xs={8}>
          <TextField
            fullWidth
            margin="normal"
            variant="standard"
            name="invoiceReference"
            label="Reference to put on invoice to you"
            value={values.invoiceReference || ''}
            onChange={handleChange}
          />
        </Grid>
        <Grid item xs={12} className="d-flex justify-content-end">
          <LoadingButton
            variant="contained"
            color="primary"
            type="submit"
            loading={loading}
            disabled={!isValid || !dirty}
            disableElevation
          >
            Save
          </LoadingButton>
        </Grid>
      </Grid>
    </form>

  );
};

export default Billing;
