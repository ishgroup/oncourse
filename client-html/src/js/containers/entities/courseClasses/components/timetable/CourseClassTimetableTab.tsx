/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useState 
} from "react";
import {
 arrayRemove, change, initialize, startAsyncValidation, stopAsyncValidation 
} from "redux-form";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import {
  addDays,
  addHours,
  addMinutes,
  differenceInMinutes,
  subDays
} from "date-fns";
import { CourseClassTutor, SessionWarning, TutorAttendance } from "@api/model";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import { connect } from "react-redux";
import Typography from "@mui/material/Typography";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Settings from "@mui/icons-material/Settings";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { EditViewProps } from "../../../../../model/common/ListView";
import { TimetableMonth, TimetableSession } from "../../../../../model/timetable";
import { getAllMonthsWithSessions } from "../../../../timetable/utils";
import CalendarMonthBase from "../../../../timetable/components/calendar/components/month/CalendarMonthBase";
import CalendarDayBase from "../../../../timetable/components/calendar/components/day/CalendarDayBase";
import { ClassCostExtended, CourseClassExtended, SessionRepeatTypes } from "../../../../../model/entities/CourseClass";
import ExpandableContainer from "../../../../../common/components/layout/expandable/ExpandableContainer";
import history from "../../../../../constants/History";
import { setCourseClassSessionsWarnings } from "../../actions";
import CourseClassBulkChangeSession from "./CourseClassBulkChangeSession";
import CourseClassExpandableSession from "./CourseClassExpandableSession";
import CopySessionDialog from "./CopySessionDialog";
import { normalizeNumber } from "../../../../../common/utils/numbers/numbersNormalizing";
import CourseClassTimetableService from "./services/CourseClassTimetableService";
import { addActionToQueue, removeActionsFromQueue } from "../../../../../common/actions";
import {
  courseClassCloseBulkUpdateModal,
  courseClassOpenBulkUpdateModal,
  courseClassSelectAllSession,
  courseClassSelectSingleSession,
  postCourseClassSessions
} from "./actions";
import { instantAsyncValidateFieldArrayItemCallback } from "../../../../../common/utils/validation";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { State } from "../../../../../reducers/state";
import { SelectItemDefault } from "../../../../../model/entities/common";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";
import uniqid from "../../../../../common/utils/uniqid";
import { getSessionsWithRepeated, setShiftedTutorAttendances } from "../../utils";

const styles = () => createStyles({
    root: {
      width: "100%"
    },
    sessionTutors: {
      marginBottom: 20
    },
    sessionExpansionPanelSummayRoot: {
      "&:hover": {
        "& $sessionActionButton, & $sessionActionCheckBox": {
          visibility: "visible"
        }
      }
    },
    visibleActionButtons: {
      "& $sessionActionButton, & $sessionActionCheckBox": {
        visibility: "visible"
      }
    },
    sessionActionButtonWrapper: {
      right: -34
    },
    sessionActionButton: {
      visibility: "hidden"
    },
    sessionActionCheckBox: {
      visibility: "hidden"
    },
    sessionItemFormControlRoot: {
      marginRight: 0
    }
  });

interface Props extends Partial<EditViewProps<CourseClassExtended>> {
  classes?: any;
  virualSites?: SelectItemDefault[];
  sessionWarnings?: SessionWarning[];
  sessionSelection?: any[];
  bulkSessionModalOpened?: boolean;
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
}

let pendingSessionActionArgs = null;

const validateSessionUpdate = (id: number, sessions: TimetableSession[], dispatch, form) => {
  const updatedForValidate = sessions.map(({ index, ...rest }) => ({ ...rest }));

  let bindedActionId;

  for (const s of sessions) {
    const temp = s.tutorAttendances.find(ta => ta.temporaryTutorId);
    if (temp) {
      bindedActionId = temp.temporaryTutorId;
      break;
    }
  }

  dispatch(startAsyncValidation(form));

  CourseClassTimetableService.validateUpdate(id || -1, updatedForValidate)
    .then(sessionWarnings => {
      if (pendingSessionActionArgs) {
        const validateArgs: [any, any, any, any] = pendingSessionActionArgs;
        pendingSessionActionArgs = null;
        return validateSessionUpdate(...validateArgs);
      }
      dispatch(
        addActionToQueue(postCourseClassSessions(id, updatedForValidate), "POST", "Session", id, bindedActionId)
      );
      dispatch(setCourseClassSessionsWarnings(sessionWarnings));
      dispatch(stopAsyncValidation(form, null));
    })
    .catch(res => {
      instantFetchErrorHandler(dispatch, res);
      dispatch(stopAsyncValidation(form, instantAsyncValidateFieldArrayItemCallback("sessions", 0, res)));
    });
};

const sessionInitial: TimetableSession = {
  id: null,
  name: "",
  code: "",
  room: null,
  site: null,
  tutors: [],
  classId: null,
  roomId: null,
  siteId: null,
  tutorAttendances: [],
  publicNotes: null,
  privateNotes: null
};

const CourseClassTimetableTab = ({
  values,
  showConfirm,
  dispatch,
  form,
  twoColumn,
  classes,
  tabIndex,
  expanded,
  setExpanded,
  toogleFullScreenEditView,
  virualSites,
  isNew,
  sessionWarnings,
  sessionSelection,
  bulkSessionModalOpened,
  addTutorWage
}: Props) => {
  const [expandedSession, setExpandedSession] = useState(null);
  const [copyDialogAnchor, setCopyDialogAnchor] = useState(null);
  const [openCopyDialog, setOpenCopyDialog] = React.useState({ open: false, session: { id: -1 } });
  const [months, setMonths] = useState<TimetableMonth[]>([]);
  const [sessionMenu, setSessionMenu] = useState(null);

  const onSelfPacedChange = useCallback(
    (e, value) => {
      if (!values.isDistantLearningCourse && !isNew) {
        e.preventDefault();
        showConfirm({
          onConfirm: () => CourseClassTimetableService.validateUpdate(values.id, [])
            .then(sessionWarnings => {
              dispatch(change(form, "isDistantLearningCourse", value));
              dispatch(change(form, "sessions", []));
              dispatch(change(form, "trainingPlan", []));
              dispatch(change(form, "studentAttendance", []));
              dispatch(change(form, "startDateTime", null));
              dispatch(change(form, "endDateTime", null));
              dispatch(addActionToQueue(postCourseClassSessions(values.id, []), "POST", "Session", values.id));
              dispatch(setCourseClassSessionsWarnings(sessionWarnings));
            })
            .catch(res => instantFetchErrorHandler(dispatch, res)),
          confirmMessage: "You are about to delete all sessions from this class. This will also remove any attendance records and any training plan details",
          confirmButtonText: "Delete"
        });
      }
    },
    [values.isDistantLearningCourse, values.sessions, isNew]
  );

  useEffect(() => {
    if (!values.sessions || !values.sessions.length) {
      setMonths([]);
      return;
    }

    if (values.sessions.length) {
      setMonths(
        getAllMonthsWithSessions(
          values.sessions,
          values.sessions[0].siteTimezone
            ? appendTimezone(new Date(values.sessions[0].start), values.sessions[0].siteTimezone)
            : new Date(values.sessions[0].start)
        )
      );
    }
  }, [expandedSession, values.sessions && values.sessions.length, values.courseName]);

  useEffect(() => {
    if (!twoColumn && expanded.includes(tabIndex)) {
      setExpanded(prev => prev.filter(p => p !== tabIndex));
    }
  }, [twoColumn, expanded, tabIndex]);

  const onChangeBase = useCallback(
    id => {
      setExpandedSession(expandedSession === id ? null : id);
    },
    [expandedSession]
  );

  const addSession = useCallback(() => {
    if (!expanded.includes(tabIndex)) {
      setExpanded(prev => [...prev, tabIndex]);
    }
    const temporaryId = uniqid();
    
    let start: any;
    let end: any;
    
    if (values.sessions.length) {
      start = values.sessions[0].start;
      end = values.sessions[0].end;
    } else {
      start = new Date();
      start.setMinutes(0, 0, 0);
      end = addHours(start, 1);
      start = start.toISOString();
      end = end.toISOString();
    }

    const duration = differenceInMinutes(new Date(end), new Date(start));

    const tutors = [];

    const tutorAttendances: TutorAttendance[] = [];

    values.tutors.forEach(t => {
      tutorAttendances.push({
        id: null,
        courseClassTutorId: t.id,
        temporaryTutorId: t.temporaryId,
        contactName: t.tutorName,
        attendanceType: 'Not confirmed for payroll',
        contactId: t.contactId,
        note: null,
        actualPayableDurationMinutes: duration,
        hasPayslip: null,
        start,
        end
      });
      tutors.push(t.tutorName);
    });

    const name = values.courseName || "No name set";
    const { courseId } = values;

    const updated: TimetableSession[] = [
      values.sessions.length
        ? {
            ...values.sessions[0],
            id: null,
            temporaryId,
            tutors,
            courseId,
            tutorAttendances,
            name
          }
        : {
            ...sessionInitial,
            start,
            end,
            tutors,
            temporaryId,
            courseId,
            tutorAttendances,
            name
          },
      ...values.sessions
    ];

    if (!values.startDateTime) {
      dispatch(change(form, "startDateTime", updated[0].start));
    }

    updated.forEach((s: any, index) => {
      s.index = index;
    });

    setExpandedSession(updated[0].temporaryId);
    dispatch(change(form, "sessions", updated));

    validateSessionUpdate(values.id, updated, dispatch, form);
  }, [values.id, values.sessions, values.tutors, values.courseName, values.startDateTime, values.courseId, expanded, tabIndex]);

  const onDeleteHandler = useCallback(
    (e, index) => {
      e.stopPropagation();
      showConfirm({
        onConfirm: () => {
        const updated: TimetableSession[] = [...values.sessions];

        updated.splice(index, 1);

        dispatch(arrayRemove(form, "sessions", index));

        if (!updated.length) {
          dispatch(change(form, "startDateTime", null));
          dispatch(change(form, "endDateTime", null));

          if (!values.id) {
            dispatch(removeActionsFromQueue([{ entity: "Session" }]));
            return;
          }
        }

        validateSessionUpdate(values.id, updated, dispatch, form);
      },
        confirmMessage: "Session will be deleted permanently",
        confirmButtonText: "Delete"
      });
    },
    [form, values.sessions]
  );

  const onCopyHandler = useCallback(
    (e, session) => {
      e.stopPropagation();
      dispatch(
        initialize("CopySessionForm", {
          session,
          repeatTimes: 1,
          repeatType: "day (excluding weekends)"
        })
      );
      setCopyDialogAnchor(e.currentTarget);
    },
    [form]
  );

  const closeCopyDialog = useCallback(e => {
    const role = e && e.target && e.target.getAttribute("role");
    // prevent close on select menu item click
    if (role === "option") {
      return;
    }
    setCopyDialogAnchor(null);
    setOpenCopyDialog({ open: false, session: { id: -1 } });
  }, []);

  const onSaveRepeatHandler = useCallback(
    (val: { repeatType: SessionRepeatTypes; repeatTimes: number; session: TimetableSession }) => {
      const sessions = getSessionsWithRepeated(
        values.sessions[val.session.index],
        val.repeatType,
        val.repeatTimes,
        values.sessions
      );

      sessions.forEach((s, index) => {
        s.index = index;
      });

      dispatch(change(form, "sessions", sessions));

      validateSessionUpdate(values.id, sessions, dispatch, form);

      closeCopyDialog(null);
      setOpenCopyDialog({ open: false, session: { id: -1 } });
    },
    [form, values.sessions, values.id]
  );

  const onExpand = useCallback(
    (e, expanded) => {
      if (!twoColumn && expanded.includes(6)) {
        e.preventDefault();

        const search = new URLSearchParams(window.location.search);
        search.append("expandTab", tabIndex.toString());
        history.replace({
          pathname: history.location.pathname,
          search: decodeURIComponent(search.toString())
        });

        toogleFullScreenEditView();
      }
    },
    [twoColumn, expanded, tabIndex]
  );

  const triggerDebounseUpdate = session => {
    const updated = [...values.sessions];
    updated.splice(session.index, 1, session);
    validateSessionUpdate(values.id, updated, dispatch, form);
  };

  const handleSessionMenu = useCallback(e => {
    setSessionMenu(e.currentTarget);
  }, []);

  const handleSessionMenuClose = useCallback(() => {
    setSessionMenu(null);
  }, []);

  const onDeleteTimetableEvents = useCallback(() => {
    showConfirm({
      onConfirm: () => {
        const updated: TimetableSession[] = [...values.sessions];
        sessionSelection.forEach(session => {
          const index = updated.findIndex(s => s.id === session);

          updated.splice(index, 1);

          dispatch(arrayRemove(form, "sessions", index));
        });

        setSessionMenu(null);
        dispatch(courseClassSelectAllSession([]));

        if (!updated.length) {
          dispatch(change(form, "startDateTime", null));
          dispatch(change(form, "endDateTime", null));

          if (!values.id) {
            dispatch(removeActionsFromQueue([{ entity: "Session" }]));
            return;
          }
        }
        validateSessionUpdate(values.id, updated, dispatch, form);
      },
      confirmMessage: `${sessionSelection.length} Session${sessionSelection.length > 1 ? "s" : ""} will be deleted permanently`,
      confirmButtonText: "Delete"
    });
  }, [form, values.sessions, sessionSelection]);

 // Bulk update
  const onBulkSessionUpdate = bulkValue => {
    const updated = [...values.sessions];

    sessionSelection.forEach(sid => {
      const originalSession = updated.find(s => s.id === sid || s.temporaryId === sid);

      if (!originalSession) return;

      const session: TimetableSession = JSON.parse(JSON.stringify(originalSession));
      const startDate = new Date(session.start);
      const endDate = new Date(session.end);

      startDate.setSeconds(0, 0);
      endDate.setSeconds(0, 0);
      const durationValue = differenceInMinutes(endDate, startDate);

      let actualPayableDurationMinutes;

      if (bulkValue.siteId !== null && bulkValue.locationChecked) {
        session.siteId = bulkValue.siteId;
        session.site = bulkValue.site;
        session.siteTimezone = bulkValue.siteTimezone;
        session.roomId = bulkValue.roomId;
        session.room = bulkValue.room;
      }
      if (
        (bulkValue.startChecked && bulkValue.durationChecked && bulkValue.payableDurationChecked)
        && bulkValue.start !== "" && bulkValue.duration !== 0 && bulkValue.payableDuration !== 0
      ) {
        const newStartDate = new Date(bulkValue.start);
        const startDate = new Date(session.start);
        startDate.setHours(newStartDate.getHours(), newStartDate.getMinutes(), 0, 0);
        session.start = startDate.toISOString();

        const endDate = addMinutes(startDate, bulkValue.duration);
        endDate.setSeconds(0, 0);
        session.end = endDate.toISOString();

        actualPayableDurationMinutes = bulkValue.payableDuration;
      } else if (
        (bulkValue.startChecked && bulkValue.durationChecked)
        && bulkValue.start !== "" && bulkValue.duration !== 0
      ) {
        const newStartDate = new Date(bulkValue.start);
        const startDate = new Date(session.start);
        startDate.setHours(newStartDate.getHours(), newStartDate.getMinutes(), 0, 0);
        session.start = startDate.toISOString();

        const endDate = addMinutes(startDate, bulkValue.duration);
        endDate.setSeconds(0, 0);
        session.end = endDate.toISOString();
      } else if (
        (bulkValue.startChecked && bulkValue.payableDurationChecked)
        && bulkValue.start !== "" && bulkValue.payableDuration !== 0
      ) {
        const newStartDate = new Date(bulkValue.start);
        const startDate = new Date(session.start);
        startDate.setHours(newStartDate.getHours(), newStartDate.getMinutes(), 0, 0);
        session.start = startDate.toISOString();

        const endDate = addMinutes(startDate, durationValue);
        endDate.setSeconds(0, 0);
        session.end = endDate.toISOString();
        actualPayableDurationMinutes = bulkValue.payableDuration;
      } else if (
        (bulkValue.durationChecked && bulkValue.payableDurationChecked)
        && bulkValue.duration !== 0 && bulkValue.payableDuration !== 0
      ) {
        const startDate = new Date(session.start);
        startDate.setSeconds(0, 0);

        const endDate = addMinutes(startDate, bulkValue.duration);
        endDate.setSeconds(0, 0);
        session.end = endDate.toISOString();

        actualPayableDurationMinutes = bulkValue.payableDuration;
      } else if (bulkValue.startChecked && bulkValue.start !== "") {
        const newStartDate = new Date(bulkValue.start);
        let startDate = new Date(session.start);
        startDate.setHours(newStartDate.getHours(), newStartDate.getMinutes(), 0, 0);

        let endDate = addMinutes(startDate, durationValue);
        endDate.setSeconds(0, 0);

        // workaround for DST time offset
        if (session.siteTimezone) {
          const startHoursDiff = appendTimezone(newStartDate, session.siteTimezone).getHours()
            - appendTimezone(startDate, session.siteTimezone).getHours();

          if (startHoursDiff) {
            startDate = addHours(startDate, startHoursDiff);
            endDate = addHours(endDate, startHoursDiff);
          }
        }

        session.start = startDate.toISOString();
        session.end = endDate.toISOString();
        setShiftedTutorAttendances(originalSession, session);
      } else if (bulkValue.durationChecked && bulkValue.duration !== 0) {
        session.end = addMinutes(new Date(session.start), bulkValue.duration).toISOString();
        setShiftedTutorAttendances(originalSession, session);
      } else if (bulkValue.payableDurationChecked && bulkValue.payableDuration !== 0) {
        actualPayableDurationMinutes = bulkValue.payableDuration;
      }
      if (bulkValue.moveForwardChecked && bulkValue.moveForward !== "" && bulkValue.moveForward !== "0") {
        session.start = addDays(new Date(session.start), parseInt(bulkValue.moveForward)).toISOString();
        session.end = addDays(new Date(session.end), parseInt(bulkValue.moveForward)).toISOString();
      } else if (bulkValue.moveBackwardChecked && bulkValue.moveBackward !== "" && bulkValue.moveBackward !== "0") {
        session.start = subDays(new Date(session.start), parseInt(bulkValue.moveBackward)).toISOString();
        session.end = subDays(new Date(session.end), parseInt(bulkValue.moveBackward)).toISOString();
      }
      if (bulkValue.tutorsChecked) {
        setShiftedTutorAttendances(originalSession, session);
        const payslipAttendances = session.tutorAttendances.filter(pa => pa.hasPayslip);
        session.tutorAttendances = bulkValue.tutorAttendances.map(ta => {
          // Check for payslip
          const payslipAttendanceIndex = payslipAttendances.findIndex(pa => pa.courseClassTutorId === ta.courseClassTutorId);
          if (payslipAttendanceIndex !== -1) {
            const result = payslipAttendances[payslipAttendanceIndex];
            payslipAttendances.splice(payslipAttendanceIndex, 1);
            return result;
          }

          const taStart = new Date(ta.start);
          const taEnd = new Date(ta.end);

          let start = new Date(session.start);
          let end = new Date(session.end);

          start.setHours(taStart.getHours(), taStart.getMinutes(), 0, 0);
          end.setHours(taEnd.getHours(), taEnd.getMinutes(), 0, 0);

          // workaround for DST time offset
          if (session.siteTimezone) {
            const startHoursDiff = appendTimezone(new Date(originalSession.start), session.siteTimezone).getHours()
              - appendTimezone(start, session.siteTimezone).getHours();

            if (startHoursDiff) {
              start = addHours(start, startHoursDiff);
              end = addHours(end, startHoursDiff);
            }
          }

          return {
            ...ta,
            start: start.toISOString(),
            end: end.toISOString(),
          };
        }).concat(payslipAttendances);
      }

      if (typeof actualPayableDurationMinutes === 'number') {
        session.tutorAttendances = session.tutorAttendances.map(ta => ({
          ...ta,
          actualPayableDurationMinutes
        }));
      }

      session.tutors = session.tutorAttendances.map(ta => ta.contactName);

      updated.splice(session.index, 1, session);
    });

    setSessionMenu(null);

    if (updated.length) {
      setMonths(
        getAllMonthsWithSessions(
          updated,
          updated[0].siteTimezone
            ? appendTimezone(new Date(updated[0].start), updated[0].siteTimezone)
            : new Date(updated[0].start)
        )
      );
    }

    validateSessionUpdate(values.id, updated, dispatch, form);
    dispatch(change(form, "sessions", updated));
    dispatch(courseClassSelectAllSession([]));
    dispatch(courseClassCloseBulkUpdateModal());
  };

  const onChangeTimetableEvents = () => {
    dispatch(courseClassOpenBulkUpdateModal());
  };

  const onSelectAllSession = useCallback(e => {
    if (e.target.checked) {
      dispatch(courseClassSelectAllSession(values.sessions.map(s => s.id || s.temporaryId)));
    } else {
      dispatch(courseClassSelectAllSession([]));
    }
  }, [values.sessions]);

  const selectSessionItem = useCallback(session => {
    dispatch(courseClassSelectSingleSession(session));
  }, []);

  const renderedMonths = useMemo(
    () => months.map((m, i) => (
      <CalendarMonthBase key={i} fullWidth {...m}>
        {m.days.map(d => {
            if (!d.sessions.length) {
              return null;
            }
            return (
              <CalendarDayBase day={d.day} timezone={d.timezone} key={d.day.toString()}>
                {d.sessions.map(s => {
                  const onChange = () => onChangeBase(s.id || s.temporaryId);
                  const warnings = sessionWarnings.filter(w => (s.id ? w.sessionId === s.id : s.temporaryId === w.temporaryId));

                  return (
                    <CourseClassExpandableSession
                      key={s.id || s.temporaryId}
                      tutors={values.tutors}
                      expanded={expandedSession}
                      session={s}
                      onChange={onChange}
                      triggerDebounseUpdate={triggerDebounseUpdate}
                      onDeleteHandler={onDeleteHandler}
                      onCopyHandler={onCopyHandler}
                      classes={classes}
                      dispatch={dispatch}
                      form={form}
                      selectSessionItem={() => selectSessionItem(s)}
                      sessionSelection={sessionSelection}
                      warnings={warnings}
                      setOpenCopyDialog={setOpenCopyDialog}
                      openCopyDialog={openCopyDialog}
                      budget={values.budget}
                      addTutorWage={addTutorWage}
                    />
                  );
                })}
              </CalendarDayBase>
            );
          })}
      </CalendarMonthBase>
      )),
    [months, values.tutors, values.budget, sessionSelection, sessionWarnings, openCopyDialog]
  );

  const selfPacedField = (
    <FormControlLabel
      className="switchWrapper"
      control={(
        <FormField
          type="switch"
          name="isDistantLearningCourse"
          color="primary"
          onChangeHandler={onSelfPacedChange}
        />
      )}
      label="Self-paced"
      labelPlacement="start"
    />
  );

  const disabledMenuItem = sessionSelection.length === 0;

  return (
    <div className="pl-3 pr-3">
      {sessionSelection.length > 0 && bulkSessionModalOpened && (
        <CourseClassBulkChangeSession
          onSubmit={onBulkSessionUpdate}
          opened={bulkSessionModalOpened}
          sessions={values.sessions}
          tutors={values.tutors}
          budget={values.budget}
        />
      )}
      {values.isDistantLearningCourse ? (
        <Grid container columnSpacing={3}>
          <Grid item xs={12} className="pb-2 centeredFlex">
            <div>
              <div className="heading pb-1">Timetable</div>
              {isNew && (
                <Typography variant="caption" color="textSecondary">
                  Please save your new class before editing timetable
                </Typography>
              )}
            </div>
            <div className="flex-fill" />
            {selfPacedField}
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              label="Maximum days to complete"
              name="maximumDays"
              min="1"
              max="99"
              step="1"
              normalize={normalizeNumber}
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              label="Expected study hours"
              name="expectedHours"
              min="1"
              max="99"
              step="1"
              normalize={normalizeNumber}
              required
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              label="Virtual site"
              name="virtualSiteId"
              items={virualSites}
              allowEmpty
            />
          </Grid>
        </Grid>
      ) : (
        <>
          <div>
            <ExpandableContainer
              header="Timetable"
              index={tabIndex}
              expanded={expanded}
              setExpanded={setExpanded}
              onAdd={twoColumn ? addSession : null}
              onChange={onExpand}
              headerAdornment={(
                <>
                  <div>
                    {values.sessions && values.sessions.length > 0 && (
                      <>
                        <Checkbox
                          name="selectAllSession"
                          size="small"
                          onClick={onSelectAllSession}
                          checked={sessionSelection.length === values.sessions.length}
                        />
                        <IconButton
                          color="inherit"
                          aria-owns={sessionMenu ? "sessionSettings" : undefined}
                          aria-haspopup="true"
                          onClick={handleSessionMenu}
                          size="small"
                        >
                          <Settings />
                        </IconButton>
                      </>
                    )}
                  </div>
                  <div className="flex-fill" />
                  {selfPacedField}
                </>
              )}
            >
              <CopySessionDialog
                popupAnchorEl={copyDialogAnchor}
                onCancel={closeCopyDialog}
                onSave={onSaveRepeatHandler}
              />
              {renderedMonths}
            </ExpandableContainer>
          </div>
          <Menu
            id="sessionSettings"
            anchorEl={sessionMenu}
            open={Boolean(sessionMenu)}
            onClose={handleSessionMenuClose}
            disableAutoFocusItem
          >
            <MenuItem
              classes={{
                root: "listItemPadding"
              }}
              onClick={onDeleteTimetableEvents}
              disabled={disabledMenuItem}
            >
              {`Delete ${sessionSelection.length} timetable event${sessionSelection.length > 1 ? "s" : ""}`}
            </MenuItem>
            <MenuItem
              classes={{
                root: "listItemPadding"
              }}
              onClick={onChangeTimetableEvents}
              disabled={disabledMenuItem}
            >
              {`Bulk change ${sessionSelection.length} timetable event${sessionSelection.length > 1 ? "s" : ""}`}
            </MenuItem>
          </Menu>
        </>
      )}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  virualSites: state.sites.virualSites,
  sessionWarnings: state.courseClass.sessionWarnings,
  sessionSelection: state.courseClassesBulkSession.selection,
  bulkSessionModalOpened: state.courseClassesBulkSession.modalOpened
});

export default connect<any, any, any>(mapStateToProps)(withStyles(styles)(CourseClassTimetableTab));
