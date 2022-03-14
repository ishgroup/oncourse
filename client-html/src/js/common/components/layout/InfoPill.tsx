/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Chip } from "@mui/material";
import React from "react";
import { alpha } from "@mui/material/styles";
import { makeAppStyles } from "../../styles/makeStyles";

const useStyles = makeAppStyles(theme => ({
  chip: {
    textTransform: "uppercase",
    fontWeight: 600,
    fontSize: "12px",
    marginLeft: theme.spacing(1),
    color: theme.palette.success.light,
    backgroundColor: alpha(theme.palette.success.light, 0.15)
  }
}));

const InfoPill = ({ label }) => {
  const classes = useStyles();
  
  return <Chip className={classes.chip} label={label} size="small" />;
};

export default InfoPill;