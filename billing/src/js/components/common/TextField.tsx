/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { TextField } from "@material-ui/core";
import withStyles from "@material-ui/core/styles/withStyles";

const styles = (theme: any) => ({
  input: {
    height: "36px",
    width: "100%"
  }
});

const CustomTextField = (props: any) => {
  const { classes } = props;

  return (
    <TextField
      className={classes.input}
      {...props}
    />
  )
}

export default withStyles(styles, { withTheme: true })(CustomTextField);