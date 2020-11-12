/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import IconButton from "@material-ui/core/IconButton";
import withStyles from "@material-ui/core/styles/withStyles";
import { StudentAttendanceExtended, TutorAttendanceExtended } from "../../../../../model/entities/CourseClass";
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

interface TutorAttendanceIconButtonProps {
  attendance: TutorAttendanceExtended;
  onClick: any;
  classes?: any;
}

const TutorAttendanceIconButtonBase: React.FC<TutorAttendanceIconButtonProps> = ({ attendance, classes, onClick }) => {
  const onIconCLick = useCallback(e => onClick(e, attendance.index), [attendance.index]);

  return (
    <IconButton role={attendance.attendanceType} size="small" className={`p-0 ${classes.iconButton}`} onClick={onIconCLick}>
      <AttendanceIcon type={attendance.attendanceType} />
    </IconButton>
  );
};

export const TutorAttendanceIconButton = withStyles(styles)(TutorAttendanceIconButtonBase);

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
