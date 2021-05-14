/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Autocomplete } from "@material-ui/lab";
import { useFormik } from "formik";
import * as yup from "yup";
import { connect } from "react-redux";
import CustomTextField from "../../common/TextField";
import { createCollege, setOrganisationFormValues } from "../../../redux/actions";
import Navigation from "../Navigations";
import { countries, countriesTimeZone } from "../../../utils";
import { addEventListenerWithDeps } from "../../Hooks/addEventListnerWithDeps";
import {State} from "../../../redux/reducers";
import {Dispatch} from "redux";

const useStyles = makeStyles((theme: any) => ({
  textFieldWrapper: {
    minHeight: "66px",
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color,
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
    collegeWasCreated,
  } = props;

  useEffect(() => {
    if (collegeWasCreated) handleNext();
  }, [collegeWasCreated])

  const { handleSubmit, handleChange, values, errors, setFieldValue, isValid, touched, dirty, handleBlur } = useFormik({
    initialValues: organisationForm,
    validationSchema,
    onSubmit: values => {},
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
  }

  const keyPressHandler = useCallback((e) => {
    if (e.keyCode === 13 && !((dirty && !isValid) || (!dirty && !validState))) {
      handleNextCustom();
    }
  }, [dirty, isValid, validState])

  addEventListenerWithDeps([keyPressHandler], keyPressHandler);

  useEffect(() => {
    validationSchema.validate(organisationForm).then(() => {
      setValidState(true)
    }).catch(() => {
      setValidState(false)
    })
  }, organisationForm)

  return (
    <form>
      <h2 className={classes.coloredHeaderText}>Organisation</h2>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Name"
          label="Name"
          id="organisationName"
          autoFocus={true}
          onChange={handleChange}
          value={values.organisationName}
          error={touched.organisationName && errors.organisationName}
          helperText={errors.organisationName}
          onBlur={handleBlur}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Company number/ABN"
          label="Company number/ABN"
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
          helperText={"(if different to organisation name)"}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Address"
          label="Address"
          id="address"
          onChange={handleChange}
          value={values.address}
          error={touched.address && errors.address}
          helperText={errors.address}
          onBlur={handleBlur}
        />
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="City | Suburb"
          label="City | Suburb"
          id="suburb"
          onChange={handleChange}
          value={values.suburb}
          error={touched.suburb && errors.suburb}
          helperText={errors.suburb}
          onBlur={handleBlur}
        />
      </div>
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
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          placeholder="Postcode/ZIP"
          label="Postcode/ZIP"
          id="postcode"
          onChange={handleChange}
          value={values.postcode}
          error={touched.postcode && errors.postcode}
          helperText={errors.postcode}
          onBlur={handleBlur}
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
              error={touched.country && errors.country}
              helperText={errors.country}
              onBlur={handleBlur}
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

const mapStateToProps = (state: State) => ({
  collegeWasCreated: state.collegeWasCreated,
  collegeKey: state.collegeKey,
  webSiteTemplate: state.webSiteTemplate,
  contactForm: state.contactForm,
  organisationForm: state.organisationForm,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  createCollege: (data) => dispatch(createCollege(data)),
  setOrganisationFormValues: (organisationFormData) => dispatch(setOrganisationFormValues(organisationFormData)),
});

export default connect(mapStateToProps, mapDispatchToProps)(OrganisationForm);
