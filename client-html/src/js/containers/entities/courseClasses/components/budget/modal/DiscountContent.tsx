/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo, useState } from "react";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import { addDays, format } from "date-fns";
import IconButton from "@material-ui/core/IconButton";
import LockOpen from "@material-ui/icons/LockOpen";
import Lock from "@material-ui/icons/Lock";
import { Discount, Tax } from "@api/model";
import Decimal from "decimal.js-light";
import { Dispatch } from "redux";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { BudgetCostModalContentProps } from "../../../../../../model/entities/CourseClass";
import Uneditable from "../../../../../../common/components/form/Uneditable";
import { D_MMM_YYYY } from "../../../../../../common/utils/dates/format";
import { decimalMul } from "../../../../../../common/utils/numbers/decimalCalculation";
import {
  formatCurrency,
  normalizeNumberToZero,
  formatFieldPercent,
  parseFieldPercent,
  preventNegativeOrLogEnter
} from "../../../../../../common/utils/numbers/numbersNormalizing";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../../constants";
import { getDiscountAmountExTax, getRoundingByType } from "../../../../discounts/utils";

interface Props extends BudgetCostModalContentProps {
  classFee: number;
  currentTax: Tax;
}

const getDiscountLabel = (discount: Discount, hasOverride: boolean, perUnit: number, currencySymbol: string) => {
  let calculated;

  if (hasOverride) {
    calculated = `${formatCurrency(perUnit, currencySymbol)} override`;
  } else {
    switch (discount.discountType) {
      case "Percent":
        calculated = decimalMul(discount.discountPercent, 100) + "%";
        break;
      case "Dollar":
        calculated = formatCurrency(discount.discountValue, currencySymbol);
        break;
      case "Fee override":
        calculated = formatCurrency(perUnit, currencySymbol);
    }
  }

  return `Discount ${calculated}`;
};

const onBeforeLockSet = (
  pr,
  dispatch: Dispatch,
  perUnit: number,
  currentTax: Tax,
  discount: Discount,
  classFee: number
) => {
  dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "courseClassDiscount.discountOverride", pr ? perUnit : null));

  if (!pr) {
    dispatch(change(
        COURSE_CLASS_COST_DIALOG_FORM,
        "perUnitAmountExTax",
        getDiscountAmountExTax(discount, currentTax, classFee)
      ));
  }

  return !pr;
};

const DiscountContent: React.FC<Props> = ({
 values, classValues, classFee, currentTax, dispatch, currencySymbol
}) => {
  const [forecastLocked, setForecastLocked] = useState(values.courseClassDiscount.forecast === null);
  const [valueLocked, setValueLocked] = useState(values.courseClassDiscount.discountOverride === null);

  const onValueLockClick = useCallback(() => {
    setValueLocked(pr =>
      onBeforeLockSet(
        pr,
        dispatch,
        values.perUnitAmountExTax,
        currentTax,
        values.courseClassDiscount.discount,
        classFee
      ));
  }, [classFee, currentTax, values.courseClassDiscount.discount, values.perUnitAmountExTax]);

  const onForecastLockClick = useCallback(() => {
    setForecastLocked(pr => {
      dispatch(
        change(
          COURSE_CLASS_COST_DIALOG_FORM,
          "courseClassDiscount.forecast",
          pr ? values.courseClassDiscount.discount.predictedStudentsPercentage : null
        )
      );

      return !pr;
    });
  }, [values.courseClassDiscount.discount.predictedStudentsPercentage]);

  const onValueChange = useCallback((e, val) => {
    dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "courseClassDiscount.discountOverride", val));
  }, []);

  const validFrom = useMemo(() => {
    let dateToFormat;

    if (values.courseClassDiscount.discount.validFromOffset) {
      dateToFormat = addDays(new Date(classValues.startDateTime), values.courseClassDiscount.discount.validFromOffset);
    }
    if (values.courseClassDiscount.discount.validFrom) {
      dateToFormat = new Date(values.courseClassDiscount.discount.validFrom);
    }

    return dateToFormat ? format(dateToFormat, D_MMM_YYYY) : "Any Date";
  }, [
    values.courseClassDiscount.discount.validFrom,
    values.courseClassDiscount.discount.validFromOffset,
    classValues.startDateTime
  ]);

  const validTo = useMemo(() => {
    let dateToFormat;

    if (values.courseClassDiscount.discount.validToOffset) {
      dateToFormat = addDays(new Date(classValues.startDateTime), values.courseClassDiscount.discount.validToOffset);
    }
    if (values.courseClassDiscount.discount.validTo) {
      dateToFormat = new Date(values.courseClassDiscount.discount.validTo);
    }

    return dateToFormat ? format(dateToFormat, D_MMM_YYYY) : "Forever";
  }, [
    values.courseClassDiscount.discount.validTo,
    values.courseClassDiscount.discount.validToOffset,
    classValues.startDateTime
  ]);

  const valueLabel = useMemo(() => {
    const hasOverride = typeof values.courseClassDiscount.discountOverride === "number";

    return getDiscountLabel(
      values.courseClassDiscount.discount,
      hasOverride,
      values.perUnitAmountExTax,
      currencySymbol
    );
  }, [
    values.courseClassDiscount.discount.discountPercent,
    values.courseClassDiscount.discount.discountType,
    values.courseClassDiscount.discountOverride,
    values.perUnitAmountExTax,
    currencySymbol
  ]);

  const valueAdormnet = useMemo(
    () => (
      <span>
        <IconButton className="inputAdornmentButton" onClick={onValueLockClick}>
          {valueLocked ? <Lock className="inputAdornmentIcon" /> : <LockOpen className="inputAdornmentIcon" />}
        </IconButton>
      </span>
    ),
    [valueLocked]
  );

  const forecastAdormnet = useMemo(
    () => (
      <span>
        <IconButton className="inputAdornmentButton" onClick={onForecastLockClick}>
          {forecastLocked ? <Lock className="inputAdornmentIcon" /> : <LockOpen className="inputAdornmentIcon" />}
        </IconButton>
      </span>
    ),
    [forecastLocked]
  );

  const taxOnDiscount = useMemo(() => decimalMul(values.courseClassDiscount.discountOverride || values.perUnitAmountExTax || 0, currentTax.rate), [classFee, currentTax, values.courseClassDiscount.discountOverride]);

  const discountTotalFee = useMemo(() => {
    const decimal = new Decimal(classFee).minus(values.perUnitAmountExTax || 0).minus(taxOnDiscount);

    return getRoundingByType(values.courseClassDiscount.discount.rounding, decimal);
  }, [classFee, taxOnDiscount, values.courseClassDiscount.discount.rounding]);

  return (
    <Grid container>
      <Grid item xs={4} className="pr-1">
        <Uneditable
          value={
            values.courseClassDiscount.discount.name
            + (values.courseClassDiscount.discount.code ? ` (${values.courseClassDiscount.discount.code})` : "")
          }
          label="Discount"
          url={`/discount/${values.courseClassDiscount.discount.id}`}
        />
      </Grid>
      <Grid item xs={4}>
        <Uneditable value={validFrom} label="Valid from" />
      </Grid>
      <Grid item xs={4}>
        <Uneditable value={validTo} label="Valid to" />
      </Grid>
      <Grid item xs={4}>
        <FormField
          type="persent"
          name={
            forecastLocked ? "courseClassDiscount.discount.predictedStudentsPercentage" : "courseClassDiscount.forecast"
          }
          min="0"
          max="100"
          format={formatFieldPercent}
          parse={parseFieldPercent}
          onKeyPress={preventNegativeOrLogEnter}
          props={{
            label: "Default forecast take-up",
            fullWidth: true,
            labelAdornment: forecastAdormnet
          }}
          disabled={forecastLocked}
        />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={values.courseClassDiscount.discount.description} label="Description" />
      </Grid>

      <Grid item xs={12}>
        <div className="heading pb-3 pt-2">Value</div>
      </Grid>

      <Grid item xs={12} container>
        <Grid item container xs={8}>
          <Grid item xs={6}>
            <FormField
              type="money"
              name="perUnitAmountExTax"
              normalize={normalizeNumberToZero}
              label={valueLabel}
              labelAdornment={valueAdormnet}
              disabled={valueLocked}
              onChange={onValueChange}
            />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={classFee} label="Class fee" money />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={taxOnDiscount} label="Tax on discount" money />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={values.courseClassDiscount.discount.rounding} label="Rounding" />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={discountTotalFee} label="Final class fee" money />
          </Grid>
        </Grid>
        <Grid item xs={4} />
      </Grid>
    </Grid>
  );
};

export default DiscountContent;
