/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCostRepetitionType } from '@api/model';
import Lock from '@mui/icons-material/Lock';
import LockOpen from '@mui/icons-material/LockOpen';
import { Collapse, Divider, FormControlLabel, Grid, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import { Decimal } from 'decimal.js-light';
import {
  decimalMinus,
  decimalMul,
  formatCurrency,
  formatFieldPercent,
  normalizeNumberToZero,
  parseFieldPercent,
  preventNegativeOrLogEnter,
  WarningMessage
} from 'ish-ui';
import React, { useCallback, useMemo, useState } from 'react';
import { change } from 'redux-form';
import { ContactLinkAdornment } from '../../../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../../../common/components/form/formFields/Uneditable';
import { greaterThanNullValidation, validateSingleMandatoryField } from '../../../../../../common/utils/validation';
import { BudgetCostModalContentProps } from '../../../../../../model/entities/CourseClass';
import { DefinedTutorRoleExtended } from '../../../../../../model/preferences/TutorRole';
import { COURSE_CLASS_COST_DIALOG_FORM } from '../../../constants';
import { getClassCostFee } from '../utils';
import { PayRateTypes, validatePayRateTypes } from './BudgetCostModal';

interface Props extends BudgetCostModalContentProps {
  tutorRoles?: DefinedTutorRoleExtended[];
  currencySymbol?: string;
  defaultOnCostRate: number;
}

const TutorPayContent: React.FC<Props> = ({
  values,
  classValues,
  tutorRoles,
  currencySymbol,
  costLabel,
  hasMinMaxFields,
  hasCountField,
  dispatch,
  defaultOnCostRate
}) => {
  const [onCostLocked, setOnCostLocked] = useState(true);
  const onLockClick = () => {
    setOnCostLocked(prev => {
      if (!prev) {
        dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "onCostRate", prev ? defaultOnCostRate : null));
      }
      return !prev;
    });
  };

  const tutor = useMemo(
    () => classValues.tutors.find(
      t => (values.courseClassTutorId ? t.id === values.courseClassTutorId : t.temporaryId === values.temporaryTutorId)
    ),
    [values.courseClassTutorId, values.temporaryTutorId, classValues.tutors]
  );

  const role = useMemo(() => tutor && tutorRoles.find(r => r.id === tutor.roleId), [tutor, tutorRoles]);

  const rate = useMemo(() => ((role && role["currentPayrate.rate"]) ? parseFloat(role["currentPayrate.rate"]) : 0), [role]);
  const repetitionType = useMemo(() => ((role && role["currentPayrate.type"]) ? role["currentPayrate.type"] : ""), [role]);

  const budgetedCost = useMemo(
    () => getClassCostFee(
        values,
        classValues.maximumPlaces,
        classValues.budgetedPlaces,
        classValues.successAndQueuedEnrolmentsCount,
        classValues.sessions,
      ).projected,
    [
      values,
      classValues.maximumPlaces,
      classValues.budgetedPlaces,
      classValues.successAndQueuedEnrolmentsCount,
      classValues.sessions
    ]
  );

  const budgetedCostLabel = useMemo(() => formatCurrency(budgetedCost, currencySymbol), [budgetedCost, currencySymbol]);

  const budgetedIncOnCost = useMemo(
    () => decimalMul(budgetedCost, new Decimal(values.onCostRate === 0 ? 0 : values.onCostRate || defaultOnCostRate || 0).plus(1).toNumber()),
    [budgetedCost, values.onCostRate, defaultOnCostRate]
  );

  const budgetedIncOnCostLabel = useMemo(() => formatCurrency(budgetedIncOnCost, currencySymbol), [
    budgetedIncOnCost,
    currencySymbol
  ]);

  const onCostTotal = useMemo(() => decimalMinus(budgetedIncOnCost, budgetedCost), [budgetedCost, budgetedIncOnCost]);
  const onCostTotalLabel = useMemo(() => formatCurrency(onCostTotal, currencySymbol), [onCostTotal, currencySymbol]);

  const typeAndCostLabel = useMemo(
    () => `${formatCurrency(values.perUnitAmountExTax, currencySymbol)} ${values.repetitionType}`,
    [values.perUnitAmountExTax, values.repetitionType, currencySymbol]
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
        <Uneditable
          label={$t('Contact')}
          value={values.contactName}
          labelAdornment={
            <ContactLinkAdornment id={values.contactId} />
          }
        />
      </Grid>
      <Grid item xs={6} className="pb-2">
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isSunk" color="secondary"  />}
          label={$t('sunk_cost_not_recoverable_if_class_cancelled')}
        />
      </Grid>
      <Grid item xs={12} className="pb-2">
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isOverriden" disabled={isNaN(rate)} />}
          label={$t('override_assigned_role_pay_rate')}
        />
      </Grid>
      <Grid item xs={12}>
        <Collapse in={values.isOverriden}>
          <Grid container columnSpacing={3}>
            <Grid item xs={4}>
              <Uneditable label={$t('tutor_role')} value={tutor.roleName} url={`/preferences/tutorRoles/${tutor.roleId}`} />
              {isNaN(rate) && <WarningMessage warning="The chosen role has no defined rate for class period" />}
            </Grid>
            <Grid item xs={4}>
              <FormField
                type="select"
                name="repetitionType"
                label={$t('type')}
                items={PayRateTypes}
                onChange={onRepetitionChange}
                debounced={false}
                validate={validatePayRateTypes}
              />

              {typeof repetitionType === "string" && repetitionType !== values.repetitionType && (
                <WarningMessage warning="The selected type does not match the defined role's name" />
              )}
            </Grid>
            {hasCountField && (
              <Grid item xs={2}>
                <FormField
                  type="number"
                  name="unitCount"
                  label={$t('count')}
                  validate={[greaterThanNullValidation, validateSingleMandatoryField]}
                  normalize={normalizeNumberToZero}
                  debounced={false}
                />
              </Grid>
            )}
            <Grid item xs={hasCountField ? 2 : 4}>
              <FormField
                type="money"
                name="perUnitAmountExTax"
                label={costLabel}
                validate={greaterThanNullValidation}
                normalize={normalizeNumberToZero}
                debounced={false}
              />
              {!isNaN(rate) && rate !== values.perUnitAmountExTax && (
                <WarningMessage warning="The rate/amount entered differs from, and will override what is defined for the chosen role" />
              )}
            </Grid>
          </Grid>
        </Collapse>
      </Grid>
      <Grid item xs={12}>
        <Divider />
      </Grid>
      <Grid item container columnSpacing={3} xs={12} className="pt-2">
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1" className="text-nowrap money pt-1">
            {typeAndCostLabel}
          </Typography>
          {hasCountField && !values.isOverriden && (
            <Typography variant="body1" className="text-nowrap pl-3">
              <FormField
                type="number"
                name="unitCount"
                label={$t('count')}
                validate={[greaterThanNullValidation, validateSingleMandatoryField]}
                normalize={normalizeNumberToZero}
                debounced={false}
              />
            </Typography>
          )}
        </Grid>
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1" className="money pt-1">
            {budgetedCostLabel}
          </Typography>
        </Grid>
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1" className="relative">
            <FormField
              type="number"
              name="onCostRate"
              format={formatFieldPercent}
              parse={parseFieldPercent}
              onKeyPress={preventNegativeOrLogEnter}
              disabled={onCostLocked}
              defaultValue={0}
              debounced={false}
              inline
            />
            <span>{$t('oncost')}</span>
            <span className="pl-1">
              <IconButton className="inputAdornmentButton" onClick={onLockClick}>
                {!onCostLocked && <LockOpen className="inputAdornmentIcon" />}
                {onCostLocked && <Lock className="inputAdornmentIcon" />}
              </IconButton>
            </span>
          </Typography>
        </Grid>
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1" className="money pt-1">
            {onCostTotalLabel}
          </Typography>
        </Grid>
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1">{$t('total')}</Typography>
        </Grid>
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1" className="money pt-1">
            {budgetedIncOnCostLabel}
          </Typography>
        </Grid>
      </Grid>
      <Grid item xs={12} className="pt-2 pb-2">
        <Divider />
      </Grid>
      <Grid item xs={12}>
        <Collapse in={hasMinMaxFields}>
          <Grid container columnSpacing={3}>
            <Grid item xs={6}>
              <FormField type="money" name="minimumCost" label={$t('minimum_pay_for_this_class')} />
            </Grid>
            <Grid item xs={6}>
              <FormField type="money" name="maximumCost" label={$t('maximum_pay_for_this_class')} />
            </Grid>
          </Grid>
        </Collapse>
      </Grid>
    </Grid>
  );
};

export default TutorPayContent;
