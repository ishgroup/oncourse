/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import IconButton from "@mui/material/IconButton";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import withStyles from "@mui/styles/withStyles";
import { StringValueType } from "../../../../../model/common/CommomObjects";
import { makeAppStyles } from "../../../../../common/styles/makeStyles";

const useStyles = makeAppStyles(() => ({
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

export const AssessmentsSubmissionTypes = [
  "Not submitted",
  "Submitted"
];

export type AssessmentsSubmissionType = StringValueType<typeof AssessmentsSubmissionTypes>;

interface AssessmentSubmissionIconProps {
  type: AssessmentsSubmissionType;
  classes?: any;
}

const AssessmentSubmissionIcon: React.FC<AssessmentSubmissionIconProps> = ({ type }) => {
  const classes  = useStyles();

  switch (type) {
    case "Submitted":
      return <FontAwesomeIcon fixedWidth icon="check" className={classes.iconSuccess} />;
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
