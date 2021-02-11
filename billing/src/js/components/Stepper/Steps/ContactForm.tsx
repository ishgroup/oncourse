/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import { useFormik } from "formik";
import { connect, Dispatch } from "react-redux";
import * as yup from "yup";
import "yup-phone";
import CustomTextField from "../../common/TextField";
import { setContactFormValues } from "../../../redux/actions";
import Navigation from "../Navigations";

const useStyles = makeStyles((theme: any) => ({
  textFieldWrapper: {
    minHeight: "66px"
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
}));

const validationSchema = yup.object({
  userFirstName: yup.string().required("Required"),
  userLastName: yup.string().required("Required"),
  userPhone: yup.string().required("Required").phone(null, null, "Phone number is not valid"),
  userEmail: yup.string().required("Required").email("Email is not valid"),
});

const ContactForm = (props: any) => {
  const classes = useStyles();
  const [ validState, setValidState ] = useState(false);
  const { contactForm, activeStep, steps, handleBack, handleNext, setContactFormValues } = props;

  const { handleSubmit, handleChange, values, errors, isValid, dirty, touched, handleBlur } = useFormik({
    initialValues: contactForm,
    validationSchema,
    onSubmit: values => {
      alert(JSON.stringify(values, null, 2));
    },
  });

  const handleBackCustom = () => {
    setContactFormValues(values);
    handleBack();
  }

  const handleNextCustom = () => {
    setContactFormValues(values);
    handleNext();
  }

  useEffect(() => {
    validationSchema.validate(contactForm).then(() => {
      setValidState(true)
    }).catch(() => {
      setValidState(false)
    })
  }, contactForm)

  return (
    <form onSubmit={handleSubmit}>
      <h2 className={classes.coloredHeaderText}>Your contact details</h2>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="First Name"
          label="First Name"
          id="userFirstName"
          autoFocus={true}
          onChange={handleChange}
          value={values.userFirstName}
          error={touched.userFirstName && errors.userFirstName}
          helperText={errors.userFirstName}
          onBlur={handleBlur}
        />
      </div>
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
      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBackCustom}
        handleNext={handleNextCustom}
        disabled={(dirty && !isValid) || (!dirty && !validState)}
      />
    </form>
  )
};

const mapStateToProps = (state: any) => ({
  contactForm: state.creatingCollege.contactForm,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setContactFormValues: (contactFormData) => dispatch(setContactFormValues(contactFormData)),
});

export default connect(mapStateToProps, mapDispatchToProps)(ContactForm as any);