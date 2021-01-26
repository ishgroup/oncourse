/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import CustomTextField from "../../common/TextField";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles((theme: any) => ({
  textFieldWrapper: {
    minHeight: "61px"
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
}));

const OrganisationForm = () => {
  const classes = useStyles();

  return (
    <>
      <h2 className={classes.coloredHeaderText}>Organisation</h2>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Name" label="Name"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="*ABN" label="*ABN"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Trading Name" label="Trading Name"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Address" label="Address"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="City | Suburb" label="City | Suburb"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="State" label="State"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Postcode/ZIP" label="Postcode/ZIP"/>
      </div>
      <div className={classes.textFieldWrapper}>
        <CustomTextField placeholder="Country" label="Country"/>
      </div>
    </>
  )
}

export default OrganisationForm;