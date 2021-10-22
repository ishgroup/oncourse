/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useState } from 'react';
import { Autocomplete } from '@mui/material';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import { makeAppStyles } from '../../../styles/makeStyles';
import CustomTextField from '../../common/TextField';
import Navigation from '../Navigations';
import { countries, countriesTimeZone } from '../../../utils';
import { addEventListenerWithDeps } from '../../../hooks/addEventListnerWithDeps';
import { State } from '../../../models/State';
import { createCollege, setOrganisationFormValues } from '../../../redux/actions/College';

const useStyles = makeAppStyles()((theme: any) => ({
  textFieldWrapper: {
    minHeight: '66px',
  },
  subTitle: {
    marginBottom: 30,
  },
  sectionTitle: {
    fontFamily: theme.typography.fontFamily,
    fontSize: 22
  },
  groupOrganization: {
    marginBottom: 50,
  },
  groupOrganizationDetails: {
    marginBottom: 20,
  },
}));

const validationSchema = yup.object({
  organisationName: yup.string().required('Required'),
  address: yup.string().required('Required'),
  suburb: yup.string().required('Required'),
  state: yup.string().required('Required'),
  postcode: yup.string().required('Required'),
  country: yup.string().required('Required').nullable(),
  timeZone: yup.string().required('Required').nullable(),
});

const OrganisationForm = (props: any) => {
  const { classes } = useStyles();
  const [validState, setValidState] = useState(false);
  const {
    activeStep,
    steps,
    handleBack,
    handleNext,
    organisationForm,
    setOrganisationFormValues,
    createCollege,
    collegeKey,
    webSiteTemplate,
    contactForm,
    collegeWasCreated,
  } = props;

  useEffect(() => {
    if (collegeWasCreated) handleNext();
  }, [collegeWasCreated]);

  const {
    handleChange, values, errors, setFieldValue, isValid, touched, dirty, handleBlur
  } = useFormik({
    initialValues: organisationForm,
    validationSchema,
    onSubmit: () => {},
  });

  const handleBackCustom = () => {
    setOrganisationFormValues(values);
    handleBack();
  };

  const handleNextCustom = () => {
    setOrganisationFormValues(values);
    createCollege({
      collegeKey,
      webSiteTemplate,
      ...contactForm,
      ...values,
    });
  };

  const keyPressHandler = useCallback((e) => {
    if (e.keyCode === 13 && !((dirty && !isValid) || (!dirty && !validState))) {
      handleNextCustom();
    }
  }, [dirty, isValid, validState]);

  addEventListenerWithDeps([keyPressHandler], keyPressHandler);

  useEffect(() => {
    validationSchema.validate(organisationForm).then(() => {
      setValidState(true);
    }).catch(() => {
      setValidState(false);
    });
  }, organisationForm);

  return (
    <form>
      <Typography variant="h4" component="h4" className="coloredHeaderText" color="primary" gutterBottom>
        Enter your organisation details
      </Typography>
      <Typography variant="subtitle1" gutterBottom className={classes.subTitle}>
        Tell us a bit about your business
      </Typography>

      <Typography variant="h5" component="h5" className={classes.sectionTitle} color="initial" gutterBottom>
        Your Organisation
      </Typography>
      <Grid container spacing={5} className={classes.groupOrganization}>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Organisation Name"
              label="Organisation Name"
              id="organisationName"
              autoFocus
              onChange={handleChange}
              value={values.organisationName}
              error={touched.organisationName && errors.organisationName}
              helperText={errors.organisationName}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Company No. / ABN"
              label="Company No. / ABN"
              id="abn"
              onChange={handleChange}
              value={values.abn}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Trading Name"
              label="Trading Name"
              id="tradingName"
              onChange={handleChange}
              value={values.tradingName}
              helperText="(if different to organisation name)"
            />
          </div>
        </Grid>
      </Grid>

      <Typography variant="h5" component="h5" className={classes.sectionTitle} color="initial" gutterBottom>
        Your Organisation's Details
      </Typography>
      <Grid container spacing={5} className={classes.groupOrganizationDetails}>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Street Address"
              label="Street Address"
              id="address"
              onChange={handleChange}
              value={values.address}
              error={touched.address && errors.address}
              helperText={errors.address}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="City or Suburb"
              label="City or Suburb"
              id="suburb"
              onChange={handleChange}
              value={values.suburb}
              error={touched.suburb && errors.suburb}
              helperText={errors.suburb}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="State"
              label="State"
              id="state"
              onChange={handleChange}
              value={values.state}
              error={touched.state && errors.state}
              helperText={errors.state}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Postcode / ZIP"
              label="Postcode / ZIP"
              id="postcode"
              onChange={handleChange}
              value={values.postcode}
              error={touched.postcode && errors.postcode}
              helperText={errors.postcode}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <Autocomplete
              id="country"
              options={countries}
              getOptionLabel={(option: string) => option}
              onChange={(_, value) => setFieldValue('country', value)}
              value={values.country}
              renderInput={(params) => (
                <CustomTextField
                  {...params}
                  label="Country"
                  margin="normal"
                  error={touched.country && errors.country}
                  helperText={errors.country}
                  onBlur={handleBlur}
                />
              )}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <Autocomplete
              id="timeZone"
              options={countriesTimeZone}
              getOptionLabel={(option: string) => option}
              onChange={(_, value) => setFieldValue('timeZone', value)}
              value={values.timeZone}
              renderInput={(params) => (
                <CustomTextField
                  {...params}
                  label="Time zone"
                  margin="normal"
                  error={touched.timeZone && errors.timeZone}
                  helperText={errors.timeZone}
                  onBlur={handleBlur}
                />
              )}
            />
          </div>
        </Grid>
        <Grid item xs={12}>
          <Navigation
            activeStep={activeStep}
            steps={steps}
            handleBack={handleBackCustom}
            handleNext={handleNextCustom}
            disabled={(dirty && !isValid) || (!dirty && !validState)}
          />
        </Grid>
      </Grid>
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  collegeWasCreated: state.college.collegeWasCreated,
  collegeKey: state.college.collegeKey,
  webSiteTemplate: state.college.webSiteTemplate,
  contactForm: state.form.contactForm,
  organisationForm: state.form.organisationForm,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  createCollege: (data) => dispatch(createCollege(data)),
  setOrganisationFormValues: (organisationFormData) => dispatch(setOrganisationFormValues(organisationFormData)),
});

export default connect(mapStateToProps, mapDispatchToProps)(OrganisationForm);
