/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Autocomplete } from "@material-ui/lab";
import { useFormik } from "formik";
import * as yup from "yup";
import { connect, Dispatch } from "react-redux";
import CustomTextField from "../../common/TextField";
import { createCollege, setOrganisationFormValues } from "../../../redux/actions";
import Navigation from "../Navigations";
import { countries, countriesTimeZone } from "../../../utils";

const useStyles = makeStyles((theme: any) => ({
  textFieldWrapper: {
    minHeight: "61px"
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
}));

const validationSchema = yup.object({
  organisationName: yup.string().required("Required"),
  address: yup.string().required("Required"),
  suburb: yup.string().required("Required"),
  state: yup.string().required("Required"),
  postcode: yup.string().required("Required"),
  country: yup.string().required("Required").nullable(),
  timeZone: yup.string().required("Required").nullable(),
});

const OrganisationForm = (props: any) => {
  const classes = useStyles();
  const [ validState, setValidState ] = useState(false);
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
  } = props;

  const { handleSubmit, handleChange, values, errors, setFieldValue, isValid, dirty } = useFormik({
    initialValues: organisationForm,
    validationSchema,
    onSubmit: values => {
      alert(JSON.stringify(values, null, 2));
    },
  });

  const handleBackCustom = () => {
    setOrganisationFormValues(values);
    handleBack();
  }

  const handleNextCustom = () => {
    setOrganisationFormValues(values);
    createCollege({
      collegeKey,
      webSiteTemplate,
      ...contactForm,
      ...values,
    })
    // handleNext();
  }

  useEffect(() => {
    validationSchema.validate(organisationForm).then(() => {
      setValidState(true)
    }).catch(() => {
      setValidState(false)
    })
  }, organisationForm)

  return (
    <form onSubmit={handleSubmit}>
      <h2 className={classes.coloredHeaderText}>Organisation</h2>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Name"
          label="Name"
          id="organisationName"
          onChange={handleChange}
          value={values.organisationName}
          error={errors.organisationName}
          helperText={errors.organisationName}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="*ABN"
          label="*ABN"
          id="abn"
          onChange={handleChange}
          value={values.abn}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Trading Name"
          label="Trading Name"
          id="tradingName"
          onChange={handleChange}
          value={values.tradingName}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Address"
          label="Address"
          id="address"
          onChange={handleChange}
          value={values.address}
          error={errors.address}
          helperText={errors.address}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="City | Suburb"
          label="City | Suburb"
          id="suburb"
          onChange={handleChange}
          value={values.suburb}
          error={errors.suburb}
          helperText={errors.suburb}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="State"
          label="State"
          id="state"
          onChange={handleChange}
          value={values.state}
          error={errors.state}
          helperText={errors.state}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Postcode/ZIP"
          label="Postcode/ZIP"
          id="postcode"
          onChange={handleChange}
          value={values.postcode}
          error={errors.postcode}
          helperText={errors.postcode}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <Autocomplete
          id="country"
          options={countries}
          getOptionLabel={(option: string) => option}
          onChange={ (_, value) => setFieldValue("country", value) }
          value={values.country}
          renderInput={(params) => (
            <CustomTextField
              {...params}
              label="Country"
              margin="normal"
              error={errors.country}
              helperText={errors.country}
            />
          )}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <Autocomplete
          id="timeZone"
          options={countriesTimeZone}
          getOptionLabel={(option: string) => option}
          onChange={ (_, value) => setFieldValue("timeZone", value) }
          value={values.countriesTimeZone}
          renderInput={(params) => (
            <CustomTextField
              {...params}
              label="Time zone"
              margin="normal"
              error={errors.timeZone}
              helperText={errors.timeZone}
            />
          )}
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
}

const mapStateToProps = (state: any) => ({
  collegeKey: state.creatingCollege.collegeKey,
  webSiteTemplate: state.creatingCollege.webSiteTemplate,
  contactForm: state.creatingCollege.contactForm,
  organisationForm: state.creatingCollege.organisationForm,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  createCollege: (data) => dispatch(createCollege(data)),
  setOrganisationFormValues: (organisationFormData) => dispatch(setOrganisationFormValues(organisationFormData)),
});

export default connect(mapStateToProps, mapDispatchToProps)(OrganisationForm as any);