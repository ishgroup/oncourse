/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Grid from "@material-ui/core/Grid";
import {
  CourseClassAttendanceType,
  EnrolmentCreditTotal,
  EnrolmentCreditType,
  EnrolmentCreditProviderType,
  EnrolmentCreditLevel,
  EnrolmentFeeStatus,
  Enrolment
} from "@api/model";
import { Collapse, FormControlLabel } from "@material-ui/core";
import { change } from "redux-form";
import FormField from "../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../common/components/form/Uneditable";
import { EditViewProps } from "../../../../model/common/ListView";
import { mapSelectItems } from "../../../../common/utils/common";
import { decimalMul } from "../../../../common/utils/numbers/decimalCalculation";
import { Switch } from "../../../../common/components/form/form-fields/Switch";

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

const EnrolmentVetStudentLoans: React.FC<EditViewProps<Enrolment>> = (
  {
    twoColumn,
    values,
    form,
    dispatch
  }
) => {
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

  const onShowCheck = (e, checked) => {
    if (checked) {
      dispatch(change(form, "feeStatus", enrolmentFeeStatusItems[0].value));
    } else {
      dispatch(change(form, "feeStatus", null));
      dispatch(change(form, "feeHelpAmount", 0));
    }
  };

  return (
    <Grid container className="pl-3 pr-3">
      {values.feeHelpClass && (
        <>
          <Grid item xs={12} className="centeredFlex mt-2 mb-2">
            <FormControlLabel
              className="switchWrapper"
              control={<Switch checked={showVSL} onChange={onShowCheck} />}
              label={<span className="heading mr-1">VET Student Loans</span>}
              labelPlacement="start"
            />
          </Grid>

          <Grid item xs={12}>
            <Collapse in={showVSL}>
              <Grid container item={true} xs={12}>
                <Grid item xs={twoColumn ? 3 : 12}>
                  <Uneditable label="Fee charged" value={values.feeCharged} money />
                </Grid>
                <Grid item xs={twoColumn ? 3 : 12}>
                  <FormField type="money" name="feeHelpAmount" label="Fee help requested" />
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
                    allowEmpty
                  />
                </Grid>
                <Grid item xs={twoColumn ? 6 : 12}>
                  <FormField
                    type="select"
                    name="attendanceType"
                    label="Type of attendance"
                    items={courseClassAttendanceTypeItems}
                  />
                </Grid>
              </Grid>
            </Collapse>
          </Grid>
        </>
      )}

      <Grid item xs={12} className="centeredFlex">
        <div className="heading mt-2 mb-2">Credit and rpl</div>
      </Grid>

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
  );
};

export default EnrolmentVetStudentLoans;
