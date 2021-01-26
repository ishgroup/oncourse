/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: any) => ({
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
}));

const FinishPage = () => {
  const classes = useStyles();

  return (
  <div>
    <h2 className={classes.coloredHeaderText}>
      All done
    </h2>
    <p>
      We are creating system for you now
    </p>
    <p>
      You will receive an email with your new account details shortly
    </p>
  </div>
)};

export default FinishPage