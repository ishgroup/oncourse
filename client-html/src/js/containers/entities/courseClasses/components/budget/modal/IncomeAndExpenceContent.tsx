/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCostRepetitionType } from '@api/model';
import { Divider, FormControlLabel, Grid } from '@mui/material';
import Collapse from '@mui/material/Collapse';
import $t from '@t';
import { normalizeNumberToZero } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { change } from 'redux-form';
import { ContactLinkAdornment } from '../../../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { getCurrentTax, getFeeExTaxByFeeIncTax, getTotalByFeeExTax } from '../../../../../../common/utils/hooks';
import { greaterThanNullValidation } from '../../../../../../common/utils/validation';
import { BudgetCostModalContentProps } from '../../../../../../model/entities/CourseClass';
import ContactSelectItemRenderer from '../../../../contacts/components/ContactSelectItemRenderer';
import { getContactFullName } from '../../../../contacts/utils';
import { COURSE_CLASS_COST_DIALOG_FORM } from '../../../constants';
import { PayRateTypes, validatePayRateTypes } from './BudgetCostModal';

const IncomeAndExpenceContent: React.FC<BudgetCostModalContentProps> = ({
  taxes,
  values,
  dispatch,
  costLabel,
  hasMinMaxFields,
  hasCountField
}) => {
  const isIncome = useMemo(() => values.flowType === "Income", [values.flowType]);

  const currentTax = useMemo(() => getCurrentTax(taxes, values.taxId), [values.taxId, taxes]);

  const onFeeIncTaxChange = useCallback(
    value => {
      if (currentTax) {
        dispatch(
          change(
            COURSE_CLASS_COST_DIALOG_FORM,
            "perUnitAmountExTax",
            getFeeExTaxByFeeIncTax( currentTax?.rate, value)
          )
        );
      }
    },
    [currentTax]
  );

  const onFeeExTaxChange = useCallback(
    value => {
      if (currentTax) {
        dispatch(
          change(
            COURSE_CLASS_COST_DIALOG_FORM,
            "perUnitAmountIncTax",
            getTotalByFeeExTax(currentTax?.rate, value)
          )
        );
      }
    },
    [currentTax]
  );

  const onTaxIdChange = useCallback(
    id => {
      dispatch(
        change(COURSE_CLASS_COST_DIALOG_FORM, "perUnitAmountIncTax", getTotalByFeeExTax(getCurrentTax(taxes, id)?.rate, values.perUnitAmountExTax))
      );
    },
    [values.perUnitAmountExTax, taxes]
  );

  const onRepetitionChange = useCallback<any>((val: ClassCostRepetitionType, v, prev: ClassCostRepetitionType) => {
    if (val === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", 1));
    }
    if (prev === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", null));
    }
  }, []);

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name="description"
          label={$t('description')}
          required
        />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="remoteDataSelect"
          entity="Contact"
          name="contactId"
          label={$t('Contact')}
          selectValueMark="id"
          selectLabelCondition={getContactFullName}
          defaultValue={values.contactName}
          labelAdornment={
            <ContactLinkAdornment id={values?.contactId} />
          }
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          allowEmpty
        />
      </Grid>
      <Grid container columnSpacing={3} item xs={12}>
        <Grid item xs={hasCountField ? 2 : 3}>
          <FormField
            type="select"
            name="repetitionType"
            label={$t('type')}
            items={PayRateTypes}
            onChange={onRepetitionChange}
            debounced={false}
            validate={validatePayRateTypes}
          />
        </Grid>
        {hasCountField && (
          <Grid item xs={2}>
            <FormField
              type="number"
              name="unitCount"
              label={$t('count')}
              validate={greaterThanNullValidation}
            />
          </Grid>
        )}
        <Grid item xs={hasCountField ? 2 : 3}>
          <FormField
            type="money"
            name="perUnitAmountExTax"
            label={isIncome ? "Amount" : costLabel}
            onChange={onFeeExTaxChange}
            debounced={false}
          />
        </Grid>
        <Grid item xs={3}>
          <FormField
            type="select"
            name="taxId"
            label={$t('tax')}
            selectValueMark="id"
            selectLabelMark="code"
            onChange={onTaxIdChange}
            debounced={false}
            items={taxes}
          />
        </Grid>
        <Grid item xs={3}>
          <FormField
            type="money"
            name="perUnitAmountIncTax"
            label={$t('amount_inc_tax')}
            normalize={normalizeNumberToZero}
            onChange={onFeeIncTaxChange}
            debounced={false}
          />
        </Grid>
      </Grid>

      {!isIncome && (
        <Grid item xs={12}>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="isSunk" color="secondary" />}
            label={$t('sunk_cost_not_recoverable_if_class_cancelled')}
          />
        </Grid>
      )}

      <Grid item xs={12}>
        <Collapse in={hasMinMaxFields}>
          <Grid container columnSpacing={3}>
            <Grid item xs={12} className="pt-2">
              <Divider />
            </Grid>
            <Grid item xs={12}>
              <div className="heading pt-2 pb-2">{$t('total_amount_for_this_class')}</div>
            </Grid>
            <Grid item xs={3}>
              <FormField type="money" name="minimumCost" label={$t('at_least')} />
            </Grid>
            <Grid item xs={3}>
              <FormField type="money" name="maximumCost" label={$t('limited_to')} />
            </Grid>
          </Grid>
        </Collapse>
      </Grid>
    </Grid>
  );
};

export default IncomeAndExpenceContent;
