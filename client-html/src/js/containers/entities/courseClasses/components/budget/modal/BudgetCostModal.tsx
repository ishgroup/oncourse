/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { getFormValues, InjectedFormProps, reduxForm, submit } from "redux-form";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import { connect } from "react-redux";
import { Account, ClassCostRepetitionType, Tax } from "@api/model";
import Button from "../../../../../../common/components/buttons/Button";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../../constants";
import { State } from "../../../../../../reducers/state";
import StudentFeeContent from "./StudentFeeContent";
import IncomeAndExpenceContent from "./IncomeAndExpenceContent";
import DiscountContent from "./DiscountContent";
import { ClassCostExtended, CourseClassExtended } from "../../../../../../model/entities/CourseClass";
import TutorPayContent from "./TutorPayContent";
import { mapSelectItems } from "../../../../../../common/utils/common";

export const PayRateTypes = Object.keys(ClassCostRepetitionType).filter(t => t !== "Discount").map(mapSelectItems);

interface CourseClassCostModalProps {
  opened?: boolean;
  classes?: any;
  onClose: any;
  onSave: any;
  classValues?: CourseClassExtended;
  values?: ClassCostExtended;
  taxes: Tax[];
  currentTax: Tax;
  accounts?: Account[];
  tutorRoles?: any[];
  dispatch?: any;
  currencySymbol?: string;
  defaultOnCostRate?: number;
  classFee?: number;
}

const BudgetCostModal = React.memo<CourseClassCostModalProps & InjectedFormProps>(
  ({
    opened,
    onClose,
    values = {},
    classValues,
    taxes,
    tutorRoles,
    accounts,
    dispatch,
    currencySymbol,
    dirty,
    invalid,
    defaultOnCostRate,
    currentTax,
    classFee
  }) => {
    const incomeAccounts = useMemo(() => accounts.filter(a => a.type === "income"), [accounts]);
    const activeTutorRoles = useMemo(() => (tutorRoles ? tutorRoles.filter(t => t.active) : []), [
      tutorRoles
    ]);

    const costLabel = useMemo(() => (values.repetitionType === "Fixed" ? "Cost" : "Cost each"), [
      values.repetitionType
    ]);

    const hasMinMaxFields = useMemo(
      () => ["Per session", "Per timetabled hour", "Per student contact hour"].includes(values.repetitionType),
      [values.repetitionType]
    );

    const hasCountField = useMemo(() => values.repetitionType === "Per unit", [values.repetitionType]);

    const content = useMemo(() => {
      switch (values.flowType) {
        default:
        case "Income":
          if (values.invoiceToStudent) {
            return (
              <StudentFeeContent
                values={values}
                classValues={classValues}
                taxes={taxes}
                currencySymbol={currencySymbol}
                dispatch={dispatch}
                accounts={incomeAccounts}
                currentTax={currentTax}
                form={COURSE_CLASS_COST_DIALOG_FORM}
              />
            );
          }
          return (
            <IncomeAndExpenceContent
              values={values}
              taxes={taxes}
              dispatch={dispatch}
              costLabel={costLabel}
              hasMinMaxFields={hasMinMaxFields}
              hasCountField={hasCountField}
            />
          );
        case "Discount":
          return (
            <DiscountContent
              currentTax={currentTax}
              classFee={classFee}
              values={values}
              classValues={classValues}
              dispatch={dispatch}
              currencySymbol={currencySymbol}
            />
          );
        case "Wages":
          return (
            <TutorPayContent
              values={values}
              dispatch={dispatch}
              classValues={classValues}
              currencySymbol={currencySymbol}
              defaultOnCostRate={defaultOnCostRate}
              tutorRoles={activeTutorRoles}
              costLabel={costLabel}
              hasMinMaxFields={hasMinMaxFields}
              hasCountField={hasCountField}
            />
          );
      }
    }, [
      values,
      classValues,
      currencySymbol,
      taxes,
      activeTutorRoles,
      hasMinMaxFields,
      hasCountField,
      incomeAccounts,
      classFee,
      currentTax
    ]);

    const header = useMemo(
      () => ( values.flowType === "Income" && values.repetitionType === "Per enrolment" && values.invoiceToStudent
        ? "Student fee" : values.flowType === "Wages"
          ? "Tutor pay" : values.flowType),
      [values.invoiceToStudent, values.flowType]
    );

    const onSubmit = useCallback(() => {
      dispatch(submit(COURSE_CLASS_COST_DIALOG_FORM));
    }, [values]);

    return (
      <Dialog
        open={opened}
        onClose={onClose}
        fullWidth
        maxWidth="md"
        classes={{
          paper: "overflow-auto"
        }}
      >
        <DialogTitle>
          <div className="heading pt-1">{header}</div>
        </DialogTitle>
        <DialogContent
          classes={{
            root: "overflow-auto"
          }}
        >
          {content}
        </DialogContent>
        <DialogActions className="p-3">
          <MuiButton color="primary" onClick={onClose}>
            Cancel
          </MuiButton>
          <Button color="primary" onClick={onSubmit} disabled={(values.id && !dirty) || invalid}>
            Save
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
);

const mapStateToProps = (state: State) => ({
  values: getFormValues(COURSE_CLASS_COST_DIALOG_FORM)(state),
  accounts: state.plainSearchRecords.Account.items,
  tutorRoles: state.preferences.tutorRoles,
  opened: state.courseClass.budgetModalOpened,
  defaultOnCostRate: state.courseClass.defaultOnCostRate
});

export default reduxForm<any, CourseClassCostModalProps>({
  form: COURSE_CLASS_COST_DIALOG_FORM
})(connect(mapStateToProps, null)(BudgetCostModal));
