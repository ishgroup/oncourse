/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useMemo
} from "react";
import { arrayInsert, arrayRemove, change } from "redux-form";
import { CourseClassPaymentPlan, Tax } from "@api/model";
import Grid from "@mui/material/Grid";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import Typography from "@mui/material/Typography";
import { addDays, format } from "date-fns";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import {
  decimalDivide,
  decimalMinus,
  decimalMul,
  decimalPlus
} from "../../../../../../common/utils/numbers/decimalCalculation";
import {
  formatCurrency,
  normalizeNumber
} from "../../../../../../common/utils/numbers/numbersNormalizing";
import { accountLabelCondition } from "../../../../accounts/utils";
import { D_MMM_YYYY } from "../../../../../../common/utils/dates/format";
import { BudgetCostModalContentProps } from "../../../../../../model/entities/CourseClass";
import { getCurrentTax } from "../../../../taxes/utils";
import { getPaymentPlansTotal } from "../utils";
import AddButton from "../../../../../../../ish-ui/buttons/AddButton";
import { IS_JEST } from "../../../../../../constants/EnvironmentConstants";

const StudentFeePaymentPlan: React.FC<any> = ({
 index, item, onDelete, onBlur, classStart
  }) => {
  const onDeleteClick = useCallback(() => onDelete(index), []);

  const offsetDate = useMemo(() => (classStart
    ? format(addDays(new Date(classStart), item.dayOffset || 0), D_MMM_YYYY)
    : null),
     [
    item.dayOffset,
    classStart
  ]);

  const name = `paymentPlan[${index}]`;
  const fieldAmountProps = IS_JEST ? {
    'data-testid': `${name}.amount`
  } : {};

  return (
    <>
      <Grid item xs={8}>
        <FormField
          type="number"
          name={`${name}.dayOffset`}
          label={`Days after start ${offsetDate ? `(${offsetDate})` : ""}`}
          normalize={normalizeNumber}
          onBlur={onBlur}
          debounced={false}
        />
      </Grid>
      <Grid item xs={3}>
        <FormField type="money" name={`${name}.amount`} label="Amount" required {...fieldAmountProps}  />
      </Grid>

      <Grid item xs={1}>
        <IconButton onClick={onDeleteClick}>
          <DeleteIcon />
        </IconButton>
      </Grid>
    </>
  );
};

interface Props extends BudgetCostModalContentProps {
  currentTax: Tax;
  form: string;
  namePrefix?: string;
}

const StudentFeeContent: React.FC<Props> = ({
    values,
    classValues,
    taxes,
    accounts,
    dispatch,
    currencySymbol,
    currentTax,
    form,
    namePrefix = ""
  }) => {
  const totalLabel = useMemo(() => `Total class fee (${currentTax.gst ? "inc " : "no "} GST)`, [currentTax]);

  const classTotalFeeLabel = useMemo(() => formatCurrency(decimalPlus(values.perUnitAmountIncTax, getPaymentPlansTotal(values.paymentPlan)), currencySymbol), [
    values.perUnitAmountIncTax,
    values.paymentPlan,
    currencySymbol
  ]);

  const onPerUnitChange = (e, v) => {
    const taxMul = decimalPlus(getCurrentTax(taxes, values.taxId).rate, 1);

    dispatch(change(form, namePrefix + "perUnitAmountExTax", decimalDivide(decimalPlus(v, getPaymentPlansTotal(values.paymentPlan)), taxMul)));
  };

  const updateFormFeeByTax = newTaxId => {
    const taxMul = decimalPlus(getCurrentTax(taxes, newTaxId).rate, 1);

    const paymentPlansTotal = getPaymentPlansTotal(values.paymentPlan);

    dispatch(change(form, namePrefix + "perUnitAmountIncTax", decimalMinus(decimalMul(decimalPlus(values.perUnitAmountExTax, paymentPlansTotal), taxMul), paymentPlansTotal)));
  };

  const addPaymentPlan = () => {
    dispatch(
      arrayInsert(form, namePrefix + "paymentPlan", 0, {
        dayOffset: 0,
        amount: 0
      } as CourseClassPaymentPlan)
    );
  };

  const onAccountIdChange = id => {
    const selectedAccountTaxId = Number(accounts.find(a => a.id === id)["tax.id"]);

    if (values.taxId !== selectedAccountTaxId) {
      dispatch(change(form, namePrefix + "taxId", selectedAccountTaxId));
      updateFormFeeByTax(selectedAccountTaxId);
    }
  };

  const removePaymentPlan = (index: number) => {
    dispatch(arrayRemove(form, namePrefix + "paymentPlan", index));
  };

  const onPaymentPlanBlur = () => {
    setTimeout(() => {
      const updated = [...values.paymentPlan];

      updated.sort((a, b) => (a.dayOffset > b.dayOffset ? 1 : -1));

      dispatch(change(form, namePrefix + "paymentPlan", updated));
    }, 1000);
  };

  return (
    <Grid container columnSpacing={3} rowSpacing={2}>
      <Grid item xs={8}>
        <FormField type="text" name="description" label="Invoice line title"  />
      </Grid>
      <Grid item xs={4}>
        <FormField
          type="select"
          name="taxId"
          label="Tax type"
          selectValueMark="id"
          selectLabelMark="code"
          onChange={updateFormFeeByTax}
          debounced={false}
          items={taxes}
          required
        />
      </Grid>
      <Grid item xs={8}>
        <FormField
          type="select"
          name="accountId"
          selectValueMark="id"
          label="Account"
          selectLabelCondition={accountLabelCondition}
          onChange={onAccountIdChange}
          debounced={false}
          items={accounts}
        />
      </Grid>
      <Grid item xs={4}>
        <FormField
          type="money"
          name="perUnitAmountIncTax"
          label="On enrolment"
          onBlur={onPerUnitChange}
          required
        />
      </Grid>

      <Grid item xs={12} className="centeredFlex">
        <div className="heading">Payment plans</div>
        <AddButton onClick={addPaymentPlan} />
      </Grid>
      <Grid container columnSpacing={3} item xs={12}>
        {values.paymentPlan.map((item, index) => <StudentFeePaymentPlan
          key={index}
          index={index}
          item={item}
          onDelete={removePaymentPlan}
          onBlur={onPaymentPlanBlur}
          classStart={classValues.startDateTime}
        />)}
      </Grid>

      <Grid container columnSpacing={3} item xs={12} className="pt-2">
        <Grid item xs={4} />
        <Grid item xs={4} className="centeredFlex pt-1 summaryTopBorder" justifyContent="flex-end">
          <Typography variant="subtitle2">{totalLabel}</Typography>
        </Grid>
        <Grid item xs={4} className="centeredFlex pt-1 summaryTopBorder money">
          <Typography variant="body2" color="textSecondary">
            {classTotalFeeLabel}
          </Typography>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default StudentFeeContent;