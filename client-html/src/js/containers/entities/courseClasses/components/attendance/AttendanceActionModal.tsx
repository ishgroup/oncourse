/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import { connect } from "react-redux";
import {
  reduxForm, getFormValues, InjectedFormProps, Form
} from "redux-form";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import DialogContent from "@material-ui/core/DialogContent";
import Grid from "@material-ui/core/Grid";
import { differenceInMinutes, format } from "date-fns";
import { Typography } from "@material-ui/core";
import SvgIcon from "@material-ui/core/SvgIcon";
import IconButton from "@material-ui/core/IconButton/IconButton";
import LockOpen from "@material-ui/icons/LockOpen";
import Lock from "@material-ui/icons/Lock";
import { CourseClassTutor } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../reducers/state";
import Button from "../../../../../common/components/buttons/Button";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import { validateMinMaxDate, validateSingleMandatoryField } from "../../../../../common/utils/validation";
import {
  AttandanceChangeType,
  StudentAttendanceExtended,
  TutorAttendanceExtended,
  tutorStatusRoles
} from "../../../../../model/entities/CourseClass";
import AttendanceIcon from "./AttendanceIcon";
import AttendanceActionsMenu from "./AttendanceActionsMenu";
import { TimetableSession } from "../../../../../model/timetable";
import { formatDurationMinutes } from "../../../../../common/utils/dates/formatString";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { stubFunction } from "../../../../../common/utils/common";
import { TutorAttendanceIconButton } from "./AttendanceIconButtons";
import { getStudentAttendanceLabel } from "./utils";
import {
  DD_MMM_YYYY_HH_MM_SS,
  HH_MM_COLONED,
  III_DD_MMM_YYYY_HH_MM_SPECIAL,
  III_DD_MMM_YYYY_HH_MM_SS
} from "../../../../../common/utils/dates/format";
import { normalizeNumber } from "../../../../../common/utils/numbers/numbersNormalizing";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";

interface StudentAttendanceContentProps {
  change: any;
  sessionDuration: number;
  bindedSession: TimetableSession;
  values: StudentAttendanceExtended;
  setHasError: any;
}

const StudentAttendanceContent: React.FC<StudentAttendanceContentProps> = ({
  change,
  sessionDuration,
  values,
  setHasError,
  bindedSession
}) => {
  useEffect(() => {
    if (values.attendanceType === "Partial" && !values.attendedFrom && !values.attendedUntil) {
      change("attendedFrom", new Date(bindedSession.start).toISOString());
      change("attendedUntil", new Date(bindedSession.end).toISOString());
    }
  }, [values.attendanceType]);

  const validateMaxDate = useCallback((value, allValues) => validateMinMaxDate(value, "", allValues.attendedUntil, "", "Attended from date is greater than until date."), []);

  const validateMinDate = useCallback((value, allValues) => validateMinMaxDate(value, allValues.attendedFrom, "", "Attended until date is less than from date.", ""), []);

  const onAttendanceTypeChange = useCallback(value => {
    change("attendanceType", value);

    if (value !== "Partial") {
      change("attendedFrom", null);
      change("attendedUntil", null);
    }
  }, []);

  const attendanceDuration = useMemo(() => {
    if (values.attendedFrom && values.attendedUntil) {
      return getDifferenceInMinutes(values.attendedFrom, values.attendedUntil);
    }
    return sessionDuration;
  }, [values.attendedFrom, values.attendedUntil, sessionDuration]);

  const attendanceDurationError = useMemo(() => {
    const error = attendanceDuration > sessionDuration
      ? "Attendance duration cantnot be greater than session duration."
      : undefined;

    setHasError(Boolean(error));
    return error;
  }, [attendanceDuration, sessionDuration]);

  const sessionDurationLabel = useMemo(() => formatDurationMinutes(sessionDuration), [sessionDuration]);

  const attendanceDurationLabel = useMemo(() => formatDurationMinutes(attendanceDuration || 0), [attendanceDuration]);

  const attendedFromLabel = useMemo(
    () => format(
      bindedSession.siteTimezone
        ? appendTimezone(new Date(bindedSession.start), bindedSession.siteTimezone)
        : new Date(bindedSession.start),
      III_DD_MMM_YYYY_HH_MM_SS
    ),
    [bindedSession.start, bindedSession.siteTimezone]
  );

  const attendedUntilLabel = useMemo(
    () => format(
      bindedSession.siteTimezone
        ? appendTimezone(new Date(bindedSession.end), bindedSession.siteTimezone)
        : new Date(bindedSession.end),
      III_DD_MMM_YYYY_HH_MM_SS
    ),
    [bindedSession.end, bindedSession.siteTimezone]
  );

  return (
    <Grid container>
      <Grid item xs={12}>
        <div className="centeredFlex mt-2 mb-2">
          <SvgIcon fontSize="small">
            <AttendanceIcon type={values.attendanceType} />
          </SvgIcon>
          <AttendanceActionsMenu
            onChange={onAttendanceTypeChange}
            type="Student"
            label="Mark attendance for this student as..."
          />
          <Typography variant="body1">{getStudentAttendanceLabel(values.attendanceType)}</Typography>
        </div>
        <div className="pb-2">
          <Grid container>
            <Grid item xs={6}>
              {values.attendanceType === "Partial" ? (
                <FormField
                  type="dateTime"
                  name="attendedFrom"
                  label="Attended from"
                  validate={validateMaxDate}
                  timezone={bindedSession.siteTimezone}
                  formatDateTime={DD_MMM_YYYY_HH_MM_SS}
                />
              ) : (
                <Uneditable label="Attended from" value={attendedFromLabel} />
              )}
            </Grid>
            <Grid item xs={6}>
              {values.attendanceType === "Partial" ? (
                <FormField
                  type="dateTime"
                  name="attendedUntil"
                  label="Attended until"
                  validate={validateMinDate}
                  timezone={bindedSession.siteTimezone}
                  formatDateTime={DD_MMM_YYYY_HH_MM_SS}
                />
              ) : (
                <Uneditable label="Attended until" value={attendedUntilLabel} />
              )}
            </Grid>
            <Grid item xs={6}>
              <EditInPlaceField
                label="Attendance duration"
                input={{
                  onChange: stubFunction,
                  onFocus: stubFunction,
                  onBlur: stubFunction,
                  value: attendanceDurationLabel
                }}
                meta={{
                  error: attendanceDurationError,
                  invalid: Boolean(attendanceDurationError)
                }}
                disabled
              />
            </Grid>
            <Grid item xs={6}>
              <Uneditable value={sessionDurationLabel} label="Session duration" />
            </Grid>
            <Grid item xs={12}>
              <FormField
                type="multilineText"
                name="note"
                label="Note"
                validate={values.attendanceType === "Absent with reason" ? validateSingleMandatoryField : undefined}
                fullWidth
              />
            </Grid>
          </Grid>
        </div>
      </Grid>
    </Grid>
  );
};

interface TutorAttendanceContentProps {
  change: any;
  sessionDuration: number;
  values: TutorAttendanceExtended;
  setHasError: any;
  bindedSession: TimetableSession;
  tutors: CourseClassTutor[];
}

const TutorAttendanceContent: React.FC<TutorAttendanceContentProps> = ({
  change,
  sessionDuration,
  bindedSession,
  values,
  tutors
}) => {
  const [durationLocked, setDurationLocked] = useState(
    !(typeof values.durationMinutes === "number" && values.attendanceType === "Confirmed for payroll")
  );

  const onIconClick = useCallback(e => {
    const roleIndex = tutorStatusRoles.indexOf(e.currentTarget.getAttribute("role"));
    change("attendanceType", [2, -1].includes(roleIndex) ? tutorStatusRoles[0] : tutorStatusRoles[roleIndex + 1]);
  }, []);

  const onLockClick = useCallback(() => {
    setDurationLocked(prev => {
      change("durationMinutes", prev ? sessionDuration : null);
      return !prev;
    });
  }, [sessionDuration]);

  const sessionDurationLabel = useMemo(() => formatDurationMinutes(sessionDuration), [sessionDuration]);

  const sessionDateTime = useMemo(
    () => (bindedSession.siteTimezone
      ? format(
        appendTimezone(new Date(bindedSession.start), bindedSession.siteTimezone),
        III_DD_MMM_YYYY_HH_MM_SPECIAL
      ) + ` to ${format(appendTimezone(new Date(bindedSession.end), bindedSession.siteTimezone), HH_MM_COLONED)}`
      : format(new Date(bindedSession.start), III_DD_MMM_YYYY_HH_MM_SPECIAL)
          + ` to ${format(new Date(bindedSession.end), HH_MM_COLONED)}`),
    [values.sessionId, bindedSession.siteTimezone]
  );

  const tutorContactLink = useMemo(() => `/contact/${tutors.find(t => t.id === values.courseClassTutorId).contactId}`, [
    tutors.length,
    values.courseClassTutorId
  ]);

  return (
    <Grid container className="pb-2">
      <Grid item xs={6}>
        <Uneditable value={values.contactName} label="Tutor" url={tutorContactLink} />
      </Grid>
      <Grid item xs={6}>
        <div className="centeredFlex mt-2 mb-2">
          <TutorAttendanceIconButton attendance={values} onClick={onIconClick} />
          <Typography variant="body1" className="pl-1">
            {values.attendanceType}
          </Typography>
        </div>
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={sessionDateTime} label="Session" />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={sessionDurationLabel} label="Session duration" />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={sessionDuration} label="Scheduled payable time (minutes)" />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="number"
          name="durationMinutes"
          label="Actual payable time (minutes)"
          normalize={normalizeNumber}
          defaultValue={values.attendanceType === "Rejected for payroll" ? "0" : sessionDuration}
          labelAdornment={(
            <span>
              <IconButton
                className="inputAdornmentButton"
                disabled={values.attendanceType !== "Confirmed for payroll"}
                onClick={onLockClick}
              >
                {!durationLocked && <LockOpen className="inputAdornmentIcon" />}
                {durationLocked && <Lock className="inputAdornmentIcon" />}
              </IconButton>
            </span>
          )}
          disabled={durationLocked}
        />
      </Grid>
      <Grid item xs={12}>
        <FormField type="multilineText" name="note" label="Notes" fullWidth />
      </Grid>
    </Grid>
  );
};

interface AttendanceActionModalProps {
  changeType: AttandanceChangeType;
  setAttendanceChangeType: (arg: AttandanceChangeType) => void;
  onSubmit: (values: StudentAttendanceExtended) => void;
  sessions: TimetableSession[];
  tutors: CourseClassTutor[];
  fetching?: boolean;
  dispatch?: any;
  values?: StudentAttendanceExtended & TutorAttendanceExtended;
}

export const ATTENDANCE_COURSE_CLASS_FORM: string = "AttendanceCourseClassForm";

const getDifferenceInMinutes = (start: string, end: string): number => {
  const startDate = new Date(start);
  const endDate = new Date(end);

  startDate.setSeconds(0, 0);
  endDate.setSeconds(0, 0);

  return differenceInMinutes(endDate, startDate);
};

const AttendanceActionModalForm: React.FC<AttendanceActionModalProps & InjectedFormProps> = ({
  changeType,
  reset,
  setAttendanceChangeType,
  fetching,
  invalid,
  values,
  sessions,
  change,
  tutors,
  handleSubmit,
  onSubmit
}) => {
  const [hasError, setHasError] = useState(false);

  const isStudent = useMemo(() => values.hasOwnProperty("attendedFrom"), [values.attendedFrom]);
  const isTutor = useMemo(() => values.hasOwnProperty("courseClassTutorId"), [values.courseClassTutorId]);

  const onClose = useCallback(() => {
    setAttendanceChangeType(null);
    reset();
  }, []);

  const bindedSession = useMemo(() => sessions.find(s => s.id === values.sessionId), [values.sessionId]);

  const sessionDuration = useMemo(() => {
    if (bindedSession) {
      return getDifferenceInMinutes(bindedSession.start, bindedSession.end);
    }
    return 0;
  }, [bindedSession]);

  return (
    <Dialog
      open={Boolean(changeType)}
      onClose={onClose}
      classes={{
        paper: "overflow-visible"
      }}
      fullWidth
      disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
    >
      <DialogContent>
        <Form onSubmit={handleSubmit(onSubmit)}>
          {isStudent && (
            <StudentAttendanceContent
              change={change}
              values={values}
              sessionDuration={sessionDuration}
              bindedSession={bindedSession}
              setHasError={setHasError}
            />
          )}
          {isTutor && (
            <TutorAttendanceContent
              change={change}
              values={values}
              tutors={tutors}
              sessionDuration={sessionDuration}
              bindedSession={bindedSession}
              setHasError={setHasError}
            />
          )}
        </Form>
      </DialogContent>

      <DialogActions className="p-3">
        <MuiButton color="primary" onClick={onClose}>
          Cancel
        </MuiButton>

        <Button
          onClick={handleSubmit}
          disabled={invalid || hasError}
          loading={fetching}
          variant="contained"
          color="primary"
        >
          OK
        </Button>
      </DialogActions>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(ATTENDANCE_COURSE_CLASS_FORM)(state)
});

export default reduxForm<any, AttendanceActionModalProps>({
  form: ATTENDANCE_COURSE_CLASS_FORM,
  initialValues: {}
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )((props: any) => (props.values ? <AttendanceActionModalForm {...props} /> : null))
);
