/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ClashType, CourseClassTutor, SessionWarning, TutorAttendance, } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import { addMinutes, differenceInMinutes } from 'date-fns';
import { EditInPlaceDurationField, ErrorMessage, LinkAdornment, NoWrapOption, stubFunction } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, Field, formValueSelector } from 'redux-form';
import { IAction } from '../../../../../common/actions/IshAction';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { greaterThanNullValidation } from '../../../../../common/utils/validation';
import { ClassCostExtended, CourseClassTutorExtended } from '../../../../../model/entities/CourseClass';
import { TimetableSession } from '../../../../../model/timetable';
import { State } from '../../../../../reducers/state';
import { openRoomLink } from '../../../rooms/utils';
import { setShiftedTutorAttendances } from '../../utils';
import CourseClassTutorRooster from './CourseClassTutorRooster';

interface Props {
  form: string;
  index: number;
  dispatch: Dispatch<IAction>
  tutors: CourseClassTutorExtended[];
  session: TimetableSession;
  triggerDebounseUpdate?: any;
  warnings: SessionWarning[];
  budget: ClassCostExtended[];
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
  asyncValidate: (field: string, value: any, trigger: "change" | "blur") => any;
  sessions?: TimetableSession[];
}

const roomLabel = room => {
  if (room && room["site.name"]) return `${room["site.name"]} - ${room.name}`;

  return room?.name;
};

const getSiteAndRoomLabel = session => `${session?.site || ""}${session?.room ? ` - ${session?.room}` : ""}`;

const validateDuration = value => (value < 5 || value > 1440
    ? "Each entry in the timetable cannot be shorter than 5 minutes or longer than 24 hours."
    : undefined);

export const siteAndRoomSort = (a, b) => {
  return (roomLabel(a).toLowerCase() > roomLabel(b).toLowerCase() ? 1 : -1);
};

const CourseClassSessionFields: React.FC<Props> = ({
  form,
  dispatch,
  session,
  tutors,
  warnings,
  budget,
  addTutorWage,
  asyncValidate,
  sessions
}) => {

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
    const end = endDate.toISOString();

    const updatedSession = {
      ...session,
      start: startDate.toISOString(),
      end
    };
    setShiftedTutorAttendances(session, updatedSession);
    return updatedSession;
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
  
  const applyChangesAndAsyncValidate = (updatedSession: TimetableSession) => {
    const updatedSessions = [...(sessions || [])];
    updatedSessions[session.index] = updatedSession;
    asyncValidate("sessions", updatedSessions, "change");
    dispatch(change(form, 'sessions', updatedSessions));
  };

  const onDurationChange = (durationMinutes: number) => {
    const updatedSession = shiftSessionDates(durationMinutes, session.start);
    if (!greaterThanNullValidation(durationMinutes) && !validateDuration(durationMinutes)) {
      applyChangesAndAsyncValidate(updatedSession);
    }
  };

  const onStartDateChange = (e, newStart) => {
    if (newStart) {
      const updatedSession = shiftSessionDates(durationValue, newStart);
      applyChangesAndAsyncValidate(updatedSession);
    }
  };

  const onEndDateChange = (e, newEnd) => {
    const updatedSession = shiftSessionDates(differenceInMinutes(new Date(newEnd), new Date(session.start)), session.start);
    applyChangesAndAsyncValidate(updatedSession);
  };

  const onRoomIdChange = room => {
    const updatedSession: TimetableSession = {
      ...session,
      ...room
        ? {
          room: room.name,
          site: room["site.name"],
          siteId: room["site.id"],
          siteTimezone: room["site.localTimezone"]
        } : {
          room: null,
          site: null,
          siteId: null,
          siteTimezone: null
        }
    };
    applyChangesAndAsyncValidate(updatedSession);
  };

  const onDeleteTutor = (index: number) => {
    const tutorAttendances = [...session.tutorAttendances];
    tutorAttendances.splice(index, 1);
    const updatedSession = {
      ...session,
      tutorAttendances
    };
    applyChangesAndAsyncValidate(updatedSession);
  };

  const onAddTutor = (tutor: CourseClassTutorExtended) => {
    const updatedSession = {
      ...session,
      tutorAttendances: [
        ...session.tutorAttendances,
        {
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
        } as TutorAttendance
      ]
    };
    applyChangesAndAsyncValidate(updatedSession);
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
          label={$t('duration')}
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
          label={$t('end')}
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
          label={$t('site_and_room')}
          aqlColumns="name,site.name,site.localTimezone,site.id"
          selectValueMark="id"
          selectLabelCondition={roomLabel}
          defaultValue={getSiteAndRoomLabel(session)}
          labelAdornment={<LinkAdornment linkHandler={openRoomLink} link={session.roomId} disabled={!session.roomId} />}
          onInnerValueChange={onRoomIdChange}
          itemRenderer={NoWrapOption}
          hasError={Boolean(warningTypes.Room.length)}
          sort={siteAndRoomSort}
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
        {$t('notes')}
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name={`sessions[${session.index}].publicNotes`}
          label={$t('public_notes')}
                  />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name={`sessions[${session.index}].privateNotes`}
          label={$t('private_notes')}
                  />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State, ownProps: Props) => ({
  session: formValueSelector(ownProps.form)(state, `sessions[${ownProps.index}]`) || {},
  sessions: formValueSelector(ownProps.form)(state, "sessions") || [],
  timezones: state.timezones,
});

export default connect(mapStateToProps)(CourseClassSessionFields);
