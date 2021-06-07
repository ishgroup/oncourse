/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Tab from "@material-ui/core/Tab";
import Tabs from "@material-ui/core/Tabs";
import React, {
 useCallback, useEffect, useMemo, useRef, useState
} from "react";
import clsx from "clsx";
import debounce from "lodash.debounce";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  change,
  DecoratedComponentClass,
  FormSection,
  getFormValues,
  initialize,
  InjectedFormProps,
  reduxForm
} from "redux-form";
import withStyles from "@material-ui/core/styles/withStyles";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import DialogContent from "@material-ui/core/DialogContent";
import Grid from "@material-ui/core/Grid/Grid";
import FormGroup from "@material-ui/core/FormGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import {
 Account, ClassCost, CourseClassDuplicate, Tax
} from "@api/model";
import {
 addDays, differenceInDays, getHours, getMilliseconds, getMinutes, getSeconds
} from "date-fns";
import { Typography } from "@material-ui/core";
import Tooltip from "@material-ui/core/Tooltip";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import EntityService from "../../../../../common/services/EntityService";
import { State } from "../../../../../reducers/state";
import Button from "../../../../../common/components/buttons/Button";
import { getPlainAccounts } from "../../../accounts/actions";
import { getPlainTaxes } from "../../../taxes/actions";
import {
  clearDuplicateCourseClassesSessions,
  duplicateCourseClass,
  getDuplicateCourseClassesBudget,
  getDuplicateCourseClassesSessions,
  setDuplicateCourseClassesBudget
} from "../../actions";
import { BooleanArgFunction, NoArgFunction, NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import { TimetableMonth, TimetableSession } from "../../../../../model/timetable";
import StudentFeeContent from "../budget/modal/StudentFeeContent";
import DuplicateCourseClassTimetable from "./DuplicateCourseClassTimetable";
import { getAllMonthsWithSessions } from "../../../../timetable/utils";
import modalStyles from "./modalStyles";
import history from "../../../../../constants/History";

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
        ).then(res => {
          setTaxId(JSON.parse(res.rows[0].values[0]));
      });
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
              <div className="centeredFlex">
                <div className="heading mt-2 mb-2">
                  Duplicate traineeship class
                </div>
              </div>
              {Boolean(sessions.length) && (
                <div className="pb-2 pr-2">
                  Advance class by
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
                  days, so that it is starts on
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
              <div>Copy to new class:</div>
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
                  control={
                    <FormField type="checkbox" name="copyTrainingPlans" color="secondary" disabled={fetching} />
                  }
                  label="Training plan"
                />
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={
                    <FormField type="checkbox" name="applyDiscounts" color="secondary" disabled={fetching} />
                  }
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
                  control={
                    <FormField type="checkbox" name="copyAssessments" color="secondary" disabled={fetching} />
                  }
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
                  control={<FormField type="checkbox" name="copyNotes" color="secondary" disabled={fetching} />}
                  label="Class notes"
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
                    Timetable
                  </div>
                )}
                />
                <Tab
                  disabled={!budget.length}
                  label={(
                    <div className={clsx("heading", selectedTab !== 1 && "text-disabled")}>
                      Budget override
                    </div>
                )}
                />
              </Tabs>

              <div className={clsx("relative overflow-y-auto flex-fill", classes.timetableContainer)}>
                <div className={clsx("absolute w-100 h-100", fetching && "centeredFlex justify-content-center")}>
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
            Duplicate and enrol
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
  currencySymbol: state.currency.shortCurrencySymbol
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
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(modalStyles)(DuplicateCourseClassModal))
) as DecoratedComponentClass<CourseClassDuplicate & { toDate: string }, Props>;
