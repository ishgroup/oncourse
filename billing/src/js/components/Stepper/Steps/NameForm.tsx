/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from 'react';
import { makeStyles } from "@material-ui/core/styles";
import { Typography } from "@material-ui/core";
import { useFormik } from 'formik';
import CustomTextField from "../../common/TextField";

const useStyles = makeStyles((theme:any) => ({
  textFieldWrapper: {
    minHeight: "61px"
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
}));

const NameForm = () => {
  const classes = useStyles();

  const formik = useFormik({
    initialValues: {
      siteName: '',
    },
    onSubmit: values => {
      alert(JSON.stringify(values, null, 2));
    },
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <h2 className={classes.coloredHeaderText}>Give your site a name</h2>
      <Typography>Choose your name for your team or company</Typography>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          id="standard-basic"
          placeholder="https://_________.oncourse.cc/"
          label="Site Name"
        />
      </div>
      <Typography>No credit card required</Typography>
      <Typography>Free until 1 month, then $10/month</Typography>
    </form>
  )
}

export default NameForm