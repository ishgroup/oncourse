/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { ButtonBase, IconButton, Typography } from '@mui/material';
import { makeAppStyles } from 'ish-ui';
import React from 'react';

interface AssessmentSubmissionIconButtonProps {
  onClick?: any;
  grade?: string;
}

const useStyles = makeAppStyles()(() => ({
  iconGrey: {
    color: "#d4d4d4"
  },
  iconButton: {
    "&:hover": {
      background: "none"
    }
  }
}));

const GradeButton: React.FC<AssessmentSubmissionIconButtonProps> = (
  {
   onClick,
   grade
 }
) => {
  const { classes }  = useStyles();

  return grade ? (
    <ButtonBase className="p-0" onClick={onClick}>
      <Typography variant="body2">
        {grade}
      </Typography>
    </ButtonBase>
  ) : (
    <IconButton size="small" className={`p-0 ${classes.iconButton}`} onClick={onClick}>
      <FontAwesomeIcon fixedWidth icon="circle" className={classes.iconGrey} />
    </IconButton>
  );
};

export default GradeButton;
