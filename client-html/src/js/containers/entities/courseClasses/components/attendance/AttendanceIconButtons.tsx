/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import IconButton from "@mui/material/IconButton";
import withStyles from "@mui/styles/withStyles";
import React, { useMemo } from "react";
import { StudentAttendanceExtended } from "../../../../../model/entities/CourseClass";
import AttendanceIcon from "./AttendanceIcon";

const styles = {
  iconButton: {
    "&:hover": {
      background: "none"
    }
  }
};

interface StudentAttendanceIconButtonProps {
  attendance: StudentAttendanceExtended;
  onClick?: any;
  classes?: any;
}
const StudentAttendanceIconButtonBase: React.FC<StudentAttendanceIconButtonProps> = ({
  attendance,
  classes,
  onClick
}) => (
  <IconButton role={attendance.attendanceType} size="small" className={`p-0 ${classes.iconButton}`} onClick={onClick}>
    <AttendanceIcon type={attendance.attendanceType} />
  </IconButton>
  );

export const StudentAttendanceIconButton = withStyles(styles)(StudentAttendanceIconButtonBase);

interface TrainingPlanIconButtonProps {
  attended: boolean;
  onClick: any;
  classes?: any;
}

const TrainingPlanIconButtonBase: React.FC<TrainingPlanIconButtonProps> = ({ attended, classes, onClick }) => {
  const type = useMemo(() => (attended ? "Attended" : "Unmarked"), [attended]);

  return (
    <IconButton role={type} size="small" className={`p-0 ${classes.iconButton}`} onClick={onClick}>
      <AttendanceIcon type={type} />
    </IconButton>
  );
};

export const TrainingPlanIconButton = withStyles(styles)(TrainingPlanIconButtonBase);
