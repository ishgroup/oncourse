/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from 'react';
import { makeStyles } from "@material-ui/core/styles";
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

  return (
    <div>
      <h2 className={classes.coloredHeaderText}>Give your site a name</h2>
      <p>Choose your name for your team or company</p>
      <div className={classes.textFieldWrapper}>
        <CustomTextField
          id="standard-basic"
          placeholder="https://_________.oncourse.cc/"
          label="Site Name"
        />
      </div>
      <p>No credit card required</p>
      <p>Free until 1 month, then $10/month</p>
    </div>
  )
}

export default NameForm