/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  CourseClassAttendanceType,
  Enrolment,
  EnrolmentCreditLevel,
  EnrolmentCreditProviderType,
  EnrolmentCreditTotal,
  EnrolmentCreditType,
  EnrolmentFeeStatus,
  EnrolmentReportingStatus,
} from "@api/model";
import { Collapse, Grid } from "@mui/material";
import { decimalMul, mapSelectItems } from "ish-ui";
import React, { useMemo } from "react";
import { change } from "redux-form";
import FormField from "../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { EditViewProps } from "../../../../model/common/ListView";

const validateCharacter = (value, len, msg) => (value && value.length > len ? msg : undefined);

const validateCreditOfferedValue = value => validateCharacter(value, 4, "Credit offered value is not valid. It must contain 4 or fewer characters");

const validateCreditUsedValue = value => validateCharacter(value, 4, "Credit used value is not valid. It must contain 4 or fewer characters");

const validateCreditFoeId = value => validateCharacter(value, 4, "Credit field of education ID is not valid. It must contain 4 or fewer characters");

const validateCreditOfferedProviderCode = value => validateCharacter(value, 4, "Credit offered provider code is not valid. It must contain 4 or fewer characters");

const courseClassAttendanceTypeItems = Object.keys(CourseClassAttendanceType).map(mapSelectItems);

const enrolmentFeeStatusKeys = Object.keys(EnrolmentFeeStatus);

const enrolmentFeeStatusItems = enrolmentFeeStatusKeys.map(mapSelectItems);

const enrolmentCreditTypeItems = Object.keys(EnrolmentCreditType).map(mapSelectItems);

const enrolmentCreditProviderTypeItems = Object.keys(EnrolmentCreditProviderType).map(mapSelectItems);

const enrolmentCreditLevelItems = Object.keys(EnrolmentCreditLevel).map(mapSelectItems);

const enrolmentCreditTotalItems = Object.keys(EnrolmentCreditTotal).map(mapSelectItems);

const enrolmentReportingStatusItems = Object.keys(EnrolmentReportingStatus).map(mapSelectItems);

const EnrolmentVetStudentLoans: React.FC<EditViewProps<Enrolment> & { namePrefix?: string }> = (
  {
    twoColumn,
    values,
    form,
    dispatch,
    expanded,
    setExpanded,
    syncErrors,
    namePrefix
  }
) => {
  const getName = (name: string) => namePrefix ? `${namePrefix}.${name}` : name;

  const loanData = useMemo(() => {
    let loanFee = 0;
    let loanTotal = values.feeHelpAmount;

    if (
      [null, EnrolmentFeeStatus[enrolmentFeeStatusKeys[0]], EnrolmentFeeStatus[enrolmentFeeStatusKeys[1]]].includes(
        values.feeStatus
      )
    ) {
      loanFee = decimalMul(0.2, values.feeHelpAmount);
      loanTotal = decimalMul(1.2, values.feeHelpAmount);
    }

    return { loanFee, loanTotal };
  }, [values.feeHelpAmount, values.feeStatus]);

  const showVSL = Boolean(values.feeHelpAmount) || values.feeStatus !== null;

  const onChangeSelectValue = e => {
    switch (e) {
      case 'Eligible':
      case 'Not eligible':
        dispatch(change(form, getName("feeStatus"), null));
        dispatch(change(form, getName("feeHelpAmount"), 0));
        break;
      case 'Ongoing':
      case 'Finalized':
        dispatch(change(form, getName("feeStatus"), enrolmentFeeStatusItems[0].value));
        break;
      default:
        break;
    }
  };

  return (
    <>
      {values.feeHelpClass && (
        <ExpandableContainer
          expanded={expanded}
          setExpanded={setExpanded}
          formErrors={syncErrors}
          header="VET Student Loans"
          index="VET Student Loans"
        >
          <Grid container>
            <Grid item xs={twoColumn ? 6 : 12} className="mb-1 mt-1">
              <FormField
                type="select"
                name="studentLoanStatus"
                label="Reporting status"
                items={enrolmentReportingStatusItems}
                onChange={onChangeSelectValue}
                debounced={false}
              />
            </Grid>
            <Collapse in={showVSL}>
              <Grid container columnSpacing={3} rowSpacing={2} className="mb-1">
                <Grid item xs={twoColumn ? 3 : 12}>
                  <FormField
                    type="money"
                    name="feeHelpAmount"
                    label="Fee help requested"
                    disabled={values.studentLoanStatus === "Finalized"}
                  />
                </Grid>
                <Grid item xs={twoColumn ? 3 : 12}>
                  <Uneditable label="Loan fee" value={loanData.loanFee} money />
                </Grid>
                <Grid item xs={twoColumn ? 3 : 12}>
                  <Uneditable label="Total loan" value={loanData.loanTotal} money />
                </Grid>
                <Grid item xs={twoColumn ? 6 : 12}>
                  <FormField
                    type="select"
                    name="feeStatus"
                    label="Fee subsidy"
                    items={enrolmentFeeStatusItems}
                    disabled={values.studentLoanStatus === "Finalized"}
                    allowEmpty
                  />
                </Grid>
                <Grid item xs={twoColumn ? 6 : 12}>
                  <FormField
                    type="select"
                    name="attendanceType"
                    label="Type of attendance"
                    items={courseClassAttendanceTypeItems}
                    disabled={values.studentLoanStatus === "Finalized"}
                  />
                </Grid>
              </Grid>
            </Collapse>
          </Grid>
        </ExpandableContainer>
      )}

      <ExpandableContainer
        expanded={expanded}
        setExpanded={setExpanded}
        formErrors={syncErrors}
        header="Credit and rpl"
        index="Credit and rpl"
      >
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="creditOfferedValue"
              label="Credit offered value"
              validate={validateCreditOfferedValue}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="creditUsedValue"
              label="Credit used value"
              validate={validateCreditUsedValue}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="creditTotal"
              label="RPL indicator"
              items={enrolmentCreditTotalItems}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="creditFOEId"
              label="Credit field of education ID"
              validate={validateCreditFoeId}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="creditProvider"
              label="Credit offered provider code"
              validate={validateCreditOfferedProviderCode}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="creditProviderType"
              label="Credit provider type"
              items={enrolmentCreditProviderTypeItems}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="creditType"
              label="Credit type"
              items={enrolmentCreditTypeItems}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="creditLevel"
              label="Credit level"
              items={enrolmentCreditLevelItems}
            />
          </Grid>
        </Grid>
      </ExpandableContainer>
    </>
  );
};

export default EnrolmentVetStudentLoans;