/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo
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
import { formatCurrency, normalizeNumber } from "../../../../../../common/utils/numbers/numbersNormalizing";
import { accountLabelCondition } from "../../../../accounts/utils";
import { D_MMM_YYYY } from "../../../../../../common/utils/dates/format";
import { BudgetCostModalContentProps } from "../../../../../../model/entities/CourseClass";
import { getCurrentTax } from "../../../../taxes/utils";
import { getPaymentPlansTotal } from "../utils";
import AddButton from "../../../../../../common/components/icons/AddButton";
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
      <Grid item xs={6}>
        <FormField
          type="number"
          name={`${name}.dayOffset`}
          label={`Days after start ${offsetDate ? `(${offsetDate})` : ""}`}
          normalize={normalizeNumber}
          onBlur={onBlur}
        />
      </Grid>
      <Grid item xs={4}>
        <FormField type="money" name={`${name}.amount`} label="Amount" {...fieldAmountProps} />
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

  const classTotalFeeLabel = useMemo(() => formatCurrency(values.perUnitAmountIncTax, currencySymbol), [
    values.perUnitAmountIncTax,
    currencySymbol
  ]);

  const updateFormFee = useCallback(
    (feeWithTax, taxId) => {
      const total = decimalPlus(feeWithTax, getPaymentPlansTotal(values.paymentPlan));

      const taxMul = decimalPlus(getCurrentTax(taxes, taxId).rate, 1);

      dispatch(change(form, namePrefix + "perUnitAmountExTax", decimalDivide(total, taxMul)));

      dispatch(change(form, namePrefix + "perUnitAmountIncTax", total));
    },
    [values.paymentPlan, taxes, form, namePrefix]
  );

  useEffect(() => {
    updateFormFee(values.perUnitAmountIncTax, values.taxId);
  }, [values.paymentPlan]);

  const addPaymentPlan = useCallback(() => {
    dispatch(
      arrayInsert(form, namePrefix + "paymentPlan", 0, {
        dayOffset: 0,
        amount: 0
      } as CourseClassPaymentPlan)
    );

    if (!values.paymentPlan.length) {
      dispatch(
        arrayInsert(form, namePrefix + "paymentPlan", 0, {
          dayOffset: null,
          amount: values.perUnitAmountIncTax
        } as CourseClassPaymentPlan)
      );
    }
  }, [values.paymentPlan, values.perUnitAmountIncTax, namePrefix, form]);

  const calculatefeeAmountByTax = useCallback(
    val => decimalMinus(
        decimalMul(values.perUnitAmountExTax, decimalPlus(getCurrentTax(taxes, val).rate, 1)),
        values.paymentPlan.length
          ? values.paymentPlan.reduce((p: number, c) => (c.dayOffset === null ? p : decimalPlus(p, c.amount)), 0)
          : 0
      ),

    [values.perUnitAmountExTax, values.paymentPlan, taxes]
  );

  const onAccountIdChange = useCallback(
    id => {
      const selectedAccountTaxId = Number(accounts.find(a => a.id === id)["tax.id"]);

      if (values.taxId !== selectedAccountTaxId) {
        dispatch(change(form, namePrefix + "taxId", selectedAccountTaxId));
        const feeWithTax = calculatefeeAmountByTax(selectedAccountTaxId);

        updateFormFee(feeWithTax, selectedAccountTaxId);
      }
    },
    [accounts, taxes, namePrefix, values.taxId, values.perUnitAmountExTax, values.paymentPlan, form]
  );

  const removePaymentPlan = useCallback((index: number) => {
    dispatch(arrayRemove(form, namePrefix + "paymentPlan", index));
  }, [form, namePrefix]);

  const onPaymentPlanBlur = useCallback(() => {
    setTimeout(() => {
      const updated = [...values.paymentPlan];

      updated.sort((a, b) => (a.dayOffset > b.dayOffset ? 1 : -1));

      dispatch(change(form, namePrefix + "paymentPlan", updated));
    }, 1000);
  }, [values.paymentPlan, form, namePrefix]);

  const onFeeChange = useCallback(
    val => {
      updateFormFee(val, values.taxId);

      if (values.paymentPlan.length) {
        dispatch(change(form, namePrefix + "paymentPlan", values.paymentPlan.map(p => ({
          ...p,
          amount: p.dayOffset === null ? val : p.amount
        }))));
      }
    },
    [values.taxId, values.paymentPlan, taxes, form, namePrefix]
  );

  const onTaxIdChange = useCallback(
    val => {
      const fee = calculatefeeAmountByTax(val);
      updateFormFee(fee, val);
    },
    [values.paymentPlan, values.perUnitAmountExTax, taxes]
  );

  useEffect(() => {
    if (values.paymentPlan.length === 1) {
      dispatch(change(form, namePrefix + "paymentPlan", []));
    }
  }, [values.paymentPlan, form, namePrefix]);

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={3}>
        <FormField type="text" name="description" label="Invoice line title" fullWidth />
      </Grid>
      <Grid item xs={2}>
        <FormField
          type="money"
          name="perUnitAmountIncTax"
          label="On enrolment"
          onChange={onFeeChange}
          required
        />
      </Grid>
      <Grid item xs={2}>
        <FormField
          type="select"
          name="taxId"
          label="Tax type"
          selectValueMark="id"
          selectLabelMark="code"
          onChange={onTaxIdChange}
          debounced={false}
          items={taxes || []}
          required
        />
      </Grid>
      <Grid item xs={5}>
        <FormField
          type="select"
          name="accountId"
          selectValueMark="id"
          label="Account"
          selectLabelCondition={accountLabelCondition}
          onChange={onAccountIdChange}
          debounced={false}
          items={accounts || []}
        />
      </Grid>

      <Grid item xs={12} className="centeredFlex">
        <div className="heading">Payment plans</div>
        <AddButton onClick={addPaymentPlan} />
      </Grid>
      <Grid container columnSpacing={3} item xs={6}>
        {values.paymentPlan.map((item, index) => {
          if (item.dayOffset === null) {
            return null;
          }

          return (
            <StudentFeePaymentPlan
              key={index}
              index={index}
              item={item}
              onDelete={removePaymentPlan}
              onBlur={onPaymentPlanBlur}
              classStart={classValues.startDateTime}
            />
          );
        })}
      </Grid>

      <Grid container columnSpacing={3} item xs={12} className="pt-2">
        <Grid item xs={3} className="centeredFlex pt-1 summaryTopBorder">
          <Typography variant="subtitle2">{totalLabel}</Typography>
        </Grid>
        <Grid item xs={3} className="centeredFlex pt-1 summaryTopBorder money">
          <Typography variant="body2" color="textSecondary">
            {classTotalFeeLabel}
          </Typography>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default StudentFeeContent;