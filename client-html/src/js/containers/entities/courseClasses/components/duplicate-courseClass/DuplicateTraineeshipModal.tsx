/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, ClassCost, CourseClassDuplicate, Tax } from '@api/model';
import { Button, FormControlLabel, Grid, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Tooltip from '@mui/material/Tooltip';
import $t from '@t';
import clsx from 'clsx';
import { addDays, differenceInDays, getHours, getMilliseconds, getMinutes, getSeconds } from 'date-fns';
import { BooleanArgFunction, NoArgFunction, NumberArgFunction } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import {
  change,
  DecoratedComponentClass,
  FormSection,
  getFormValues,
  initialize,
  InjectedFormProps,
  reduxForm
} from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../../common/components/form/formFields/FormField';
import EntityService from '../../../../../common/services/EntityService';
import history from '../../../../../constants/History';
import { TimetableMonth, TimetableSession } from '../../../../../model/timetable';
import { State } from '../../../../../reducers/state';
import { getAllMonthsWithSessions } from '../../../../timetable/utils';
import { getPlainAccounts } from '../../../accounts/actions';
import { getPlainTaxes } from '../../../taxes/actions';
import {
  clearDuplicateCourseClassesSessions,
  duplicateCourseClass,
  getDuplicateCourseClassesBudget,
  getDuplicateCourseClassesSessions,
  setDuplicateCourseClassesBudget
} from '../../actions';
import StudentFeeContent from '../budget/modal/StudentFeeContent';
import DuplicateCourseClassTimetable from './DuplicateCourseClassTimetable';
import modalStyles from './modalStyles';

export const DUPLICATE_TRAINEESHIP_FORM: string = "DuplicateTraineeshipForm";

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
  getBudget?: NumberArgFunction;
  clearBudget?: any;
  budget?: ClassCost[];
  accounts?: Account[];
  taxes?: Tax[];
  getAccounts?: any;
  getTaxes?: any;
  currencySymbol?: string;
}

const initialValues: CourseClassDuplicate & { toDate: string } = {
  classIds: [],
  classCost: {},
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
    hasZeroWages,
    getBudget,
    clearBudget,
    budget,
    accounts,
    taxes,
    getAccounts,
    getTaxes,
    currencySymbol
  } = props;

  const [months, setMonths] = useState<TimetableMonth[]>([]);
  const [taxId, setTaxId] = useState<number>(null);
  const [selectedTab, setSelectedTab] = useState<number>(0);
  const formattedSessions = useRef<TimetableSession[]>([]);

  const studentFee = useMemo(() => budget.find(b => b.invoiceToStudent), [budget]);

  const incomeAccounts = useMemo(() => accounts.filter(a => a.type === "income"), [accounts]);

  useEffect(() => {
    if (opened) {
      getAccounts();
      getTaxes();
      getSessions(selection);
      getBudget(Number(selection[0]));

      EntityService.getPlainRecords(
        "CourseClass",
        "tax.id",
        `id is ${selection[0]}`
        )
        .then(res => {
          setTaxId(JSON.parse(res.rows[0].values[0]));
        })
        .catch(e => console.error(e));
    } else {
      clearTimetable();
      clearBudget();
      setMonths([]);
      setSelectedTab(0);
      setTaxId(null);
      dispatch(initialize(DUPLICATE_TRAINEESHIP_FORM, initialValues));
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
      dispatch(change(DUPLICATE_TRAINEESHIP_FORM, "toDate", earliest.toISOString()));
    }
  }, [earliest]);

  useEffect(() => {
    if (studentFee) {
      dispatch(initialize(DUPLICATE_TRAINEESHIP_FORM, { ...values, classCost: studentFee }));
    }
  }, [studentFee]);

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

      duplicateCourseClass(values, ids => {
        onClose();
        closeMenu();
        history.push(`/checkout?courseClassId=${ids[0]}`);
      });
    },
    [selection]
  );

  const handleDateChange = useCallback(
    (value: any) => {
      const dateValue = new Date(value);
      dateValue.setHours(getHours(earliest), getMinutes(earliest), getSeconds(earliest), getMilliseconds(earliest));

      const daysOffset = differenceInDays(dateValue, earliest);
      dispatch(change(DUPLICATE_TRAINEESHIP_FORM, "daysTo", daysOffset));

      debounceUpdateDaysOffset(daysOffset, addDays(earliest, daysOffset));
    },
    [earliest]
  );

  const handleDaysToChange = useCallback(
    (e: any) => {
      const value = Number(e.target.value);

      if (!isNaN(value)) {
        debounceUpdateDaysOffset(value, addDays(earliest, value));
        dispatch(change(DUPLICATE_TRAINEESHIP_FORM, "toDate", addDays(earliest, value).toISOString()));
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
      disableEscapeKeyDown={disableClose}
      onKeyDown={e => e.stopPropagation()}
    >
      <form autoComplete="off" noValidate onSubmit={handleSubmit(onSubmit)} role={DUPLICATE_TRAINEESHIP_FORM}>
        <DialogContent
          classes={{
            root: classes.dialogContent
          }}
        >
          <Grid container columnSpacing={3}>
            <Grid item xs={4}>
              <div className="centeredFlex">
                <div className="heading mt-2 mb-2">
                  {$t('duplicate_traineeship_class')}
                </div>
              </div>
              {Boolean(sessions.length) && (
                <div className="pb-2 pr-2">
                  {$t('advance_class_by')}
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
                  {$t('days_so_that_it_is_starts_on')}
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
              <div>{$t('copy_to_new_class')}</div>
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
                  label={$t('tutors_for_each_session')}
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
                  control={
                    <FormField type="checkbox" name="copyTrainingPlans" color="secondary" disabled={fetching} />
                  }
                  label={$t('training_plan')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={
                    <FormField type="checkbox" name="applyDiscounts" color="secondary" disabled={fetching} />
                  }
                  label={$t('discounts')}
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField type="checkbox" name="tutorRosterOverrides" color="secondary" disabled={fetching} />
                  )}
                  label={$t('payable_time')}
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
                  control={
                    <FormField type="checkbox" name="copyAssessments" color="secondary" disabled={fetching} />
                  }
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
                  control={<FormField type="checkbox" name="copyNotes" color="secondary" disabled={fetching} />}
                  label={$t('class_notes')}
                />
              </FormGroup>
            </Grid>

            <Grid item xs={8} className="flex-column">
              <Tabs
                value={selectedTab}
                onChange={(e, v) => setSelectedTab(v)}
                indicatorColor="primary"
              >
                <Tab label={(
                  <div className={clsx("heading", selectedTab !== 0 && "text-disabled")}>
                    {$t('timetable')}
                  </div>
                )}
                />
                <Tab
                  disabled={!budget.length}
                  label={(
                    <div className={clsx("heading", selectedTab !== 1 && "text-disabled")}>
                      {$t('budget_override')}
                    </div>
                )}
                />
              </Tabs>

              <div className={clsx("relative overflow-y-auto flex-fill", classes.timetableContainer)}>
                <div className={clsx("absolute w-100 h-100 pl-3 pr-3", fetching && "centeredFlex justify-content-center")}>
                  {selectedTab === 0 && <DuplicateCourseClassTimetable months={months} fetching={fetching} />}
                  {selectedTab === 1
                    && (
                      <div className="p-3">
                        <FormSection name="classCost">
                          <StudentFeeContent
                            form={DUPLICATE_TRAINEESHIP_FORM}
                            namePrefix="classCost."
                            currentTax={taxes.find(t => t.id === taxId) || {}}
                            classValues={{
                              startDateTime: sessions && sessions.length
                                ? sessions[0].start : (new Date()).toISOString()
                            }}
                            currencySymbol={currencySymbol}
                            values={values.classCost as any}
                            dispatch={dispatch}
                            accounts={incomeAccounts}
                            taxes={taxes}
                          />
                        </FormSection>
                      </div>
                  )}
                </div>
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
            {$t('duplicate_and_enrol')}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  taxes: state.taxes.items,
  accounts: state.plainSearchRecords.Account.items,
  values: getFormValues(DUPLICATE_TRAINEESHIP_FORM)(state),
  budget: state.courseClass.duplicateTraineeshipBudget,
  fetching: state.courseClass.timetable.fetching,
  earliest: state.courseClass.timetable.earliest,
  sessions: state.courseClass.timetable.sessions,
  hasZeroWages: state.courseClass.timetable.hasZeroWages,
  currencySymbol: state.location.currency.shortCurrencySymbol
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  duplicateCourseClass: (values: CourseClassDuplicate, onComplete) =>
    dispatch(duplicateCourseClass(values, onComplete)),
  getSessions: request => dispatch(getDuplicateCourseClassesSessions(request)),
  clearTimetable: () => dispatch(clearDuplicateCourseClassesSessions()),
  getBudget: id => dispatch(getDuplicateCourseClassesBudget(id)),
  clearBudget: () => dispatch(setDuplicateCourseClassesBudget([])),
  getAccounts: () => getPlainAccounts(dispatch),
  getTaxes: () => dispatch(getPlainTaxes())
});

export default reduxForm({
  form: DUPLICATE_TRAINEESHIP_FORM,
  initialValues
})(
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(DuplicateCourseClassModal, modalStyles))
) as DecoratedComponentClass<CourseClassDuplicate & { toDate: string }, Props>;
