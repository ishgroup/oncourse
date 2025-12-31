/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency, InvoicePaymentPlan } from '@api/model';
import DeleteIcon from '@mui/icons-material/Delete';
import OpenInNew from '@mui/icons-material/OpenInNew';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { format, isPast } from 'date-fns';
import {
  decimalMinus,
  decimalPlus,
  formatCurrency,
  III_DD_MMM_YYYY,
  openInternalLink,
  useHoverShowStyles
} from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import { reducePayments } from '../utils';

const validateAmount = val => (val <= 0 ? "Payment due amount must be greater than zero" : undefined);

const checkPaymentDue = (type: string) => type === "Payment due";

const getOverdue = (fieldDate: string, allFields: InvoicePaymentPlan[]) => {
  let prevDuesAmount = 0;

  const paidAmount = reducePayments(
    allFields.filter(p => p.entityName === "PaymentIn" && p.successful)
  );

  allFields.forEach(f => {
    if (f.entityName === "InvoiceDueDate" && new Date(f.date) <= new Date(fieldDate)) {
      prevDuesAmount = decimalPlus(f.amount, prevDuesAmount);
    }
  });

  return decimalMinus(prevDuesAmount || 0, paidAmount || 0);
};

const checkExpired = (date: string) => isPast(new Date(date));

interface PaymentPlanHeaderProps {
  field: InvoicePaymentPlan;
  index: number;
  onDelete: (e: any, index: number) => void;
  currency: Currency;
  classes: any;
}

export const InvoicePaymentPlanHeader: React.FunctionComponent<PaymentPlanHeaderProps> = React.memo(props => {
  const {
    classes, field, currency, index, onDelete
  } = props;

  const { classes: hoverShowClasses } = useHoverShowStyles();

  const handleDeleteClick = useCallback(e => onDelete(e, index), [index]);

  const handleOpenClick = useCallback(
    () =>
      openInternalLink(`/${field.entityName === "PaymentIn" ? "paymentIn" : "paymentOut"}/` + field.id),
    [field.id, field.entityName]
  );

  const isPaymentDue = useMemo(() => checkPaymentDue(field.type), [field.type]);

  const hasLink = useMemo(() => ["PaymentIn", "PaymentOut"].includes(field.entityName), [field.entityName]);

  const isExpired = useMemo(() => checkExpired(field.date), [field.date]);

  const date = useMemo(() => (field.date ? format(new Date(field.date), III_DD_MMM_YYYY) : "Not set"), [field.date]);

  const total = useMemo(() => " " + formatCurrency(field.amount, currency.shortCurrencySymbol), [
    field.amount,
    currency.shortCurrencySymbol
  ]);

  const type = useMemo(() => (field.successful || isPaymentDue ? field.type : "Failed Payment "), [
    field.successful,
    field.type,
    isPaymentDue
  ]);

  return (
    <div className={clsx("centeredFlex", hoverShowClasses.container)}>
      <div className="flex-fill">
        <div className="d-flex align-items-baseline">
          <Typography
            variant="body1"
            color={!field.successful && !isPaymentDue ? "error" : undefined}
            className={isPaymentDue && !field.successful && !isExpired ? "text-bold" : undefined}
          >
            {type}
            <Typography variant="caption" color="textSecondary" className="money">
              {total}
            </Typography>
          </Typography>
        </div>
        <Typography variant="body2" color="textSecondary" align="left">
          {date}
        </Typography>
      </div>

      {hasLink && (
        <Tooltip title={$t('open_payment')}>
          <IconButton
            classes={{
              root: classes.deleteIcon
            }}
            onClick={handleOpenClick}
          >
            <OpenInNew fontSize="inherit" color="secondary" />
          </IconButton>
        </Tooltip>
      )}

      {isPaymentDue && onDelete && (
        <Tooltip title={$t('remove_payment_due')} className={hoverShowClasses.target}>
          <IconButton
            classes={{
              root: classes.deleteIcon
            }}
            onClick={handleDeleteClick}
          >
            <DeleteIcon fontSize="inherit" color="inherit" />
          </IconButton>
        </Tooltip>
      )}
    </div>
  );
});

interface PaymentPlanContentProps {
  field: InvoicePaymentPlan;
  fields: InvoicePaymentPlan[];
  totalAmount: number;
  item: string;
  currency: Currency;
  form?: any;
  dispatch?: any;
  allowZeroValue?: boolean;
  hideOwing?: boolean;
}

export const InvoicePaymentPlanContent: React.FunctionComponent<PaymentPlanContentProps> = React.memo(props => {
  const {
   field, fields, item, currency, totalAmount, allowZeroValue, hideOwing
  } = props;
  
  const isPaymentDue = useMemo(() => field.entityName === "InvoiceDueDate", [field.type]);

  const isExpired = useMemo(() => checkExpired(field.date), [field.date]);

  const validatePaymentDueAmount = useMemo(() => (isPaymentDue && !allowZeroValue ? validateAmount : undefined), [isPaymentDue]);

  const successfulPayments = useMemo(
    () => fields.filter(f => f.entityName === "PaymentIn" && f.successful),
    [fields]
  );

  const paidAmount = useMemo(() => {
    if (field.type === "Invoice office") {
      return null;
    }

    return reducePayments(successfulPayments);
  }, [field.id, fields, successfulPayments]);

  const lineOwing = useMemo(() => {
    if (field.entityName === "Invoice") {
      return field.amount;
    }

    return decimalMinus(totalAmount || 0, paidAmount || 0);
  }, [field, paidAmount, totalAmount]);

  const lineOwerdue = useMemo(() => (isPaymentDue && !field.successful && isExpired ? getOverdue(field.date, fields) : 0), [
    isPaymentDue,
    isExpired,
    field.date,
    fields
  ]);

  const overdueLabel = useMemo(() => {
    if (!isPaymentDue || !isExpired || lineOwerdue <= 0) {
      return null;
    }

    return (
      <div>
        <div className="centeredFlex pt-1 pr-4 justify-content-end">
          <Typography variant="subtitle2" noWrap>
            {$t('overdue')}
          </Typography>
          <Typography variant="body2" color="textSecondary" className="pl-1 money">
            {formatCurrency(lineOwerdue, currency.shortCurrencySymbol)}
          </Typography>
        </div>
      </div>
    );
  }, [isPaymentDue, lineOwerdue, isExpired, field.amount, field.id, currency.shortCurrencySymbol]);

  const totalOwing = useMemo(() => (
    <div>
      <div className="centeredFlex pt-1 pr-4 justify-content-end">
        <Typography variant="subtitle2" noWrap>
          {$t('total_owing2')}
        </Typography>
        <Typography variant="body2" color="textSecondary" className="pl-1 money">
          {formatCurrency(lineOwing, currency.shortCurrencySymbol)}
        </Typography>
      </div>
    </div>
    ), [isExpired, currency.shortCurrencySymbol, lineOwing]);

  return (
    <>
      <div>
        <FormField
          type="date"
          name={`${item}.date`}
          label={$t('date')}
          disabled={!isPaymentDue}
          className="mb-2 mt-2"
        />
      </div>

      <div>
        <FormField
          type="money"
          name={`${item}.amount`}
          label={$t('amount')}
          validate={validatePaymentDueAmount}
          disabled={!isPaymentDue}
        />
      </div>

      {overdueLabel}
      {!hideOwing && totalOwing}
    </>
  );
});