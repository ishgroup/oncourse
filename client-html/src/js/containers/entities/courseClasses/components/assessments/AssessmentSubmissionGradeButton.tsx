/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { ButtonBase } from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import makeStyles from "@material-ui/core/styles/makeStyles";

interface AssessmentSubmissionIconButtonProps {
  onClick?: any;
  grade?: string;
}

const useStyles = makeStyles(() => ({
  iconGrey: {
    color: "#d4d4d4"
  },
  iconButton: {
    "&:hover": {
      background: "none"
    }
  }
}));

const AssessmentSubmissionGradeButton: React.FC<AssessmentSubmissionIconButtonProps> = (
  {
   onClick,
   grade
 }
) => {
  const classes = useStyles();

  return grade ? (
    <ButtonBase className="p-0" onClick={onClick}>
      {grade}
    </ButtonBase>
  ) : (
    <IconButton size="small" className={`p-0 ${classes.iconButton}`} onClick={onClick}>
      <FontAwesomeIcon fixedWidth icon="circle" className={classes.iconGrey} />
    </IconButton>
  );
};

export default AssessmentSubmissionGradeButton;
