/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from 'react';
import { TextField } from '@mui/material';
import { makeAppStyles } from '../../styles/makeStyles';

const useStyles = makeAppStyles()(() => ({
  input: {
    height: '36px',
    width: '100%',
  }
}));

const CustomTextField = (props) => {
  const { classes } = useStyles();

  const inputProps = {
    autoComplete: 'off',
    type: 'search',
    form: {
      autoComplete: 'off',
    },
  };

  return (
    <TextField
      className={classes.input}
      inputProps={inputProps}
      variant="standard"
      {...props}
    />
  );
};

export default CustomTextField;
