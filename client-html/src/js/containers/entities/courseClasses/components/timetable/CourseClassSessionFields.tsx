/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useRef
} from "react";
import Grid from "@mui/material/Grid";
import FormGroup from "@mui/material/FormGroup";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import {
  arrayPush, arrayRemove, change, Field, formValueSelector
} from "redux-form";
import {
  addMinutes, differenceInMinutes, set, setDate
} from "date-fns";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  ClashType, SessionWarning, Site, TutorAttendance,
} from "@api/model";
import { FormControl, FormHelperText } from "@mui/material";
import clsx from "clsx";
import ErrorMessage from "../../../../../common/components/form/fieldMessage/ErrorMessage";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { greaterThanNullValidation } from "../../../../../common/utils/validation";
import EditInPlaceDurationField from "../../../../../common/components/form/formFields/EditInPlaceDurationField";
import { stubFunction } from "../../../../../common/utils/common";
import { State } from "../../../../../reducers/state";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import { openRoomLink } from "../../../rooms/utils";
import { TimetableSession } from "../../../../../model/timetable";
import { CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import CourseClassTutorRooster from "./CourseClassTutorRooster";
import { setShiftedTutorAttendances } from "../../utils";

interface Props {
  form: string;
  index: number;
  dispatch: Dispatch;
  tutors: CourseClassTutorExtended[];
  session?: TimetableSession;
  sites?: Site[];
  triggerDebounseUpdate?: any;
  warnings: SessionWarning[];
}

const roomLabel = room => {
  if (room["site.name"]) return `${room["site.name"]} - ${room.name}`;

  return room.name;
};

const validateDuration = value => (value < 5 || value > 1440
    ? "Each entry in the timetable cannot be shorter than 5 minutes or longer than 24 hours."
    : undefined);

const CourseClassSessionFields: React.FC<Props> = ({
  form,
  dispatch,
  session,
  tutors,
  triggerDebounseUpdate,
  warnings
}) => {
  const isMounted = useRef(false);

  useEffect(() => {
    if (isMounted.current && session.start) {
      triggerDebounseUpdate(session);
    } else {
      isMounted.current = true;
    }
  }, [
    session.siteId,
    session.roomId,
    session.start,
    session.end,
    session.tutorAttendances,
    session.privateNotes,
    session.publicNotes
  ]);

  const warningTypes = useMemo<{ [P in ClashType]: SessionWarning[] }>(() => {
    const types = {
      Tutor: [],
      Room: [],
      Site: [],
      Session: [],
      UnavailableRule: []
    };
    warnings.forEach(w => {
      types[w.type].push(w);
    });
    return types;
  }, [warnings]);

  const shiftSessionDates = (durationMinutes: number, sessionStart: string) => {
    const startDate = new Date(sessionStart);
    const endDate = addMinutes(startDate, durationMinutes);

    dispatch(
      change(
        form,
        `sessions[${session.index}].end`,
        endDate.toISOString()
      )
    );

    const updatedSession = {
      ...session,
      start: startDate.toISOString(),
      end: endDate.toISOString()
    };

    setShiftedTutorAttendances(session, updatedSession);

    dispatch(
      change(form, `sessions[${session.index}].tutorAttendances`, updatedSession.tutorAttendances)
    );
  };

  const durationValue = useMemo(() => {
    const startDate = new Date(session.start);
    const endDate = new Date(session.end);

    startDate.setSeconds(0, 0);
    endDate.setSeconds(0, 0);

    return differenceInMinutes(endDate, startDate);
  }, [session.start, session.end]);

  const durationError = useMemo(() => greaterThanNullValidation(durationValue) || validateDuration(durationValue), [
    durationValue
  ]);

  const validateSessionEnd = useCallback(() => durationError, [durationError]);

  const onDurationChange = (durationMinutes: number) => {
    shiftSessionDates(durationMinutes, session.start);
  };

  const onStartDateChange = (e, newStart) => {
    if (newStart) {
      shiftSessionDates(durationValue, newStart);
    }
  };

  const onEndDateChange = (e, newEnd) => {
    shiftSessionDates(differenceInMinutes(new Date(newEnd), new Date(session.start)), session.start);
  };

  const onRoomIdChange = useCallback(
    room => {
      dispatch(change(form, `sessions[${session.index}].room`, room ? room.name : null));
      dispatch(change(form, `sessions[${session.index}].site`, room ? room["site.name"] : null));
      dispatch(change(form, `sessions[${session.index}].siteId`, room ? room["site.id"] : null));
      dispatch(change(form, `sessions[${session.index}].siteTimezone`, room ? room["site.localTimezone"] : null));
    },
    [form, session.index]
  );

  const onDeleteTutor = (index: number) => {
    dispatch(arrayRemove(form, `sessions[${session.index}].tutorAttendances`, index));
  };

  const onAddTutor = (tutor: CourseClassTutorExtended) => {
    dispatch(arrayPush(form, `sessions[${session.index}].tutorAttendances`, {
      id: null,
      courseClassTutorId: tutor.id,
      temporaryTutorId: tutor.temporaryId,
      contactName: tutor.tutorName,
      attendanceType: 'Not confirmed for payroll',
      note: null,
      actualPayableDurationMinutes: durationValue,
      hasPayslip: false,
      start: session.start,
      end: session.end,
      contactId: tutor.contactId,
      payslipIds: []
    } as TutorAttendance));
  };

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={4}>
        <FormField type="stub" name={`sessions[${session.index}].end`} validate={validateSessionEnd} />
        <FormField
          type="dateTime"
          name={`sessions[${session.index}].start`}
          label={`${session.roomId
              ? (session.siteTimezone 
                ? `Start (${session.siteTimezone})` 
                : `Virtual start (${Intl.DateTimeFormat().resolvedOptions().timeZone})`)
              : "Start"}`}
          onChange={onStartDateChange}
          timezone={session.siteTimezone}
          className={warningTypes.Session.length || warningTypes.UnavailableRule.length ? "errorColor" : undefined}
          persistValue
        />
      </Grid>

      <Grid item xs={2}>
        <EditInPlaceDurationField
          label="Duration"
          meta={{
            error: durationError,
            invalid: Boolean(durationError)
          }}
          input={{
            value: durationValue,
            onChange: stubFunction,
            onBlur: onDurationChange,
            onFocus: stubFunction
          }}
        />
      </Grid>

      <Grid item xs={2}>
        <FormField
          name={`sessions[${session.index}].end`}
          timezone={session.siteTimezone}
          onChange={onEndDateChange}
          type="time"
          label="End"
        />
      </Grid>
      <Grid item xs={12}>
        {warningTypes.Session
          .map(w => <ErrorMessage message={w.message} /> )}
        {warningTypes.UnavailableRule
          .map(w => <ErrorMessage message={w.message} /> )}
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Room"
          name={`sessions[${session.index}].roomId`}
          label="Site and room"
          aqlColumns="name,site.name,site.localTimezone,site.id"
          selectValueMark="id"
          selectLabelCondition={roomLabel}
          defaultDisplayValue={`${session.site} - ${session.room}`}
          labelAdornment={<LinkAdornment linkHandler={openRoomLink} link={session.roomId} disabled={!session.roomId} />}
          onInnerValueChange={onRoomIdChange}
          rowHeight={36}
          hasError={Boolean(warningTypes.Room.length)}
          allowEmpty
        />
      </Grid>
      {warningTypes.Room
        .map(w => <ErrorMessage message={w.message} /> )}
      <Grid item xs={12} className="mb-2">
        <Field
          name={`sessions[${session.index}].tutorAttendances`}
          component={CourseClassTutorRooster}
          warningTypes={warningTypes}
          session={session}
          sessionDuration={durationValue}
          tutors={tutors}
          onDeleteTutor={onDeleteTutor}
          onAddTutor={onAddTutor}
        />
      </Grid>
      <Grid xs={12} className="secondaryHeading mb-1">
        Notes
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name={`sessions[${session.index}].publicNotes`}
          label="Public notes"
          fullWidth
        />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name={`sessions[${session.index}].privateNotes`}
          label="Private notes"
          fullWidth
        />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State, ownProps: Props) => ({
  sites: state.plainSearchRecords["Site"].items,
  session: formValueSelector(ownProps.form)(state, `sessions[${ownProps.index}]`) || {},
  timezones: state.timezones,
});

export default connect(mapStateToProps)(CourseClassSessionFields);
