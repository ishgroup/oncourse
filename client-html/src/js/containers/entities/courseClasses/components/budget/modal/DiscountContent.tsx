/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount, Tax } from '@api/model';
import Lock from '@mui/icons-material/Lock';
import LockOpen from '@mui/icons-material/LockOpen';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import { addDays, format } from 'date-fns';
import Decimal from 'decimal.js-light';
import {
  D_MMM_YYYY,
  decimalMul,
  formatCurrency,
  formatFieldPercent,
  formatPercent,
  normalizeNumberToZero,
  parseFieldPercent,
  preventNegativeOrLogEnter
} from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback, useMemo, useState } from 'react';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { IAction } from '../../../../../../common/actions/IshAction';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../../../common/components/form/formFields/Uneditable';
import { BudgetCostModalContentProps } from '../../../../../../model/entities/CourseClass';
import { getDiscountAmountExTax, getRoundingByType } from '../../../../discounts/utils';
import { COURSE_CLASS_COST_DIALOG_FORM } from '../../../constants';

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
        calculated = formatPercent(discount.discountPercent);
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
  dispatch: Dispatch<IAction>,
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
};

const DiscountContent: React.FC<Props> = ({
 values, classValues, classFee, currentTax, dispatch, currencySymbol
}) => {
  const [forecastLocked, setForecastLocked] = useState(values.courseClassDiscount.forecast === null);
  const [valueLocked, setValueLocked] = useState(values.courseClassDiscount.discountOverride === null);

  const onValueLockHandler = () => {
    setValueLocked(pr => {
      onBeforeLockSet(
        pr,
        dispatch,
        values.perUnitAmountExTax,
        currentTax,
        values.courseClassDiscount.discount,
        classFee
      );
      return !pr;
    });
  };

  const onValueLockClick = useMemo(
    () => debounce(onValueLockHandler, 300),
    []
  );

  const onForecastLockClickHandler = () => {
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
  };

  const onForecastLockClick = useMemo(
    () => debounce(onForecastLockClickHandler, 300),
    []
  );

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

  const taxOnDiscount = useMemo(() => decimalMul(values.courseClassDiscount.discountOverride || values.perUnitAmountExTax || 0, currentTax.rate), [classFee, currentTax, values.courseClassDiscount.discountOverride]);

  const discountTotalFee = useMemo(() => {
    const decimal = new Decimal(classFee).minus(values.perUnitAmountExTax || 0).minus(taxOnDiscount);

    return getRoundingByType(values.courseClassDiscount.discount.rounding, decimal);
  }, [classFee, taxOnDiscount, values.courseClassDiscount.discount.rounding]);

  return (
    <Grid container columnSpacing={3} rowSpacing={2}>
      <Grid item xs={4} className="pr-1">
        <Uneditable
          value={
            values.courseClassDiscount.discount.name
            + (values.courseClassDiscount.discount.code ? ` (${values.courseClassDiscount.discount.code})` : "")
          }
          label={$t('discount')}
          url={`/discount/${values.courseClassDiscount.discount.id}`}
        />
      </Grid>
      <Grid item xs={4}>
        <Uneditable value={validFrom} label={$t('valid_from')} />
      </Grid>
      <Grid item xs={4}>
        <Uneditable value={validTo} label={$t('valid_to')} />
      </Grid>
      <Grid item xs={4}>
        <FormField
          type="number"
          name={
            forecastLocked ? "courseClassDiscount.discount.predictedStudentsPercentage" : "courseClassDiscount.forecast"
          }
          min="0"
          max="100"
          format={formatFieldPercent}
          parse={parseFieldPercent}
          onKeyPress={preventNegativeOrLogEnter}
          label={$t('default_forecast_takeup')}
          labelAdornment={<IconButton className="inputAdornmentButton" onClick={onForecastLockClick}>
            {forecastLocked ? <Lock className="inputAdornmentIcon" /> : <LockOpen className="inputAdornmentIcon" />}
          </IconButton>}
          disabled={forecastLocked}
          debounced={false}
        />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={values.courseClassDiscount.discount.description} label={$t('description')} />
      </Grid>

      <Grid item xs={12}>
        <div className="heading pb-1 pt-2">{$t('value')}</div>
      </Grid>

      <Grid item xs={12} container>
        <Grid item container xs={8} columnSpacing={3} rowSpacing={2}>
          <Grid item xs={6}>
            <FormField
              type="money"
              name="perUnitAmountExTax"
              normalize={normalizeNumberToZero}
              label={valueLabel}
              labelAdornment={(
                <IconButton className="inputAdornmentButton" onClick={onValueLockClick}>
                  {valueLocked ? <Lock className="inputAdornmentIcon" /> : <LockOpen className="inputAdornmentIcon" />}
                </IconButton>
              )}
              disabled={valueLocked}
              onChange={onValueChange}
              debounced={false}
            />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={classFee} label={$t('class_fee')} money />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={taxOnDiscount} label={$t('tax_on_discount')} money />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={values.courseClassDiscount.discount.rounding} label={$t('rounding')} />
          </Grid>
          <Grid item xs={6}>
            <Uneditable value={discountTotalFee} label={$t('final_class_fee')} money />
          </Grid>
        </Grid>
        <Grid item xs={4} />
      </Grid>
    </Grid>
  );
};

export default DiscountContent;