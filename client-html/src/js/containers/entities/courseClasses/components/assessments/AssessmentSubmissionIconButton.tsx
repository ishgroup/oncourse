/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import IconButton from "@material-ui/core/IconButton";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { createStringEnum } from "@api/model";
import withStyles from "@material-ui/core/styles/withStyles";
import { StudentAttendanceExtended } from "../../../../../model/entities/CourseClass";

const useStyles = makeStyles(() => ({
  iconRemove: {
    color: "red"
  },
  iconSuccess: {
    color: "green"
  },
  adjustNormal: {
    color: "red"
  },
  iconGrey: {
    color: "#d4d4d4"
  }
}));

export const AssessmentsSubmissionType = [
  "Not submitted",
  "Submitted",
  "Failed submission",
];

interface AssessmentSubmissionIconProps {
  type: AssessmentSubmissionType;
  classes?: any;
}

const AssessmentSubmissionIcon: React.FC<AssessmentSubmissionIconProps> = ({ type }) => {
  const classes = useStyles();

  switch (type) {
    case "Submitted":
      return <FontAwesomeIcon fixedWidth icon="check" className={classes.iconSuccess} />;
    case "Failed submission":
      return <FontAwesomeIcon fixedWidth icon="times" className={classes.iconRemove} />;
    case "Not submitted":
      return <FontAwesomeIcon fixedWidth icon="circle" className={classes.iconGrey} />;
    default:
      return null;
  }
};

const styles = {
  iconButton: {
    "&:hover": {
      background: "none"
    }
  }
};

interface AssessmentSubmissionIconButtonProps {
  status: AssessmentsSubmissionType;
  onClick?: any;
  classes?: any;
}

const AssessmentSubmissionIconButton: React.FC<AssessmentSubmissionIconButtonProps> = ({
   status,
   classes,
   onClick
 }) => (
   <IconButton size="small" className={`p-0 ${classes.iconButton}`} onClick={onClick}>
     <AssessmentSubmissionIcon type={status} />
   </IconButton>
);

export default withStyles(styles)(AssessmentSubmissionIconButton);