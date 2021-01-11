/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useRef
} from "react";
import Grid from "@material-ui/core/Grid";
import FormGroup from "@material-ui/core/FormGroup";
import Typography from "@material-ui/core/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import {
 arrayPush, arrayRemove, change, Field, formValueSelector
} from "redux-form";
import { addMinutes, differenceInMinutes, subMinutes } from "date-fns";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { FormControl, FormHelperText } from "@material-ui/core";
import clsx from "clsx";
import { ClashType, Room, SessionWarning } from "@api/model";
import ErrorMessage from "../../../../../common/components/form/fieldMessage/ErrorMessage";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { greaterThanNullValidation } from "../../../../../common/utils/validation";
import EditInPlaceDurationField from "../../../../../common/components/form/form-fields/EditInPlaceDurationField";
import { stubFunction } from "../../../../../common/utils/common";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { State } from "../../../../../reducers/state";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import { defaultContactName } from "../../../contacts/utils";
import { openSiteLink } from "../../../sites/utils";
import { openRoomLink } from "../../../rooms/utils";
import { StringArgFunction } from "../../../../../model/common/CommonFunctions";
import { TimetableSession } from "../../../../../model/timetable";
import { CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import { getCommonPlainRecords, setCommonPlainSearch } from "../../../../../common/actions/CommonPlainRecordsActions";

interface Props {
  form: string;
  index: number;
  dispatch: Dispatch;
  tutors: CourseClassTutorExtended[];
  classes: any;
  session?: TimetableSession;
  rooms?: Room[];
  getRooms?: StringArgFunction;
  triggerDebounseUpdate?: any;
  warnings: SessionWarning[];
  prevTutorsState?: any;
}
const siteRoomLabel = site => site.name;

const normalizeStartDate = (value, prevValue) => value || prevValue;

const validateDuration = value => (value < 5 || value > 1440
    ? "Each entry in the timetable cannot be shorter than 5 minutes or longer than 24 hours."
    : undefined);

const CourseClassSessionFields: React.FC<Props> = ({
  form,
  dispatch,
  session,
  rooms,
  getRooms,
  tutors,
  triggerDebounseUpdate,
  classes,
  warnings,
  prevTutorsState
}) => {
  const isMounted = useRef(false);

  useEffect(() => {
    if (isMounted.current && session.siteId && !session.roomId && rooms.length) {
      dispatch(change(form, `sessions[${session.index}].roomId`, rooms[0].id));
      dispatch(change(form, `sessions[${session.index}].room`, rooms[0].name));
    }
  }, [rooms]);

  useEffect(() => {
    if (!rooms.length && session.siteId) {
      getRooms(`site.id is ${session.siteId}`);
    }
  }, []);

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
    session.payAdjustment,
    session.temporaryTutorIds,
    session.courseClassTutorIds,
    session.privateNotes,
    session.publicNotes
  ]);

  const warningTypes = useMemo<{[P in ClashType]: SessionWarning[]}>(() => {
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

  const onTutorChange = useCallback(
    (checked, tutor: CourseClassTutorExtended) => {
      if (checked) {
        dispatch(arrayPush(form, `sessions[${session.index}].tutors`, tutor.tutorName));
        const courseClassTutorIds = session.courseClassTutorIds ? session.courseClassTutorIds : [];
        tutor.id
          ? dispatch(
              change(form, `sessions[${session.index}].courseClassTutorIds`, [...courseClassTutorIds, tutor.id])
            )
          : dispatch(
              change(form, `sessions[${session.index}].temporaryTutorIds`, [
                ...session.temporaryTutorIds,
                tutor.temporaryId
              ])
            );
        dispatch(
          arrayPush(form, `sessions[${session.index}].contactIds`, tutor.contactId)
        );
      } else {
        prevTutorsState.current = {
          tutors: session.tutors,
          contactIds: session.contactIds,
          courseClassTutorIds: session.courseClassTutorIds
        };

        const tutorNameIndex = session.tutors ? session.tutors.findIndex(name => name === tutor.tutorName) : -1;
        dispatch(arrayRemove(form, `sessions[${session.index}].tutors`, tutorNameIndex));

        const contactIdIndex = session.contactIds ? session.contactIds.findIndex(id => id === tutor.contactId) : -1;
        dispatch(arrayRemove(form, `sessions[${session.index}].contactIds`, contactIdIndex));

        if (tutor.id) {
          const tutorIdIndex = session.courseClassTutorIds.findIndex(tId => tId === tutor.id);
          const updatedTutorIds = [...session.courseClassTutorIds];
          updatedTutorIds.splice(tutorIdIndex, 1);
          dispatch(change(form, `sessions[${session.index}].courseClassTutorIds`, updatedTutorIds));
        } else {
          const tutorIdIndex = session.temporaryTutorIds.findIndex(tId => tId === tutor.temporaryId);
          const updatedTutorIds = [...session.temporaryTutorIds];
          updatedTutorIds.splice(tutorIdIndex, 1);
          dispatch(change(form, `sessions[${session.index}].temporaryTutorIds`, updatedTutorIds));
        }
      }
    },
    [form, tutors, session.index, session.courseClassTutorIds]
  );

  const onDurationChange = useCallback(
    (durationMinutes: number) => {
      dispatch(
        change(
          form,
          `sessions[${session.index}].end`,
          addMinutes(new Date(session.start), durationMinutes).toISOString()
        )
      );
    },
    [session.start, form, session.index]
  );

  const durationValue = useMemo(() => {
    const startDate = new Date(session.start);
    const endDate = new Date(session.end);

    startDate.setSeconds(0, 0);
    endDate.setSeconds(0, 0);

    return differenceInMinutes(endDate, startDate);
  }, [session.start, session.end]);

  const payableDurationValue = useMemo(() => {
    const startDate = new Date(session.start);
    const endDate = subMinutes(new Date(session.end), session.payAdjustment || 0);

    startDate.setSeconds(0, 0);
    endDate.setSeconds(0, 0);

    return differenceInMinutes(endDate, startDate);
  }, [session.start, session.end, session.payAdjustment]);

  const onPayableDurationChange = useCallback(
    (durationMinutes: number) => {
      dispatch(change(form, `sessions[${session.index}].payAdjustment`, durationValue - durationMinutes));
    },
    [form, session.index, durationValue]
  );

  const durationError = useMemo(() => greaterThanNullValidation(durationValue) || validateDuration(durationValue), [
    durationValue
  ]);

  const validateSessionEnd = useCallback(() => durationError, [durationError]);

  const onStartDateChange = useCallback(
    (e, newStart) => {
      if (newStart) {
        dispatch(
          change(form, `sessions[${session.index}].end`, addMinutes(new Date(newStart), durationValue).toISOString())
        );
      }
    },
    [form, session.index, durationValue]
  );

  const onSiteIdChange = useCallback(
    site => {
      dispatch(change(form, `sessions[${session.index}].site`, site ? site.name : null));
      dispatch(change(form, `sessions[${session.index}].siteTimezone`, site ? site.localTimezone : null));
      dispatch(change(form, `sessions[${session.index}].room`, null));
      dispatch(change(form, `sessions[${session.index}].roomId`, null));
      if (site) {
        getRooms(`site.id is ${site.id}`);
      }
    },
    [form, session.index, session.start, session.end]
  );

  const onRoomIdChange = useCallback(
    room => {
      dispatch(change(form, `sessions[${session.index}].room`, room ? room.label : null));
    },
    [form, session.index]
  );

  const courseClassTutorIdsField = useCallback(
    ({ meta: { invalid, error }, input: { name } }) => (
      <FormControl error={invalid} id={name}>
        <Typography variant="body2" className={clsx("heading", (invalid || warningTypes.Tutor.length) && "errorColor")}>
          Tutors
        </Typography>
        <FormGroup>
          {tutors.filter(t => t.contactId).map(t => {
            const tutorWarning = warningTypes.Tutor.find(w => w.referenceId === t.contactId);

            return (
              <div key={t.id || t.temporaryId}>
                <FormControlLabel
                  className={clsx("checkbox", tutorWarning && "errorColor")}
                  control={(
                    <StyledCheckbox
                      checked={
                      (session.courseClassTutorIds && session.courseClassTutorIds.includes(t.id))
                      || (session.temporaryTutorIds && session.temporaryTutorIds.includes(t.temporaryId))
                    }
                      onChange={(e, v) => onTutorChange(v, t)}
                      color="secondary"
                    />
                )}
                  label={`${defaultContactName(t.tutorName)} (${t.roleName})`}
                />
                {tutorWarning && <ErrorMessage message={tutorWarning.message} className="m-0 p-0" />}
              </div>
            );
          })}
        </FormGroup>
        {invalid && <FormHelperText className="shakingError">{error}</FormHelperText>}
      </FormControl>
      ),
    [tutors, session.courseClassTutorIds, session.temporaryTutorIds, warningTypes.Tutor]
  );

  return (
    <Grid container>
      <Grid item container xs={6}>
        <Grid item xs={12}>
          <FormField type="stub" name={`sessions[${session.index}].end`} validate={validateSessionEnd} />
          <FormField type="stub" name={`sessions[${session.index}].payAdjustment`} />
          <FormField type="stub" name={`sessions[${session.index}].temporaryTutorIds`} />
          <FormField
            type="dateTime"
            name={`sessions[${session.index}].start`}
            label={`${session.room
              ? (session.siteTimezone 
                ? `Start date (${session.siteTimezone})` 
                : `Virtual start date (${Intl.DateTimeFormat().resolvedOptions().timeZone})`)
              : "Start date"}`}
            normalize={normalizeStartDate}
            onChange={onStartDateChange}
            timezone={session.siteTimezone}
            className={warningTypes.Session.length || warningTypes.UnavailableRule.length ? "errorColor" : undefined}
            required
          />
          {warningTypes.Session
            .map(w => <ErrorMessage message={w.message} /> )}
          {warningTypes.UnavailableRule
            .map(w => <ErrorMessage message={w.message} /> )}
        </Grid>

        <Grid item xs={6}>
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
        <Grid item xs={6}>
          <EditInPlaceDurationField
            label="Payable duration"
            meta={{}}
            input={{
              value: payableDurationValue,
              onChange: stubFunction,
              onBlur: onPayableDurationChange,
              onFocus: stubFunction
            }}
          />
        </Grid>

        <Grid item xs={12}>
          <Typography variant="body2" className="heading pt-2 pb-1">
            Location
          </Typography>
        </Grid>
      </Grid>
      <Grid item xs={6}>
        <div className={classes.sessionTutors}>
          <Field
            name={`sessions[${session.index}].courseClassTutorIds`}
            component={courseClassTutorIdsField}
          />
        </div>
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Site"
          name={`sessions[${session.index}].siteId`}
          label="Site"
          aqlColumns="name,localTimezone"
          selectValueMark="id"
          selectLabelMark="name"
          selectLabelCondition={siteRoomLabel}
          defaultDisplayValue={session.site}
          labelAdornment={<LinkAdornment linkHandler={openSiteLink} link={session.siteId} disabled={!session.siteId} />}
          onInnerValueChange={onSiteIdChange}
          className={warningTypes.Site.length ? "errorColor" : undefined}
          rowHeight={36}
          allowEmpty
        />
        {warningTypes.Site
          .map(w => <ErrorMessage message={w.message} /> )}
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="select"
          name={`sessions[${session.index}].roomId`}
          label="Room"
          selectValueMark="id"
          selectLabelMark="name"
          defaultValue={session.room}
          labelAdornment={<LinkAdornment linkHandler={openRoomLink} link={session.roomId} disabled={!session.roomId} />}
          items={rooms || []}
          disabled={!session.siteId}
          onInnerValueChange={onRoomIdChange}
          className={warningTypes.Room.length ? "errorColor" : undefined}
          allowEmpty
        />
        {warningTypes.Room
          .map(w => <ErrorMessage message={w.message} /> )}
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

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getRooms: (search: string) => {
    dispatch(setCommonPlainSearch("Room", search));
    dispatch(getCommonPlainRecords("Room", 0, "name"));
  }
});

const mapStateToProps = (state: State, ownProps: Props) => ({
  rooms: state.plainSearchRecords["Room"].items,
  session: formValueSelector(ownProps.form)(state, `sessions[${ownProps.index}]`) || {}
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseClassSessionFields);
