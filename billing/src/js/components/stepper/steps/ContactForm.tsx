/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useState } from 'react';
import { useFormik } from 'formik';
import { connect } from 'react-redux';
import * as yup from 'yup';
import { Dispatch } from 'redux';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { makeAppStyles } from '../../../styles/makeStyles';
import CustomTextField from '../../common/TextField';
import Navigation from '../Navigations';
import { phoneRegExp } from '../../../constant/common';
import { addEventListenerWithDeps } from '../../../hooks/addEventListnerWithDeps';
import { State } from '../../../models/State';
import { setContactFormValues } from '../../../redux/actions/College';

const useStyles = makeAppStyles()((theme: any) => ({
  textFieldWrapper: {
    minHeight: '66px'
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
  subTitle: {
    marginBottom: 30,
  },
  info: {
    position: 'absolute',
    bottom: -70,
    padding: 5,
    backgroundColor: '#fff',
    left: 0,
    right: 0,
    maxWidth: '90%',
    margin: '0 auto',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: 12
  },
}));

const validationSchema = yup.object({
  userFirstName: yup.string().required('Required'),
  userLastName: yup.string().required('Required'),
  userPhone: yup.string().required('Required').matches(phoneRegExp, 'Phone number is not valid'),
  userEmail: yup.string().required('Required').email('Email is not valid'),
});

const ContactForm = (props: any) => {
  const [validState, setValidState] = useState(false);

  const {
    contactForm, activeStep, steps, handleBack, handleNext, setContactFormValues
  } = props;

  const { classes } = useStyles();

  const {
    handleChange, values, errors, isValid, dirty, touched, handleBlur
  } = useFormik({
    initialValues: contactForm,
    validationSchema,
    onSubmit: () => {},
  });

  const handleBackCustom = () => {
    setContactFormValues(values);
    handleBack();
  };

  const handleNextCustom = () => {
    setContactFormValues(values);
    handleNext();
  };

  const keyPressHandler = useCallback((e) => {
    if (e.keyCode === 13 && !((dirty && !isValid) || (!dirty && !validState))) {
      handleNextCustom();
    }
  }, [dirty, isValid, validState]);

  addEventListenerWithDeps([keyPressHandler], keyPressHandler);

  useEffect(() => {
    validationSchema.validate(contactForm).then(() => {
      setValidState(true);
    }).catch(() => {
      setValidState(false);
    });
  }, contactForm);

  return (
    <form>
      <Typography variant="h4" component="h4" className={classes.coloredHeaderText} color="primary" gutterBottom>
        Enter your contact details
      </Typography>
      <Typography variant="subtitle1" gutterBottom className={classes.subTitle}>
        Tell us a bit about yourself
      </Typography>
      <Grid container spacing={5}>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="First Name"
              label="First Name"
              id="userFirstName"
              autoFocus
              onChange={handleChange}
              value={values.userFirstName}
              error={touched.userFirstName && errors.userFirstName}
              helperText={errors.userFirstName}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Last Name"
              label="Last Name"
              id="userLastName"
              onChange={handleChange}
              value={values.userLastName}
              error={touched.userLastName && errors.userLastName}
              helperText={errors.userLastName}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Phone"
              label="Phone"
              id="userPhone"
              onChange={handleChange}
              value={values.userPhone}
              error={touched.userPhone && errors.userPhone}
              helperText={errors.userPhone}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
        <Grid item xs={12} sm={6}>
          <div className={classes.textFieldWrapper}>
            <CustomTextField
              placeholder="Email"
              label="Email"
              id="userEmail"
              onChange={handleChange}
              value={values.userEmail}
              error={touched.userEmail && errors.userEmail}
              helperText={errors.userEmail}
              onBlur={handleBlur}
            />
          </div>
        </Grid>
      </Grid>

      <Typography className={classes.info} component="div">
        <ErrorOutlineIcon fontSize="small" color="primary" />
&nbsp;&nbsp;Don't worry, ish won't share your details or spam you. These details help us personalise your site.
      </Typography>

      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBackCustom}
        handleNext={handleNextCustom}
        disabled={(dirty && !isValid) || (!dirty && !validState)}
      />
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  contactForm: state.form.contactForm,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setContactFormValues: (contactFormData) => dispatch(setContactFormValues(contactFormData)),
});

export default connect(mapStateToProps, mapDispatchToProps)(ContactForm);
