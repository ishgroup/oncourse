/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import CustomTextField from "../../common/TextField";

const useStyles = makeStyles((theme: any) => ({
  textFieldWrapper: {
    minHeight: "61px"
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
}));

const ContactForm = () => {
  const classes = useStyles();

  return (
    <form>
      <h2 className={classes.coloredHeaderText}>Your contact details</h2>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="First Name" label="First Name"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Last Name" label="Last Name"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Phone" label="Phone"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Email" label="Email"/>
      </div>
    </form>
  )
};

export default ContactForm;