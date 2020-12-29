/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useRef, useState
} from "react";
import clsx from "clsx";
import debounce from "lodash.debounce";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  reduxForm, getFormValues, DecoratedComponentClass, InjectedFormProps, change
} from "redux-form";
import withStyles from "@material-ui/core/styles/withStyles";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import DialogContent from "@material-ui/core/DialogContent";
import Grid from "@material-ui/core/Grid/Grid";
import FormGroup from "@material-ui/core/FormGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { CourseClassDuplicate } from "@api/model";
import {
 addDays, differenceInDays, getHours, getMinutes, getSeconds, getMilliseconds
} from "date-fns";
import { Typography } from "@material-ui/core";
import Tooltip from "@material-ui/core/Tooltip";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../reducers/state";
import Button from "../../../../../common/components/buttons/Button";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { getPlainCourses, setPlainCourses, setPlainCoursesSearch } from "../../../courses/actions";
import CourseItemRenderer from "../../../courses/components/CourseItemRenderer";
import { courseFilterCondition } from "../../../courses/utils";
import {
  clearDuplicateCourseClassesSessions,
  duplicateCourseClass,
  getDuplicateCourseClassesSessions
} from "../../actions";
import {
 BooleanArgFunction, NoArgFunction
} from "../../../../../model/common/CommonFunctions";
import { TimetableMonth, TimetableSession } from "../../../../../model/timetable";
import DuplicateCourseClassTimetable from "./DuplicateCourseClassTimetable";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { getAllMonthsWithSessions } from "../../../../timetable/utils";
import modalStyles from "./modalStyles";

export const DUPLICATE_COURSE_CLASS_FORM: string = "DuplicateCourseClassForm";

interface Props {
  opened: boolean;
  setDialogOpened: BooleanArgFunction;
  values?: CourseClassDuplicate & { toDate: string };
  duplicateCourseClass?: (values: CourseClassDuplicate, onComplete?: any) => void;
  classes?: any;
  selection?: any;
  closeMenu?: any;
  timetable?: any;
  dispatch?: any;
  getSessions?: (selection: string[]) => void;
  clearTimetable?: NoArgFunction;
  fetching?: boolean;
  earliest?: Date;
  sessions?: TimetableSession[];
  disableClose?: boolean;
  hasZeroWages?: boolean;
  clearCourses?: any;
}

const initialValues: CourseClassDuplicate & { toDate: string } = {
  classIds: [],
  daysTo: 0,
  toDate: new Date().toISOString(),
  copyTutors: true,
  copyTrainingPlans: true,
  applyDiscounts: true,
  copyCosts: true,
  copySitesAndRooms: true,
  copyPayableTimeForSessions: true,
  copyVetData: true,
  copyNotes: true,
  copyAssessments: true,
  copyOnlyMandatoryTags: true
};

const DuplicateCourseClassModal: React.FunctionComponent<Props & InjectedFormProps> = props => {
  const {
    opened,
    handleSubmit,
    duplicateCourseClass,
    setDialogOpened,
    classes,
    reset,
    selection,
    closeMenu,
    getSessions,
    clearTimetable,
    fetching,
    earliest,
    dispatch,
    sessions,
    values,
    invalid,
    disableClose,
    hasZeroWages
  } = props;

  const [months, setMonths] = useState<TimetableMonth[]>([]);
  const [changeCourse, setChangeCourse] = useState<boolean>(false);
  const formattedSessions = useRef<TimetableSession[]>([]);

  useEffect(() => {
    if (opened) {
      getSessions(selection);
    } else {
      clearTimetable();
    }
  }, [opened]);

  useEffect(() => {
    if (sessions.length && earliest) {
      setMonths(getAllMonthsWithSessions(sessions, earliest));
      formattedSessions.current = [...sessions];
    }
  }, [sessions, earliest]);

  useEffect(() => {
    if (earliest) {
      dispatch(change(DUPLICATE_COURSE_CLASS_FORM, "toDate", earliest.toISOString()));
    }
  }, [earliest]);

  const debounceUpdateDaysOffset = useCallback<any>(
    debounce((offset, start) => {
      if (sessions.length) {
        formattedSessions.current = formattedSessions.current.map((s, i) => ({
          ...s,
          start: addDays(new Date(sessions[i].start), offset).toISOString(),
          end: addDays(new Date(sessions[i].end), offset).toISOString()
        }));

        setMonths(getAllMonthsWithSessions(formattedSessions.current, start));
      }
    }, 500),
    [sessions, formattedSessions.current]
  );

  const updateValues = useCallback<any>(
    (formatted, start) => {
      formattedSessions.current = formatted;

      if (sessions.length) {
        setMonths(getAllMonthsWithSessions(formatted, start));
      }
    },
    [sessions]
  );

  const onClose = useCallback(() => {
    setDialogOpened(false);
    reset();
  }, []);

  const onSubmit = useCallback(
    values => {
      values.classIds = selection;
      delete values.toDate;

      duplicateCourseClass(values, () => {
        onClose();
        closeMenu();
      });
    },
    [selection]
  );

  const handleDateChange = useCallback(
    (value: any) => {
      const dateValue = new Date(value);
      dateValue.setHours(getHours(earliest), getMinutes(earliest), getSeconds(earliest), getMilliseconds(earliest));

      const daysOffset = differenceInDays(dateValue, earliest);
      dispatch(change(DUPLICATE_COURSE_CLASS_FORM, "daysTo", daysOffset));

      debounceUpdateDaysOffset(daysOffset, addDays(earliest, daysOffset));
    },
    [earliest]
  );

  const handleDaysToChange = useCallback(
    (e: any) => {
      const value = Number(e.target.value);

      if (!isNaN(value)) {
        debounceUpdateDaysOffset(value, addDays(earliest, value));
        dispatch(change(DUPLICATE_COURSE_CLASS_FORM, "toDate", addDays(earliest, value).toISOString()));
      }
    },
    [earliest]
  );

  const onValuesChange = useCallback<any>(
    debounce((prop, value, toDate, replace) => {
      if (value) {
        updateValues(
          formattedSessions.current.map((s, i) => ({
            ...s,
            [prop]: sessions[i][prop]
          })),
          new Date(toDate)
        );
      } else {
        updateValues(
          formattedSessions.current.map(s => ({
            ...s,
            [prop]: replace
          })),
          new Date(toDate)
        );
      }
    }, 500),
    [sessions]
  );

  const onTutorsChange = useCallback<any>(val => onValuesChange("tutors", val, values.toDate, []), [values.toDate]);
  const onTagsChange = useCallback<any>(val => onValuesChange("tags", val, values.toDate, {}), [values.toDate]);
  const onSiteAndRoomsChange = useCallback<any>(
    debounce(val => {
      if (val) {
        updateValues(
          formattedSessions.current.map((s, i) => ({
              ...s,
              room: sessions[i] ? sessions[i].room : null,
              site: sessions[i] ? sessions[i].site : null
            })),
          new Date(values.toDate)
        );
      } else {
        updateValues(
          formattedSessions.current.map(s => ({
            ...s,
            room: null,
            site: null
          })),
          new Date(values.toDate)
        );
      }
    }, 500),
    [values.toDate]
  );

  return (
    <Dialog
      open={opened}
      onClose={onClose}
      classes={{
        paper: classes.root
      }}
      disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
      disableBackdropClick={disableClose}
      disableEscapeKeyDown={disableClose}
      onKeyDown={e => e.stopPropagation()}
    >
      <form autoComplete="off" noValidate onSubmit={handleSubmit(onSubmit)}>
        <DialogContent
          classes={{
            root: classes.dialogContent
          }}
        >
          <Grid container>
            <Grid item xs={4}>
              <div className={clsx("centeredFlex")}>
                <div className="heading mt-2 mb-2">
                  Duplicate
                  {' '}
                  {selection.length}
                  {' '}
                  class
                  {selection.length === 1 ? "" : "es"}
                </div>
              </div>

              {Boolean(sessions.length) && (
              <div className="pb-2 pr-2">
                Advance all classes by
                <FormField
                  type="number"
                  name="daysTo"
                  formatting="inline"
                  step="1"
                  onChange={handleDaysToChange}
                  disabled={fetching}
                  className={classes.daysInput}
                  hideArrows
                  required
                />
                {" "}
                days, so that the earliest class starts on
                {" "}
                <FormField
                  type="date"
                  name="toDate"
                  className={classes.dateTime}
                  formatting="inline"
                  onChange={handleDateChange}
                  disabled={fetching}
                  fullWidth
                  required
                />
              </div>
              )}

              <div>Copy to each new class:</div>
              <FormGroup>
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField
                      type="checkbox"
                      name="copyTutors"
                      color="secondary"
                      onChange={onTutorsChange}
                      disabled={fetching}
                    />
                  )}
                  label="Tutors for each session"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField
                      type="checkbox"
                      name="copySitesAndRooms"
                      color="secondary"
                      onChange={onSiteAndRoomsChange}
                      disabled={fetching}
                    />
                  )}
                  label="Site and room for each session"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={<FormField type="checkbox" name="copyCosts" color="secondary" disabled={fetching} />}
                  label={
                    hasZeroWages ? (
                      <Tooltip title="Found one or more classes with overridden by zero wage">
                        <Typography variant="body2" color="error">
                          Budget
                        </Typography>
                      </Tooltip>
                    ) : (
                      "Budget"
                    )
                  }
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="copyTrainingPlans" color="secondary" disabled={fetching} />
                  )}
                  label="Training plan"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="applyDiscounts" color="secondary" disabled={fetching} />
                  )}
                  label="Discounts"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="copyPayableTimeForSessions" color="secondary" disabled={fetching} />
                  )}
                  label="Payable time"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={<FormField type="checkbox" name="copyVetData" color="secondary" disabled={fetching} />}
                  label="VET fields"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={<FormField type="checkbox" name="copyAssessments" color="secondary" disabled={fetching} />}
                  label="Assessment task"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="copyOnlyMandatoryTags" color="secondary" onChange={onTagsChange} disabled={fetching} />
                  )}
                  label="Tags"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(<FormField type="checkbox" name="copyNotes" color="secondary" disabled={fetching} />)}
                  label="Class notes"
                />

                <FormControlLabel
                  classes={{
                    root: "mt-1",
                    label: "w-100"
                  }}
                  control={(
                    <StyledCheckbox
                      checked={changeCourse}
                      onChange={(e, v) => setChangeCourse(v)}
                    />
                  )}
                  label={(
                    <Typography variant="body2" color="inherit" component="span" onClick={e => e.preventDefault()} noWrap>
                      Change course for all classes to
                      <FormField
                        type="remoteDataSearchSelect"
                        entity="Course"
                        aqlFilter="currentlyOffered is true"
                        name="courseId"
                        selectValueMark="id"
                        selectLabelMark="name"
                        formatting="inline"
                        selectLabelCondition={v => v.name}
                        selectFilterCondition={courseFilterCondition}
                        validate={changeCourse ? validateSingleMandatoryField : undefined}
                        itemRenderer={CourseItemRenderer}
                        fullWidth
                        rowHeight={55}
                      />
                    </Typography>
                  )}
                />
              </FormGroup>
            </Grid>
            <Grid item xs={8} className={clsx("relative overflow-y-auto mt-2", classes.timetableContainer)}>
              <div className={clsx("absolute w-100 h-100", fetching && "centeredFlex justify-content-center")}>
                <DuplicateCourseClassTimetable months={months} fetching={fetching} />
              </div>
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions className="p-3">
          {!disableClose && (
            <MuiButton color="primary" onClick={onClose}>
              Cancel
            </MuiButton>
          )}

          <Button
            disabled={fetching || invalid || (hasZeroWages && values.copyCosts)}
            variant="contained"
            color="primary"
            type="submit"
          >
            Duplicate
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(DUPLICATE_COURSE_CLASS_FORM)(state),
  fetching: state.courseClass.timetable.fetching,
  earliest: state.courseClass.timetable.earliest,
  sessions: state.courseClass.timetable.sessions,
  courses: state.courses.items,
  coursesSearch: state.courses.search,
  coursesLoading: state.courses.loading,
  coursesRowsCount: state.courses.rowsCount,
  hasZeroWages: state.courseClass.timetable.hasZeroWages
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    duplicateCourseClass: (values: CourseClassDuplicate, onComplete) =>
      dispatch(duplicateCourseClass(values, onComplete)),
    getSessions: request => dispatch(getDuplicateCourseClassesSessions(request)),
    clearTimetable: () => dispatch(clearDuplicateCourseClassesSessions()),
    getCourses: (offset?: number) => dispatch(getPlainCourses(offset, "code,name,nextAvailableCode,reportableHours,isTraineeship", true)),
    clearCourses: () => dispatch(setPlainCourses([])),
    setCoursesSearch: (search: string) => dispatch(setPlainCoursesSearch(search ? `~"${search}" and` : "")),
});

export default reduxForm({
  form: DUPLICATE_COURSE_CLASS_FORM,
  initialValues
})(
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(modalStyles)(DuplicateCourseClassModal))
) as DecoratedComponentClass<CourseClassDuplicate & { toDate: string }, Props>;
