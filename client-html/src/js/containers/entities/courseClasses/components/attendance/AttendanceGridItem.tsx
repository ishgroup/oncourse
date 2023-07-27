/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { change, initialize } from "redux-form";
import clsx from "clsx";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { withStyles, createStyles } from "@mui/styles";
import IconButton from "@mui/material/IconButton";
import Message from "@mui/icons-material/Message";
import { AttendanceType } from "@api/model";
import { differenceInMinutes, format } from "date-fns";
import Decimal from "decimal.js-light";
import {
  AssessmentClassExtended,
  AttandanceChangeType,
  AttandanceStepItem,
  AttendanceGridType,
  ContactAttendanceItem,
  StudentAttendanceExtended
} from "../../../../../model/entities/CourseClass";
import AttendanceActionsMenu from "./AttendanceActionsMenu";
import { ATTENDANCE_COURSE_CLASS_FORM } from "./AttendanceActionModal";
import {
  StudentAttendanceIconButton,
  TrainingPlanIconButton
} from "./AttendanceIconButtons";
import { TimetableSession } from "../../../../../model/timetable";
import { D_MMM } from  "ish-ui";

const styles = () =>
  createStyles({
    sessionItem: {
      maxWidth: "12.3%",
      "& > span:hover .invisible": {
        visibility: "visible"
      }
    },
    nameLabel: {
      lineHeight: 2
    },
    name: {
      "&:hover .invisible": {
        visibility: "visible"
      }
    },
    iconClipboard: {
      fontSize: "0.9rem"
    },
    iconButton: {
      "&:hover": {
        background: "none"
      }
    }
  });

interface AttendanceGridItemProps {
  item: ContactAttendanceItem;
  type: AttendanceGridType;
  selectedItems: AttandanceStepItem[];
  setAttendanceChangeType: (arg: AttandanceChangeType) => void;
  validateAttendanceUpdate: (updated, type: AttendanceGridType) => void;
  dispatch: any;
  form: string;
  sessions?: TimetableSession[];
  assessments?: AssessmentClassExtended[];
  classes?: any;
  changeAllItems?: any;
  onStudentIconClick?: (e, index: number) => void;
  onTutorIconClick?: (e, attendance: any) => void;
  checkAnimationClass?: () => any;
}
const getMenuLabelByType = (type: AttendanceGridType) => {
  switch (type) {
    case "Student":
      return "Mark ALL sessions for this student as...";
    case "Training plan":
      return "Mark ALL sessions and tasks for this module as...";
  }
};

const getAttendanceDurationByType = (item: StudentAttendanceExtended, sessionDuration: number) => {
  switch (item.attendanceType) {
    case "Absent with reason":
    case "Attended": {
      return sessionDuration;
    }
    case "Partial": {
      return differenceInMinutes(new Date(item.attendedFrom), new Date(item.attendedUntil));
    }
    case "Absent without reason":
      return 0;
    default:
      throw Error(`Unknown attendanceType ${item.attendanceType}`);
  }
};

const AttendanceGridItem: React.FC<AttendanceGridItemProps> = ({
  item,
  type,
  selectedItems,
  classes,
  dispatch,
  form,
  setAttendanceChangeType,
  changeAllItems,
  validateAttendanceUpdate,
  onStudentIconClick,
  sessions,
  assessments,
  checkAnimationClass
}) => {
  const editAttendance = useCallback(
    attendance => {
      dispatch(initialize(ATTENDANCE_COURSE_CLASS_FORM, attendance));
      if (type === "Student") {
        setAttendanceChangeType("singleStudent");
      }
    },
    [type]
  );

  const changeGridRow = useCallback(
    (attendanceType: AttendanceType) => {
      switch (attendanceType) {
        case "Partial":
        case "Absent with reason": {
          dispatch(initialize(ATTENDANCE_COURSE_CLASS_FORM, { ...item.attendances[0], attendanceType }));
          setAttendanceChangeType("byStudent");
          break;
        }
        default: {
          if (type === "Student") {
            const updated = [];

            item.attendances.forEach(s => {
              const updatedItem = { ...s, attendanceType };
              dispatch(change(form, `studentAttendance[${s.index}]`, updatedItem));
              updated.push(updatedItem);
            });

            validateAttendanceUpdate(updated, "Student");

            return;
          }

            changeAllItems(attendanceType, item.attendances[0].index);
        }
      }
    },
    [item.attendances, type]
  );

  const onTrainingPlanIconClick = useCallback(
    (e, s: AttandanceStepItem) => {
      const type = e.currentTarget.getAttribute("role");
      const sessionIds = [...item.attendances[0].sessionIds];
      const assessmentIds = [...item.attendances[0].assessmentIds];

      if (type === "Attended") {
        const sessionIndex = item.attendances[0].sessionIds.findIndex(id => id === s.id);
        const assessmentIndex = item.attendances[0].assessmentIds.findIndex(id => id === s.id);
        if (sessionIndex > -1) {
          sessionIds.splice(sessionIndex, 1);
        }
        if (assessmentIndex > -1) {
          assessmentIds.splice(assessmentIndex, 1);
        }
      } else {
        if (s.start) {
          sessionIds.push(s.id);
        }
        if (s.dueDate) {
          assessmentIds.push(s.id);
        }
      }

      dispatch(change(form, `trainingPlan[${item.attendances[0].index}].sessionIds`, sessionIds));
      dispatch(change(form, `trainingPlan[${item.attendances[0].index}].assessmentIds`, assessmentIds));

      validateAttendanceUpdate([{ ...item.attendances[0], sessionIds, assessmentIds }], "Training plan");
    },
    [item]
  );

  const renderedItems = useMemo(
    () =>
      selectedItems.map((s, index) => {
        if (type === "Training plan") {
          return (
            <Grid key={index} item xs={2} className={classes.sessionItem}>
              <TrainingPlanIconButton
                attended={
                  item.attendances[0].sessionIds.includes(s.id) || item.attendances[0].assessmentIds.includes(s.id)
                }
                onClick={e => onTrainingPlanIconClick(e, s)}
              />
            </Grid>
          );
        }

        const attendance = item.attendances.find(a => a.sessionId === s.id);

        return attendance ? (
          <Grid
            key={attendance.id}
            item
            xs={2}
            className={classes.sessionItem}
          >
            <span>
              {type === "Student" && (
                <StudentAttendanceIconButton
                  attendance={attendance}
                  onClick={e => onStudentIconClick(e, attendance.index)}
                />
              )}

              <IconButton
                size="small"
                disableFocusRipple
                disableRipple
                className={clsx(classes.iconButton, !attendance.note && "invisible")}
                onClick={() => editAttendance(attendance)}
              >
                <Message className={clsx("d-inline-block warningColor", classes.iconClipboard)} />
              </IconButton>
            </span>
          </Grid>
        ) : (
          <Grid key={index} item xs={2} className={classes.sessionItem} />
        );
      }),
    [selectedItems, item]
  );

  const actionsMenuLabel = useMemo(() => getMenuLabelByType(type), [type]);

  const attendancePeriod = useMemo(() => {
    if (type !== "Training plan") {
      return null;
    }

    let attended = [];

    if (sessions) {
      attended = [...attended, ...sessions.filter(s => item.attendances[0].sessionIds.includes(s.id))];
    }

    if (assessments) {
      attended = [...attended, ...assessments.filter(a => item.attendances[0].assessmentIds.includes(a.id))];
    }

    if (attended.length) {
      let earliestLabel;

      if (attended.length === 1) {
        earliestLabel = format(new Date(attended[0].start || attended[0].dueDate), D_MMM);
      } else {
        let earliestDate = new Date(attended[0].start || attended[0].dueDate);
        let latestDate = earliestDate;

        attended.forEach(s => {
          const startDate = new Date(s.start || s.dueDate);
          if (startDate < earliestDate) {
            earliestDate = startDate;
          }
          if (startDate > latestDate) {
            latestDate = startDate;
          }
        });

        earliestLabel = `${format(earliestDate, D_MMM)} - ${format(latestDate, D_MMM)}`;
      }

      return (
        <Typography variant="caption" color="textSecondary" component="div">
          {earliestLabel}
        </Typography>
      );
    }

    return null;
  }, [type, sessions, assessments, item.attendances[0]]);

  const attendancePercent = useMemo(() => {
    if (type !== "Student") {
      return null;
    }

    const now = new Date();

    let markedSessionsTime = 0;
    let attendedTime = 0;

    item.attendances.forEach(a => {
      if (a.attendanceType === "Unmarked") {
        return;
      }

      const attendetSession = sessions.find(s => s.id === a.sessionId);

      if (!attendetSession) {
        return;
      }

      const attendetSessionEnd = new Date(attendetSession.end);

      if (attendetSessionEnd >= now) {
        return;
      }

      const sessionDuration = differenceInMinutes(new Date(attendetSession.start), attendetSessionEnd);

      markedSessionsTime += sessionDuration;
      attendedTime += getAttendanceDurationByType(a, sessionDuration);
    });

    const totalPercent = new Decimal(attendedTime).div(markedSessionsTime / 100).toDecimalPlaces(0, Decimal.ROUND_DOWN);

    return ` (${totalPercent}%)`;
  }, [type, sessions, item.attendances]);

  const attendanceLeftLabel = useMemo(() => {
    if (type === "Training plan") {
      return (
        <div>
          <Typography variant="body2" className="mb-0-5">
            {item.title}
            {' '}
            {attendancePercent}
          </Typography>
          <div className="d-flex align-items-center">
            <Typography variant="caption" color="textSecondary" component="div" className="mr-0-5">
              {item.name}
            </Typography>
            {attendancePeriod}
          </div>
        </div>
      );
    }

    return (
      <div>
        <Typography variant="body2" className={classes.nameLabel}>
          {item.name}
          {' '}
          {attendancePercent}
        </Typography>
        {attendancePeriod}
      </div>
    );
  }, [type, attendancePercent, attendancePeriod]);

  return (
    <Grid container className="align-items-center">
      <Grid item xs={3}>
        <div className={clsx("pt-0-5 pb-0-5 pl-1 pr-1 d-inline-flex-center", classes.name)}>
          {attendanceLeftLabel}

          <AttendanceActionsMenu className="invisible" onChange={changeGridRow} label={actionsMenuLabel} type={type} />
        </div>
      </Grid>
      <Grid item xs={9}>
        <Grid container>
          <Grid item xs={10} className="overflow-hidden">
            <Grid container className={clsx(checkAnimationClass())}>
              {renderedItems}
            </Grid>
          </Grid>
          <Grid item xs={2} />
        </Grid>
      </Grid>
    </Grid>
  );
};

export default withStyles(styles)(AttendanceGridItem);
