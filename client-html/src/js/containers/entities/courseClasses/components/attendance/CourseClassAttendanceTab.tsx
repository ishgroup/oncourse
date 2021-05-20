/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import { withStyles, createStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
 faAdjust, faCheck, faTimes, faCircle
} from "@fortawesome/free-solid-svg-icons";
import { change, initialize } from "redux-form";
import { AttendanceType } from "@api/model";
import IconButton from "@material-ui/core/IconButton";
import ChevronRight from "@material-ui/icons/ChevronRight";
import ChevronLeft from "@material-ui/icons/ChevronLeft";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";

import { AppTheme } from "../../../../../model/common/Theme";
import AttendanceActionsMenu from "./AttendanceActionsMenu";
import AttendanceActionModal, { ATTENDANCE_COURSE_CLASS_FORM } from "./AttendanceActionModal";
import ExpandableContainer from "../../../../../common/components/layout/expandable/ExpandableContainer";
import history from "../../../../../constants/History";
import AttendanceGridItem from "./AttendanceGridItem";
import AttendanceDayBase from "./AttendanceDayBase";
import {
  AttandanceChangeType,
  CourseClassExtended,
  ContactAttendanceItem,
  // AttandanceMonth,
  AttandanceStepItem,
  AttendanceGridType,
  tutorStatusRoles,
  StudentAttendanceExtended
} from "../../../../../model/entities/CourseClass";
import { EditViewProps } from "../../../../../model/common/ListView";
import CourseClassAttendanceService from "./services/CourseClassAttendanceService";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import {
  updateCourseClassStudentAttendance,
  updateCourseClassTrainingPlans,
  updateCourseClassTutorAttendance
} from "./actions";
import { addActionToQueue } from "../../../../../common/actions";
import { TimetableSession } from "../../../../../model/timetable";
import uniqid from "../../../../../common/utils/uniqid";

library.add(faAdjust, faCheck, faTimes, faCircle);

const styles = (theme: AppTheme) => createStyles({
    timeline: {
      marginLeft: theme.spacing(-1),
      background: theme.palette.background.default,
      height: "156px"
    },
    timelineShadow: {
      width: `calc(100% + ${theme.spacing(6)}px)`,
      height: "1px",
      top: "156px",
      marginLeft: theme.spacing(-3),
      background: "rgba(0,0,0,0.05)",
      boxShadow: "0px 2px 4px -1px rgba(0,0,0,0.4), 0px 2px 5px 0px rgba(0,0,0,0.2), 0px 1px 10px 0px rgba(0,0,0,0.12)"
    },
    sessionsLine: {
      paddingBottom: theme.spacing(3),
      "&:before": {
        content: "''",
        width: `calc(100% + ${theme.spacing(6)}px)`,
        margin: theme.spacing(0, -3),
        height: "16px",
        position: "relative",
        top: "-6px",
        background: theme.palette.background.default
      }
    },
    root: {
      width: "100%"
    },
    sessionTutors: {
      marginBottom: 20
    },
    attendanceGroupHeading: {
      "&:hover .invisible": {
        visibility: "visible"
      }
    },
    months: {
      display: "flex",
      position: "relative",
      alignItems: "flex-start",
      flexDirection: "row",
      overflow: "hidden",
      overflowX: "auto",
      height: 75,
      paddingLeft: 20,
      marginLeft: -20
    },
    items: {
      marginTop: 20,
      marginLeft: -8,
      marginRight: -8,
      "& > div:nth-child(even)": {
        backgroundColor: theme.table.contrastRow.light
      },
      "&:first-child": {
        marginTop: 0
      }
    },
    attendanceDayBase: {
      paddingLeft: 20,
      marginLeft: -20
    }
  });

let lastUpdated: { type: AttendanceGridType; items: ContactAttendanceItem["attendances"]; id: number | string } = null;

const validateByType = (
  type: AttendanceGridType,
  id: number,
  updated: ContactAttendanceItem["attendances"],
  dispatch
): Promise<any> => {
  let preparedUpdated = updated.map(u => {
    const prepared = { ...u };
    delete prepared.index;
    return prepared;
  });

  let actionId = preparedUpdated.length === 1 ? preparedUpdated[0].id || preparedUpdated[0].moduleId : uniqid();

  if (lastUpdated && lastUpdated.type === type) {
    preparedUpdated.forEach(p => {
      const lastUpdatedIndex = lastUpdated.items.findIndex(
        l => (typeof l.id === "number" && l.id === p.id) || (typeof l.moduleId === "number" && l.moduleId === p.moduleId)
      );

      if (lastUpdatedIndex !== -1) {
        lastUpdated.items.splice(lastUpdatedIndex, 1);
      }
    });

    preparedUpdated = preparedUpdated.concat(lastUpdated.items);
    actionId = lastUpdated.id;
  }

  lastUpdated = { type, items: preparedUpdated, id: actionId };

  switch (type) {
    case "Student":
      return CourseClassAttendanceService.validateUpdateStudentAttendance(id, preparedUpdated).then(() => {
        dispatch(
          addActionToQueue(
            updateCourseClassStudentAttendance(id, preparedUpdated),
            "POST",
            "StudentAttendance",
            actionId
          )
        );
      });
    case "Tutor":
      return CourseClassAttendanceService.validateUpdateTutorAttendance(id, preparedUpdated).then(() => {
        dispatch(
          addActionToQueue(updateCourseClassTutorAttendance(id, preparedUpdated), "POST", "TutorAttendance", actionId)
        );
      });
    case "Training plan":
      return CourseClassAttendanceService.validateUpdateTrainingPlans(id, preparedUpdated).then(() => {
        dispatch(
          addActionToQueue(updateCourseClassTrainingPlans(id, preparedUpdated), "POST", "SessionModule", actionId)
        );
      });
  }
};

const checkDateOverride = (
  updated: StudentAttendanceExtended[],
  attendance: StudentAttendanceExtended,
  sessions: TimetableSession[]
) => {
  if (attendance.attendanceType === "Partial") {
    const targetSession = sessions.find(s => attendance.sessionId === s.id);

    const targetUntilDate = new Date(attendance.attendedUntil || targetSession.start);
    const targetFromDate = new Date(attendance.attendedFrom || targetSession.end);

    updated.forEach(u => {
      const attendedSession = sessions.find(s => u.sessionId === s.id);
      const attendedUntil = new Date(u.attendedUntil || attendedSession.start);
      const attendedFrom = new Date(u.attendedFrom || attendedSession.end);
      attendedUntil.setHours(targetUntilDate.getHours(), targetUntilDate.getMinutes());
      attendedFrom.setHours(targetFromDate.getHours(), targetFromDate.getMinutes());

      u.attendedUntil = attendedUntil.toISOString();
      u.attendedFrom = attendedFrom.toISOString();
    });
  }
};

const studentStatusRoles: AttendanceType[] = ["Unmarked", "Attended", "Absent without reason"];

interface Props extends Partial<EditViewProps<CourseClassExtended>> {
  classes?: any;
  showTrainingPlans?: boolean;
}

const CourseClassAttendanceTab = React.memo<Props>(
  ({
    values,
    form,
    dispatch,
    setExpanded,
    classes,
    toogleFullScreenEditView,
    twoColumn,
    expanded,
    tabIndex,
    dirty,
    isNew,
    showTrainingPlans
  }) => {
    const [selectedItems, setSelectedItems] = useState<AttandanceStepItem[]>([]);
    const [studentsToAttend, setStudentsToAttend] = useState<ContactAttendanceItem[]>([]);
    const [tutorsToAttend, setTutorsToAttend] = useState<ContactAttendanceItem[]>([]);
    const [modulesToAttend, setModulesToAttend] = useState<ContactAttendanceItem[]>([]);
    const [changeType, setAttendanceChangeType] = useState<AttandanceChangeType>(null);
    const [sliderValue, setSliderValue] = useState<number[]>([0, 7]);
    const [sessionsChanged, setSessionsChanged] = useState<boolean>(false);
    const [assessmentsChanged, setAssessmentsChanged] = useState<boolean>(false);
    const [animateDays, setAnimateDays] = React.useState({ animate: "right", value: false });

    const stepItems = useMemo(() => {
      if ((!values.sessions || !values.sessions.length) && (!values.assessments || !values.assessments.length)) {
        return [];
      }

      const items: AttandanceStepItem[] = [...values.sessions, ...values.assessments];

      items.sort((a, b) => (new Date(a.start || a.dueDate) > new Date(b.start || b.dueDate) ? 1 : -1));

      return items;
    }, [
      values.sessions,
      values.sessions && values.sessions.length,
      values.assessments,
      values.assessments && values.assessments.length
    ]);

    useEffect(() => {
      setStudentsToAttend([]);
      setTutorsToAttend([]);
      setModulesToAttend([]);
      lastUpdated = null;
    }, [values.id]);

    useEffect(() => {
      if (stepItems.length > 8) {
        setSelectedItems(stepItems.slice(sliderValue[0], sliderValue[1] + 1));
      } else {
        setSelectedItems(stepItems);
      }
    }, [stepItems.length]);

    useEffect(() => {
      if (!sessionsChanged && values.sessions && values.sessions.length && dirty) {
        setSessionsChanged(true);
      }
      if (sessionsChanged && !dirty) {
        setSessionsChanged(false);
      }
    }, [values.sessions]);

    useEffect(() => {
      if (!assessmentsChanged && values.assessments && values.assessments.length && dirty) {
        setAssessmentsChanged(true);
      }
      if (assessmentsChanged && !dirty) {
        setAssessmentsChanged(false);
      }
    }, [values.assessments]);

    useEffect(() => {
      if (animateDays.value) {
        setTimeout(() => {
          setAnimateDays(prev => ({
              ...prev,
              ...{ value: false }
            }));
        }, 600);
      }
    }, [animateDays.value, animateDays.animate]);

    const checkAnimationClass = React.useCallback(() => [
        animateDays.value && animateDays.animate === "right" && "animated bounceOutLeftCustom",
        animateDays.value && animateDays.animate === "left" && "animated bounceOutRightCustom"
      ], [animateDays && animateDays.value, animateDays && animateDays.animate]);

    const setAttendanceItems = useCallback((type: AttendanceGridType, currentValues: CourseClassExtended) => {
      let valuesPath = "studentAttendance";
      let idPath = "contactId";
      let namePath = "contactName";
      let addAction = setStudentsToAttend;
      let titlePath = null;

      if (type === "Tutor") {
        valuesPath = "tutorAttendance";
        idPath = "courseClassTutorId";
        addAction = setTutorsToAttend;
      }

      if (type === "Training plan") {
        valuesPath = "trainingPlan";
        namePath = "moduleName";
        idPath = "moduleId";
        titlePath = "moduleTitle";
        addAction = setModulesToAttend;
      }

      const result: { [key: string]: ContactAttendanceItem } = {};

      currentValues[valuesPath].forEach(s => {
        if (!result[s[idPath]]) {
          result[s[idPath]] = {
            name: s[namePath],
            title: titlePath ? s[titlePath] : null,
            contactId: s[idPath],
            attendances: []
          };
        }
        result[s[idPath]].attendances.push(s);
      });
      const resultArray = Object.keys(result).map(k => result[k]);

      resultArray.sort((a, b) => (a.name > b.name ? 1 : -1));

      addAction(resultArray);
    }, [setStudentsToAttend, setTutorsToAttend, setModulesToAttend, form]);

    useEffect(() => {
      if (values.studentAttendance && values.studentAttendance.length) {
        setAttendanceItems("Student", values);
      }
    }, [values.studentAttendance]);

    useEffect(() => {
      if (values.tutorAttendance && values.tutorAttendance.length) {
        setAttendanceItems("Tutor", values);
      }
    }, [values.tutorAttendance]);

    useEffect(() => {
      if (showTrainingPlans && values.trainingPlan && values.trainingPlan.length) {
        setAttendanceItems("Training plan", values);
      }
    }, [values.trainingPlan, showTrainingPlans]);

    const validateAttendanceUpdate = useCallback(
      (updated, type: AttendanceGridType) => {
        validateByType(type, values.id, updated, dispatch).catch(res => instantFetchErrorHandler(dispatch, res));
      },
      [validateByType, values.id, form]
    );

    const onExpand = useCallback(
      (e, expanded) => {
        if (!twoColumn && expanded) {
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

    const onSubmitAttendance = useCallback(
      attendance => {
        let mergeProps = {};

        if (attendance.note) {
          mergeProps = { ...mergeProps, note: attendance.note };
        }

        switch (changeType) {
          case "allStudents": {
            const updated = values.studentAttendance.map(s => ({
              ...s,
              ...mergeProps,
              attendanceType: attendance.attendanceType
            }));

            checkDateOverride(updated, attendance, values.sessions);

            dispatch(change(form, "studentAttendance", updated));
            validateAttendanceUpdate(updated, "Student");
            break;
          }
          case "byStudent": {
            const updated = [];

            values.studentAttendance.forEach(s => {
              if (s.contactId === attendance.contactId) {
                const updatedAttandance = {
                  ...s,
                  ...mergeProps,
                  attendanceType: attendance.attendanceType
                };
                checkDateOverride([updatedAttandance], attendance, values.sessions);
                dispatch(change(form, `studentAttendance[${s.index}]`, updatedAttandance));
                updated.push(updatedAttandance);
              }
            });

            validateAttendanceUpdate(updated, "Student");
            break;
          }

          case "bySession": {
            const updated = [];

            values.studentAttendance.forEach(s => {
              if (s.sessionId === attendance.sessionId) {
                const updatedAttandance = {
                  ...s,
                  ...mergeProps,
                  attendanceType: attendance.attendanceType
                };
                checkDateOverride([updatedAttandance], attendance, values.sessions);

                dispatch(change(form, `studentAttendance[${s.index}]`, updatedAttandance));
                updated.push(updatedAttandance);
              }
            });
            validateAttendanceUpdate(updated, "Student");
            break;
          }

          case "singleStudent": {
            dispatch(change(form, `studentAttendance[${attendance.index}]`, attendance));
            validateAttendanceUpdate([attendance], "Student");
            break;
          }

          case "singleTutor": {
            dispatch(change(form, `tutorAttendance[${attendance.index}]`, attendance));
            validateAttendanceUpdate([attendance], "Tutor");
            break;
          }
        }
        setAttendanceChangeType(null);
      },
      [
        form,
        changeType,
        values.id,
        values.studentAttendance,
        values.tutorAttendance,
        values.sessions,
        values.sessions && values.sessions.length,
        setAttendanceChangeType,
        validateAttendanceUpdate,
        checkDateOverride
      ]
    );

    const changeAttendancesByType = useCallback(
      (attendanceType: AttendanceType, changeType: AttandanceChangeType) => {
        let attendances: any = values.studentAttendance;
        let field = "studentAttendance";

        if (changeType === "allTutors") {
          attendances = values.tutorAttendance;
          field = "tutorAttendance";
        }

        switch (attendanceType) {
          case "Partial":
          case "Absent with reason": {
            dispatch(initialize(ATTENDANCE_COURSE_CLASS_FORM, { ...attendances[0], attendanceType }));
            setAttendanceChangeType(changeType);
            break;
          }
          default: {
            const updated = attendances.map(s => ({ ...s, attendanceType }));
            dispatch(change(form, field, updated));
            validateAttendanceUpdate(updated, field === "studentAttendance" ? "Student" : "Tutor");
          }
        }
      },
      [
        form,
        values.studentAttendance,
        values.studentAttendance && values.studentAttendance.length,
        values.tutorAttendance,
        values.tutorAttendance && values.tutorAttendance.length,
        setAttendanceChangeType,
        validateAttendanceUpdate
      ]
    );

    const onChangeAllStudentsAttendance = useCallback(type => changeAttendancesByType(type, "allStudents"), [
      changeAttendancesByType,
      values.studentAttendance,
      values.studentAttendance && values.studentAttendance.length
    ]);

    const onChangeAllTutorsAttendance = useCallback(type => changeAttendancesByType(type, "allTutors"), [
      changeAttendancesByType,
      values.tutorAttendance,
      values.tutorAttendance && values.tutorAttendance.length
    ]);

    const onChangeAllTrainingPlansAttendance = useCallback(
      (type, index?: number) => {
        const sessionIds = [];

        if (type === "Attended") {
          values.sessions.forEach(s => {
            sessionIds.push(s.id);
          });
          values.assessments.forEach(a => {
            sessionIds.push(a.id);
          });
        }

        if (typeof index === "number") {
          dispatch(change(form, `trainingPlan[${index}].sessionIds`, sessionIds));
          validateAttendanceUpdate([{ ...values.trainingPlan[index], sessionIds }], "Training plan");
          return;
        }

        const updated = values.trainingPlan.map(t => ({ ...t, sessionIds }));

        dispatch(change(form, "trainingPlan", updated));

        validateAttendanceUpdate(updated, "Training plan");
      },
      [
        form,
        validateAttendanceUpdate,
        values.trainingPlan,
        values.sessions,
        values.sessions && values.sessions.length,
        values.assessments,
        values.assessments && values.assessments.length
      ]
    );

    const onChangeAllTrainingPlansSessionRow = useCallback(
      (type, sessionId: number) => {
        const updated = values.trainingPlan.map(t => {
          let sessionIds = [...t.sessionIds];
          if (type === "Attended") {
            sessionIds.push(sessionId);
          } else {
            sessionIds = sessionIds.filter(s => s !== sessionId);
          }
          return { ...t, sessionIds };
        });

        dispatch(change(form, "trainingPlan", updated));

        validateAttendanceUpdate(updated, "Training plan");
      },
      [
        form,
        validateAttendanceUpdate,
        values.trainingPlan,
        values.sessions,
        values.sessions && values.sessions.length,
        values.assessments,
        values.assessments && values.assessments.length
      ]
    );

    const scrollSessionsRight = useCallback(() => {
      if (!animateDays.value) {
        setSliderValue(prev => {
          const updatedRight = prev[1] + 8;
          let newValue = [prev[0] + 8, updatedRight];
          if (updatedRight >= stepItems.length) {
            newValue = [stepItems.length - 8, stepItems.length - 1];
          }
          setSelectedItems(stepItems.slice(newValue[0], newValue[1] + 1));
          setAnimateDays({
            animate: "right",
            value: true
          });
          return newValue;
        });
      }
    }, [setSliderValue, setSelectedItems, setAnimateDays, stepItems.length, animateDays.value]);

    const scrollItemsLeft = useCallback(() => {
      if (!animateDays.value) {
        setSliderValue(prev => {
          const updatedLeft = prev[0] - 8;
          let newValue = [updatedLeft, prev[1] - 8];
          if (updatedLeft < 0) {
            newValue = [0, 7];
          }
          setSelectedItems(stepItems.slice(newValue[0], newValue[1] + 1));
          setAnimateDays({
            animate: "left",
            value: true
          });
          return newValue;
        });
      }
    }, [setSliderValue, setSelectedItems, setAnimateDays, stepItems.length, animateDays.value]);

    const changeSessionRow = useCallback(
      (attendanceType: AttendanceType, sessionId: number) => {
        switch (attendanceType) {
          case "Partial":
          case "Absent with reason": {
            dispatch(
              initialize(ATTENDANCE_COURSE_CLASS_FORM, {
                ...values.studentAttendance.find(s => s.sessionId === sessionId),
                attendanceType
              })
            );
            setAttendanceChangeType("bySession");
            break;
          }
          default: {
            const updated = [];

            values.studentAttendance.forEach(s => {
              if (s.sessionId === sessionId) {
                const updatedItem = { ...s, attendanceType };
                dispatch(change(form, `studentAttendance[${s.index}]`, updatedItem));
                updated.push(updatedItem);
              }
            });

            validateAttendanceUpdate(updated, "Student");
          }
        }
      },
      [form, setAttendanceChangeType, validateAttendanceUpdate, values.studentAttendance]
    );

    const onStudentIconClick = useCallback(
      (e, index) => {
        const roleIndex = studentStatusRoles.indexOf(e.currentTarget.getAttribute("role"));

        const attendanceType = [2, -1].includes(roleIndex) ? studentStatusRoles[0] : studentStatusRoles[roleIndex + 1];

        dispatch(change(form, `studentAttendance[${index}].attendanceType`, attendanceType));

        validateAttendanceUpdate(
          [
            {
              ...values.studentAttendance[index],
              attendanceType
            }
          ],
          "Student"
        );
      },
      [form, values.studentAttendance, studentStatusRoles, validateAttendanceUpdate]
    );

    const onTutorIconClick = (e, attendance) => {
      const roleIndex = tutorStatusRoles.indexOf(e.currentTarget.getAttribute("role"));

      const attendanceType = [2, -1].includes(roleIndex) ? tutorStatusRoles[0] : tutorStatusRoles[roleIndex + 1];

      dispatch(change(form, `tutorAttendance[${attendance.index}].attendanceType`, attendanceType));

      const updated = { ...attendance, attendanceType };

      validateAttendanceUpdate([updated], "Tutor");
    };

    const sessionsLeftScroller = useMemo(
      () => selectedItems.length
        && stepItems.length
        && selectedItems[0].id !== stepItems[0].id && (
          <IconButton onClick={scrollItemsLeft} className="mr-3">
            <ChevronLeft />
          </IconButton>
        ),
      [
        selectedItems,
        stepItems,
        stepItems.length,
        animateDays.value,
        scrollItemsLeft
      ]
    );

    const renderedDays = useCallback(
      (attendanceType?: string) => (
        <Grid container>
          <Grid item xs={10} className={clsx("overflow-hidden", classes.attendanceDayBase)}>
            <Grid container className={clsx(checkAnimationClass())}>
              {selectedItems.map((sd, si) => (
                <AttendanceDayBase
                  // eslint-disable-next-line react/no-array-index-key
                  key={si}
                  {...sd}
                  changeSessionRow={type => (
                    attendanceType === "Training plan"
                      ? onChangeAllTrainingPlansSessionRow(type, sd.id)
                      : changeSessionRow(type, sd.id)
                  )}
                  hasStudentAttendance={Boolean(values.studentAttendance.length)}
                  type={attendanceType}
                />
              ))}
            </Grid>
          </Grid>

          {selectedItems.length
            && stepItems.length
            && selectedItems[selectedItems.length - 1].id !== stepItems[stepItems.length - 1].id && (
              <Grid item xs={2} className={clsx(classes.dayItem, "centeredFlex")}>
                <IconButton onClick={scrollSessionsRight}>
                  <ChevronRight />
                </IconButton>
              </Grid>
            )}
        </Grid>
      ),
      [
        checkAnimationClass,
        changeSessionRow,
        scrollSessionsRight,
        selectedItems,
        stepItems,
        stepItems.length,
        values.studentAttendance,
        values.tutorAttendance,
        values.trainingPlan,
        animateDays
      ]
    );

    const renderedStudentAttendances = useMemo(
      () => (studentsToAttend.length ? (
        <>
          <div className={clsx("d-inline-flex-center pt-0 pr-0 pb-2 pl-1", classes.attendanceGroupHeading)}>
            <div className="heading">Students</div>
            <AttendanceActionsMenu
              className="invisible"
              type="Student"
              onChange={onChangeAllStudentsAttendance}
              label="Mark ALL sessions for ALL students as..."
            />
          </div>
          {studentsToAttend.map(sa => (
            <AttendanceGridItem
              type="Student"
              key={sa.contactId}
              item={sa}
              selectedItems={selectedItems}
              setAttendanceChangeType={setAttendanceChangeType}
              validateAttendanceUpdate={validateAttendanceUpdate}
              onStudentIconClick={onStudentIconClick}
              dispatch={dispatch}
              form={form}
              sessions={values.sessions}
              checkAnimationClass={checkAnimationClass}
            />
            ))}
        </>
        ) : null),
      [
        form,
        onChangeAllStudentsAttendance,
        setAttendanceChangeType,
        validateAttendanceUpdate,
        studentsToAttend,
        onStudentIconClick,
        checkAnimationClass,
        selectedItems,
        animateDays,
        values.sessions,
        values.sessions && values.sessions.length
      ]
    );

    const renderedTutorAttendances = useMemo(
      () => (tutorsToAttend.length ? (
        <>
          <div className={clsx("d-inline-flex-center pt-0 pr-0 pb-2 pl-1", classes.attendanceGroupHeading)}>
            <div className="heading">Tutors</div>
            <AttendanceActionsMenu
              className="invisible"
              type="Tutor"
              onChange={onChangeAllTutorsAttendance}
              label="Mark ALL sessions for ALL tutors as..."
            />
          </div>
          {tutorsToAttend.map(sa => (
            <AttendanceGridItem
              type="Tutor"
              key={sa.contactId}
              item={sa}
              selectedItems={selectedItems}
              setAttendanceChangeType={setAttendanceChangeType}
              validateAttendanceUpdate={validateAttendanceUpdate}
              onTutorIconClick={onTutorIconClick}
              dispatch={dispatch}
              form={form}
              checkAnimationClass={checkAnimationClass}
            />
            ))}
        </>
        ) : null),
      [
        form,
        onChangeAllTutorsAttendance,
        setAttendanceChangeType,
        validateAttendanceUpdate,
        checkAnimationClass,
        onTutorIconClick,
        tutorsToAttend,
        selectedItems,
        animateDays,
        values.tutorAttendance,
        values.tutorAttendance && values.tutorAttendance.length
      ]
    );

    const renderedTrainingPlans = useMemo(
      () => (modulesToAttend.length ? (
        <>
          <div className={clsx("d-inline-flex-center pt-0 pr-0 pb-2 pl-1", classes.attendanceGroupHeading)}>
            <AttendanceActionsMenu
              className="invisible"
              type="Training plan"
              onChange={onChangeAllTrainingPlansAttendance}
              label="Mark ALL sessions and tasks for ALL modules as..."
            />
          </div>
          {modulesToAttend.map(sa => (
            <AttendanceGridItem
              type="Training plan"
              key={sa.contactId}
              item={sa}
              selectedItems={selectedItems}
              setAttendanceChangeType={setAttendanceChangeType}
              changeAllItems={onChangeAllTrainingPlansAttendance}
              validateAttendanceUpdate={validateAttendanceUpdate}
              sessions={values.sessions}
              assessments={values.assessments}
              dispatch={dispatch}
              form={form}
              checkAnimationClass={checkAnimationClass}
            />
            ))}
        </>
        ) : null),
      [
        form,
        modulesToAttend,
        selectedItems,
        values.trainingPlan,
        values.sessions,
        values.sessions && values.sessions.length,
        values.assessments,
        values.assessments && values.assessments.length,
        animateDays,
        onChangeAllTrainingPlansAttendance,
        setAttendanceChangeType,
        validateAttendanceUpdate,
        checkAnimationClass
      ]
    );

    const changedWarning = useMemo(() => {
      if (sessionsChanged) {
        return (
          <Typography variant="caption" color="textSecondary">
            Please save your timetable changes before editing attendances
          </Typography>
        );
      }
      if (assessmentsChanged) {
        return (
          <Typography variant="caption" color="textSecondary">
            Please save your assessments changes before editing attendances
          </Typography>
        );
      }
      if (isNew) {
        return (
          <Typography variant="caption" color="textSecondary">
            Please save your new class before editing attendances
          </Typography>
        );
      }
      return null;
    }, [sessionsChanged, assessmentsChanged, isNew]);

    const changedWarningForTrainingPlans = useMemo(() => {
      if (sessionsChanged) {
        return (
          <Typography variant="caption" color="textSecondary">
            Please save your timetable changes before editing training plans
          </Typography>
        );
      }
      if (assessmentsChanged) {
        return (
          <Typography variant="caption" color="textSecondary">
            Please save your assessments changes before editing training plans
          </Typography>
        );
      }
      if (isNew) {
        return (
          <Typography variant="caption" color="textSecondary">
            Please save your new class before editing training plans
          </Typography>
        );
      }
      return null;
    }, [sessionsChanged, assessmentsChanged, isNew]);

    const daysScroller = (type?: string) => (
      <>
        <Grid container className={clsx("sticky top-0 pt-1 zIndex1", classes.timeline)}>
          <Grid item xs={3}>
            &nbsp;
          </Grid>
          <Grid item xs={9} />

          <Grid item container xs={3} alignItems="center" justify="flex-end" className="pr-2">
            {sessionsLeftScroller}
          </Grid>
          <Grid item xs={9} className="centeredFlex">
            {renderedDays(type)}
          </Grid>
        </Grid>
        <div className={clsx("sticky", classes.timelineShadow)} />
      </>
    );

    return showTrainingPlans ? (
      <>
        {stepItems.length > 0 && !sessionsChanged && !assessmentsChanged && !isNew ? (
          <div className="w-100">
            <div className="heading">Training plan</div>
            {daysScroller("Training plan")}
            <Grid container className={classes.sessionsLine}>
              <Grid item xs={12} className={classes.items}>
                {renderedTrainingPlans}
              </Grid>
            </Grid>

            <AttendanceActionModal
              onSubmit={onSubmitAttendance}
              changeType={changeType}
              setAttendanceChangeType={setAttendanceChangeType}
              sessions={values.sessions}
              tutors={values.tutors}
            />
          </div>
        ) : changedWarningForTrainingPlans}
      </>
    ) : (
      <div className="pl-3 pr-3 pb-2">
        <ExpandableContainer
          onChange={onExpand}
          index={tabIndex}
          expanded={expanded}
          setExpanded={setExpanded}
          header="Attendance"
        >
          <>
            {stepItems.length > 0 && !sessionsChanged && !assessmentsChanged && !isNew ? (
              <>
                {daysScroller("")}
                <Grid container className={classes.sessionsLine}>
                  <Grid item xs={12} className={classes.items}>
                    {renderedStudentAttendances}
                  </Grid>
                  <Grid item xs={12} className={classes.items}>
                    {renderedTutorAttendances}
                  </Grid>
                </Grid>

                <AttendanceActionModal
                  onSubmit={onSubmitAttendance}
                  changeType={changeType}
                  setAttendanceChangeType={setAttendanceChangeType}
                  sessions={values.sessions}
                  tutors={values.tutors}
                />
              </>
            ) : changedWarning}
          </>
        </ExpandableContainer>
      </div>
    );
  }
);

export default withStyles(styles)(CourseClassAttendanceTab);
