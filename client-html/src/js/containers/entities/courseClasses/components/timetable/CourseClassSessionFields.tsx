/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ClashType, CourseClassTutor, SessionWarning, TutorAttendance, } from "@api/model";
import Grid from "@mui/material/Grid";
import { addMinutes, differenceInMinutes } from "date-fns";
import { EditInPlaceDurationField, ErrorMessage, LinkAdornment, NoWrapOption, stubFunction } from "ish-ui";
import React, { useCallback, useEffect, useMemo, useRef } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { arrayPush, arrayRemove, change, Field, formValueSelector } from "redux-form";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { greaterThanNullValidation } from "../../../../../common/utils/validation";
import { ClassCostExtended, CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import { TimetableSession } from "../../../../../model/timetable";
import { State } from "../../../../../reducers/state";
import { openRoomLink } from "../../../rooms/utils";
import { setShiftedTutorAttendances } from "../../utils";
import CourseClassTutorRooster from "./CourseClassTutorRooster";

interface Props {
  form: string;
  index: number;
  dispatch: Dispatch;
  tutors: CourseClassTutorExtended[];
  session?: TimetableSession;
  triggerDebounseUpdate?: any;
  warnings: SessionWarning[];
  budget: ClassCostExtended[];
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
}

const roomLabel = room => {
  if (room && room["site.name"]) return `${room["site.name"]} - ${room.name}`;

  return room?.name;
};

const getSiteAndRoomLabel = session => `${session?.site || ""}${session?.room ? ` - ${session?.room}` : ""}`;

const validateDuration = value => (value < 5 || value > 1440
    ? "Each entry in the timetable cannot be shorter than 5 minutes or longer than 24 hours."
    : undefined);

const CourseClassSessionFields: React.FC<Props> = ({
  form,
  dispatch,
  session,
  tutors,
  triggerDebounseUpdate,
  warnings,
  budget,
  addTutorWage
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
    <Grid container columnSpacing={3} rowSpacing={2}>
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
          debounced={false}
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
          debounced={false}
          type="time"
          label="End"
        />
      </Grid>
      {Boolean(warningTypes.Session.length || warningTypes.UnavailableRule.length) && (
        <Grid item xs={12}>
          {warningTypes.Session
            .map(w => <ErrorMessage message={w.message} /> )}
          {warningTypes.UnavailableRule
            .map(w => <ErrorMessage message={w.message} /> )}
        </Grid>
      ) }

      <Grid item xs={12}>
        <FormField
          type="remoteDataSelect"
          entity="Room"
          name={`sessions[${session.index}].roomId`}
          label="Site and room"
          aqlColumns="name,site.name,site.localTimezone,site.id"
          selectValueMark="id"
          selectLabelCondition={roomLabel}
          defaultValue={getSiteAndRoomLabel(session)}
          labelAdornment={<LinkAdornment linkHandler={openRoomLink} link={session.roomId} disabled={!session.roomId} />}
          onInnerValueChange={onRoomIdChange}
          itemRenderer={NoWrapOption}
          hasError={Boolean(warningTypes.Room.length)}
          allowEmpty
        />
      </Grid>
      {Boolean(warningTypes.Room.length)
        && (
          <Grid item xs={12}>
            {warningTypes.Room
              .map(w => <ErrorMessage message={w.message} /> )}
          </Grid>
      )}
      <Grid item xs={12}>
        <Field
          name={`sessions[${session.index}].tutorAttendances`}
          component={CourseClassTutorRooster}
          warningTypes={warningTypes}
          session={session}
          sessionDuration={durationValue}
          tutors={tutors}
          onDeleteTutor={onDeleteTutor}
          onAddTutor={onAddTutor}
          budget={budget}
          addTutorWage={addTutorWage}
        />
      </Grid>
      <Grid item xs={12} className="secondaryHeading">
        Notes
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name={`sessions[${session.index}].publicNotes`}
          label="Public notes"
                  />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name={`sessions[${session.index}].privateNotes`}
          label="Private notes"
                  />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State, ownProps: Props) => ({
  session: formValueSelector(ownProps.form)(state, `sessions[${ownProps.index}]`) || {},
  timezones: state.timezones,
});

export default connect(mapStateToProps)(CourseClassSessionFields);