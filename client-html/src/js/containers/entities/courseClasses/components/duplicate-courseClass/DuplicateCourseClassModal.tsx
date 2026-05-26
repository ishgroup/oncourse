/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassDuplicate } from '@api/model';
import { Grid, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import Tooltip from '@mui/material/Tooltip';
import $t from '@t';
import clsx from 'clsx';
import { addDays, differenceInDays, getHours, getMilliseconds, getMinutes, getSeconds } from 'date-fns';
import { BooleanArgFunction, NoArgFunction, StyledCheckbox } from 'ish-ui';
import { debounce } from 'es-toolkit/compat';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, DecoratedComponentClass, getFormValues, InjectedFormProps, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { validateSingleMandatoryField } from '../../../../../common/utils/validation';
import { TimetableMonth, TimetableSession } from '../../../../../model/timetable';
import { State } from '../../../../../reducers/state';
import { getAllMonthsWithSessions } from '../../../../timetable/utils';
import CourseItemRenderer from '../../../courses/components/CourseItemRenderer';
import { courseFilterCondition } from '../../../courses/utils';
import {
  clearDuplicateCourseClassesSessions,
  duplicateCourseClass,
  getDuplicateCourseClassesSessions
} from '../../actions';
import DuplicateCourseClassTimetable from './DuplicateCourseClassTimetable';
import modalStyles from './modalStyles';

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
  tutorRosterOverrides: true,
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
    form,
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

  const onChangeCourseSelect = (e, v) => {
    if (v) {
      dispatch(change(DUPLICATE_COURSE_CLASS_FORM, "copyTrainingPlans", false));
    } else {
      dispatch(change(DUPLICATE_COURSE_CLASS_FORM, "courseId", null));
    }
    setChangeCourse(v);
  };

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

  const onTutorsChange = useCallback<any>(val => {
    onValuesChange("tutors", val, values.toDate, []);
    if (!val) {
      dispatch(change(form, "tutorRosterOverrides", false));
    }
  }, [values.toDate]);

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
      disableEscapeKeyDown={disableClose}
      onKeyDown={e => e.stopPropagation()}
    >
      <form autoComplete="off" noValidate onSubmit={handleSubmit(onSubmit)} role={DUPLICATE_COURSE_CLASS_FORM}>
        <DialogContent
          classes={{
            root: classes.dialogContent
          }}
        >
          <Grid container columnSpacing={3}>
            <Grid item xs={4}>
              <div className={clsx("centeredFlex")}>
                <div className="heading mt-2 mb-2">
                  {$t('duplicate_class',[selection.length])}
                  {selection.length === 1 ? "" : "es"}
                </div>
              </div>

              {Boolean(sessions.length) && (
                <div className="pb-2 pr-2">
                  {$t('advance_all_classes_by')}
                  {" "}
                  <FormField
                    type="number"
                    name="daysTo"
                    inline
                    step="1"
                    onChange={handleDaysToChange}
                    debounced={false}
                    disabled={fetching}
                    required
                  />
                  {" "}
                  {$t('days_so_that_the_earliest_class_starts_on')}
                  {" "}
                  <FormField
                    type="date"
                    name="toDate"
                    onChange={handleDateChange}
                    debounced={false}
                    disabled={fetching}
                    required
                    inline
                  />
                </div>
              )}

              <div>{$t('copy_to_each_new_class')}</div>
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
                      debounced={false}
                      disabled={fetching}
                    />
                  )}
                  label={$t('tutors')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField
                      type="checkbox"
                      name="tutorRosterOverrides"
                      color="secondary"
                      disabled={fetching || !values.copyTutors}
                    />
                  )}
                  label={$t('tutor_roster_overrides')}
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
                      debounced={false}
                      disabled={fetching}
                    />
                  )}
                  label={$t('site_and_room_for_each_session')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={<FormField type="checkbox" name="copyCosts" color="secondary" disabled={fetching} />}
                  label={
                    hasZeroWages ? (
                      <Tooltip title={$t('found_one_or_more_classes_with_overridden_by_zero')}>
                        <Typography variant="body2" color="error">
                          {$t('budget')}
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
                    <FormField type="checkbox" name="copyTrainingPlans" color="secondary" disabled={fetching || changeCourse} />
                  )}
                  label={$t('training_plan')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="applyDiscounts" color="secondary" disabled={fetching} />
                  )}
                  label={$t('discounts')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={<FormField type="checkbox" name="copyVetData" color="secondary" disabled={fetching} />}
                  label={$t('vet_fields')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={<FormField type="checkbox" name="copyAssessments" color="secondary" disabled={fetching} />}
                  label={$t('assessment_task')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="copyOnlyMandatoryTags" color="secondary" onChange={onTagsChange} debounced={false} disabled={fetching} />
                  )}
                  label={$t('tags')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(<FormField type="checkbox" name="copyNotes" color="secondary" disabled={fetching} />)}
                  label={$t('class_notes')}
                />

                <FormControlLabel
                  classes={{
                    root: "mt-1",
                    label: "w-100"
                  }}
                  control={(
                    <StyledCheckbox
                      checked={changeCourse}
                      onChange={onChangeCourseSelect}
                    />
                  )}
                  label={(
                    <Typography variant="body2" color="inherit" component="span" onClick={e => e.preventDefault()} noWrap>
                      {$t('change_course_for_all_classes_to')}
                      {" "}
                      <FormField
                        type="remoteDataSelect"
                        entity="Course"
                        aqlFilter="currentlyOffered is true"
                        name="courseId"
                        selectValueMark="id"
                        selectLabelMark="name"
                        inline
                        selectLabelCondition={v => v.name}
                        selectFilterCondition={courseFilterCondition}
                        validate={changeCourse ? validateSingleMandatoryField : undefined}
                        itemRenderer={CourseItemRenderer}
                                                rowHeight={55}
                      />
                    </Typography>
                  )}
                />
              </FormGroup>
            </Grid>
            <Grid item xs={8} className={clsx("relative overflow-y-auto mt-2", classes.timetableContainer)}>
              <div className={clsx("absolute w-100 h-100 pl-3 pr-3", fetching && "centeredFlex justify-content-center")}>
                <DuplicateCourseClassTimetable months={months} fetching={fetching} />
              </div>
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions className="p-3">
          {!disableClose && (
            <Button color="primary" onClick={onClose}>
              {$t('cancel')}
            </Button>
          )}

          <Button
            disabled={fetching || invalid || (hasZeroWages && values.copyCosts)}
            variant="contained"
            color="primary"
            type="submit"
          >
            {$t('duplicate')}
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
  hasZeroWages: state.courseClass.timetable.hasZeroWages
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    duplicateCourseClass: (values: CourseClassDuplicate, onComplete) =>
      dispatch(duplicateCourseClass(values, onComplete)),
    getSessions: request => dispatch(getDuplicateCourseClassesSessions(request)),
    clearTimetable: () => dispatch(clearDuplicateCourseClassesSessions())
});

export default reduxForm({
  form: DUPLICATE_COURSE_CLASS_FORM,
  initialValues
})(
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(DuplicateCourseClassModal, modalStyles))
) as DecoratedComponentClass<CourseClassDuplicate & { toDate: string }, Props>;
