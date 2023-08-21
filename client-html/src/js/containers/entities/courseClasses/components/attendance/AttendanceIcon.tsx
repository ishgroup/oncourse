/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AttendanceType, TutorAttendanceType } from "@api/model";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { createStyles, withStyles } from "@mui/styles";
import React from "react";

const styles = () =>
  createStyles({
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
  });

interface AttendanceIconProps {
  type: AttendanceType | TutorAttendanceType;
  classes?: any;
}

const AttendanceIcon: React.FC<AttendanceIconProps> = ({ type, classes }) => {
  switch (type) {
    case "Confirmed for payroll":
    case "Attended":
      return <FontAwesomeIcon fixedWidth={true} icon="check" className={classes.iconSuccess} />;
    case "Absent with reason":
      return <FontAwesomeIcon fixedWidth={true} icon="adjust" className={classes.adjustNormal} />;
    case "Rejected for payroll":
    case "Absent without reason":
      return <FontAwesomeIcon fixedWidth={true} icon="times" className={classes.iconRemove} />;
    case "Partial":
      return <FontAwesomeIcon fixedWidth={true} icon="adjust" flip="horizontal" className={classes.iconSuccess} />;
    case "Not confirmed for payroll":
    case "Unmarked":
      return <FontAwesomeIcon fixedWidth={true} icon="circle" className={classes.iconGrey} />;
    default:
      return null;
  }
};

export default withStyles(styles)(AttendanceIcon);
