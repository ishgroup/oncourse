/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FinalisePeriodInfo } from '@api/model';
import HelpOutline from '@mui/icons-material/HelpOutline';
import Launch from '@mui/icons-material/Launch';
import { FormControlLabel, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Collapse from '@mui/material/Collapse';
import DialogActions from '@mui/material/DialogActions/DialogActions';
import DialogContent from '@mui/material/DialogContent/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import { addDays, format } from 'date-fns';
import { EEE_D_MMM_YYYY, openInternalLink, StringArgFunction, StyledCheckbox, validateMinMaxDate } from 'ish-ui';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { DecoratedComponentClass, getFormValues, reduxForm } from 'redux-form';
import FormField from '../../common/components/form/formFields/FormField';
import LoadingIndicator from '../../common/components/progress/LoadingIndicator';
import { validateSingleMandatoryField } from '../../common/utils/validation';
import { State } from '../../reducers/state';
import { getFinaliseInfo, updateFinaliseDate } from './actions';

interface Props {
  handleSubmit: any;
  invalid: boolean;
  values: FinalisePeriodInfo;
  getFinaliseInfo: StringArgFunction;
  updateFinaliseDate: StringArgFunction;
}

const LinkItem = React.memo<{ label, error?, onClick }>(({ label, error, onClick }) => (label ? (
  <Typography
    variant="body1"
    color={error ? "error" : undefined}
    onClick={onClick}
    className="linkDecoration centeredFlex"
  >
    <span className="pr-0-5">{label}</span>
    <Launch className="inputAdornmentIcon" color="secondary" />
  </Typography>
) : null));

const Finalise = React.memo<Props>(({
 handleSubmit, values, invalid, updateFinaliseDate, getFinaliseInfo
}) => {
  const [finalise, setFinalise] = useState(false);

  const onSubmit = useCallback((values: FinalisePeriodInfo) => updateFinaliseDate(values.targetDate), []);

  const onClickFanalise = useCallback(() => setFinalise(prev => !prev), []);

  const validateMinDate = useCallback((value, allValues) => validateMinMaxDate(value, allValues.lastDate, "", "Finalise date could not be before period start date"), []);

  useEffect(() => {
    getFinaliseInfo(values.targetDate);
  }, [values.targetDate]);

  const onClickUnreconciledPayments = useCallback(() =>
    openInternalLink(`/banking?search=id in (${values.unreconciledPaymentsBankingIds.toString()})`),
    [values.unreconciledPaymentsBankingIds]);

  const onClickUnbankedPaymentIns = useCallback(
    () => openInternalLink(`/paymentIn?search=id in (${values.unbankedPaymentInIds.toString()})`),
    [values.unbankedPaymentInIds]
  );

  const onClickUnbankedPaymentOuts = useCallback(
    () => openInternalLink(`/paymentOut?search=id in (${values.unbankedPaymentOutIds.toString()})`),
    [values.unbankedPaymentOutIds]
  );

  const onClickDepositBankings = useCallback(
    () => openInternalLink(`/banking?search=id in (${values.depositBankingIds.toString()})`),
    [values.depositBankingIds]
  );

  const finalaseDateLabel = useMemo(
    () => `Finalise period from ${format(new Date(values.lastDate || null), EEE_D_MMM_YYYY)} to`,
    [values.lastDate]
  );

  const finalaseCheckboxLabel = useMemo(
    () =>
      `Finalise (close, lock and automatically reconcile) transactions up to ${format(
        new Date(values.targetDate || null),
        EEE_D_MMM_YYYY
      )}`,
    [values.targetDate]
  );

  const unreconciledPayments = useMemo(
    () =>
      (values.unreconciledPaymentsCount
        ? `${values.unreconciledPaymentsCount} unreconciled payment${values.unreconciledPaymentsCount > 1 ? "s" : ""}`
        : null),
    [values.unreconciledPaymentsCount]
  );

  const unbankedPaymentIns = useMemo(
    () => (values.unbankedPaymentInCount ? `${values.unbankedPaymentInCount} unbanked payment in` : null),
    [values.unbankedPaymentInCount]
  );

  const unbankedPaymentOuts = useMemo(
    () => (values.unbankedPaymentOutCount ? `${values.unbankedPaymentOutCount} unbanked payment out` : null),
    [values.unbankedPaymentOutCount]
  );

  const depositBankings = useMemo(
    () =>
      (values.depositBankingCount
        ? `${values.depositBankingCount} deposit banking${values.depositBankingCount > 1 ? "s" : ""}`
        : null),
    [values.depositBankingCount]
  );

  const finaliseCaption = useMemo(
    () => (
      <Typography variant="caption" color="textSecondary" component="div">
        {`Once finalised you will never be able to create or edit financial records dated before ${format(
          addDays(new Date(values.targetDate || null), 1),
          EEE_D_MMM_YYYY
        )}`}
      </Typography>
    ),
    [values.targetDate]
  );

  return (
    <div>
      <LoadingIndicator />
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <DialogTitle>
          <div className="centeredFlex">
            {$t('finalise_accounting_period')}
            <IconButton
              target="_blank"
              href="https://www.ish.com.au/onCourse/doc/latest/manual/#accounting-finalise"
            >
              <HelpOutline />
            </IconButton>
          </div>
        </DialogTitle>

        <DialogContent className="overflow-hidden">
          <Grid container columnSpacing={3}>
            <Grid item xs={8}>
              <FormField
                type="date"
                name="targetDate"
                label={finalaseDateLabel}
                validate={[validateSingleMandatoryField, validateMinDate]}
              />
            </Grid>
            <Grid item xs={6}>
              <LinkItem label={unreconciledPayments} onClick={onClickUnreconciledPayments} error />

              <LinkItem label={unbankedPaymentIns} onClick={onClickUnbankedPaymentIns} error />

              <LinkItem label={unbankedPaymentOuts} onClick={onClickUnbankedPaymentOuts} error />

              <LinkItem label={depositBankings} onClick={onClickDepositBankings} />
            </Grid>
            <Grid item xs={12} />
            <Grid item xs={12} />
          </Grid>

          <FormControlLabel
            classes={{
              root: "checkbox mt-2"
            }}
            control={<StyledCheckbox checked={finalise} color="primary" onChange={onClickFanalise} />}
            label={finalaseCheckboxLabel}
          />
          <Collapse in={finalise}>{finaliseCaption}</Collapse>
        </DialogContent>

        <DialogActions className="p-2 justify-content-start">
          <Button color="primary" type="submit" disabled={invalid || !finalise}>
            {$t('finalise')}
          </Button>
        </DialogActions>
      </form>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  values: getFormValues("FinaliseForm")(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getFinaliseInfo: (lockDate: string) => dispatch(getFinaliseInfo(lockDate)),
  updateFinaliseDate: (lockDate: string) => dispatch(updateFinaliseDate(lockDate))
});

export default reduxForm({
  form: "FinaliseForm",
  initialValues: {}
})(
  connect<any, any, any>(
    mapStateToProps,
    mapDispatchToProps
  )(Finalise)
) as DecoratedComponentClass<any, any>;
