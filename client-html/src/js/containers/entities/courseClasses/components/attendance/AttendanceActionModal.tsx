/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import { connect } from "react-redux";
import {
  reduxForm, getFormValues, InjectedFormProps
} from "redux-form";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import Grid from "@mui/material/Grid";
import { differenceInMinutes, format } from "date-fns";
import { Typography } from "@mui/material";
import SvgIcon from "@mui/material/SvgIcon";
import LoadingButton from "@mui/lab/LoadingButton";
import Button from "@mui/material/Button";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { State } from "../../../../../reducers/state";
import EditInPlaceField from "../../../../../../ish-ui/formFields/EditInPlaceField";
import { validateMinMaxDate, validateSingleMandatoryField } from "../../../../../common/utils/validation";
import {
  AttandanceChangeType,
  StudentAttendanceExtended
} from "../../../../../model/entities/CourseClass";
import AttendanceIcon from "./AttendanceIcon";
import AttendanceActionsMenu from "./AttendanceActionsMenu";
import { TimetableSession } from "../../../../../model/timetable";
import { formatDurationMinutes } from "../../../../../common/utils/dates/formatString";
import Uneditable from "../../../../../common/components/form/formFields/Uneditable";
import { stubFunction } from "../../../../../common/utils/common";
import { getStudentAttendanceLabel } from "./utils";
import {
  DD_MMM_YYYY_HH_MM_SS,
  III_DD_MMM_YYYY_HH_MM_SS
} from "../../../../../common/utils/dates/format";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";

const getDifferenceInMinutes = (start: string, end: string): number => {
  const startDate = new Date(start);
  const endDate = new Date(end);

  startDate.setSeconds(0, 0);
  endDate.setSeconds(0, 0);

  return differenceInMinutes(endDate, startDate);
};

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
      <Grid item xs={12} className="centeredFlex mt-2 mb-2">
        <SvgIcon fontSize="small">
          <AttendanceIcon type={values.attendanceType} />
        </SvgIcon>
        <AttendanceActionsMenu
          onChange={onAttendanceTypeChange}
          type="Student"
          label="Mark attendance for this student as..."
        />
        <Typography variant="body1">{getStudentAttendanceLabel(values.attendanceType)}</Typography>
      </Grid>
      <Grid item container columnSpacing={3} rowSpacing={2} xs={12} className="pb-2">
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
                      />
        </Grid>
      </Grid>
    </Grid>
  );
};


interface AttendanceActionModalProps {
  changeType: AttandanceChangeType;
  setAttendanceChangeType: (arg: AttandanceChangeType) => void;
  onSubmit: (values: StudentAttendanceExtended) => void;
  sessions: TimetableSession[];
  fetching?: boolean;
  dispatch?: any;
  values?: StudentAttendanceExtended;
}

export const ATTENDANCE_COURSE_CLASS_FORM: string = "AttendanceCourseClassForm";

const AttendanceActionModalForm: React.FC<AttendanceActionModalProps & InjectedFormProps> = ({
  changeType,
  reset,
  setAttendanceChangeType,
  fetching,
  invalid,
  values,
  sessions,
  change,
  handleSubmit,
  onSubmit
}) => {
  const [hasError, setHasError] = useState(false);

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
            disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
    >
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)}>
          <StudentAttendanceContent
            change={change}
            values={values}
            sessionDuration={sessionDuration}
            bindedSession={bindedSession}
            setHasError={setHasError}
          />
        </form>
      </DialogContent>

      <DialogActions className="p-3">
        <Button color="primary" onClick={onClose}>
          Cancel
        </Button>

        <LoadingButton
          onClick={handleSubmit}
          disabled={invalid || hasError}
          loading={fetching}
          variant="contained"
          color="primary"
        >
          OK
        </LoadingButton>
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
