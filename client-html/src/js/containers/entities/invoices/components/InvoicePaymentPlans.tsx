/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { InvoicePaymentPlan } from '@api/model';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import StepContent from '@mui/material/StepContent';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import { AddButton, decimalPlus, formatCurrency, YYYY_MM_DD_MINUSED } from 'ish-ui';
import React, { useCallback, useEffect, useMemo } from 'react';
import { FieldArray, WrappedFieldArrayProps } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { paymentPlanStyles } from '../styles/paymentPlanStyles';
import { sortInvoicePaymentPlans } from '../utils';
import { InvoicePaymentPlanContent, InvoicePaymentPlanHeader } from './InvoicePaymentPlanComponents';

interface PaymentPlansProps {
  classes?: any;
  fields?: any[];
  currency?: any;
  syncErrors: any;
  id: number;
  form?: any;
  dispatch?: any;
  total: number;
}

const initialPaymentDue: InvoicePaymentPlan = {
  id: null,
  date: format(new Date(), YYYY_MM_DD_MINUSED),
  type: "Payment due",
  successful: false,
  amount: 0,
  entityName: "InvoiceDueDate"
};

const InvoicePaymentPlansBase: React.FC<WrappedFieldArrayProps<any> & PaymentPlansProps> = props => {
  const {
    classes, syncErrors, fields, currency, id, form, dispatch, total
  } = props;

  const [activeStep, setActiveStep] = React.useState(fields.length);

  useEffect(() => {
    setActiveStep(fields.length);
  }, [id]);

  const handleStep = useCallback(
    (index: number) => {
      if (activeStep === index) {
        setActiveStep(fields.length);
      } else {
        setActiveStep(index);
      }
    },
    [activeStep]
  );

  const onDelete = useCallback(
    (e, index: number) => {
      e.stopPropagation();
      fields.remove(index);
    },
    [fields]
  );

  const addPaymentDue = useCallback(() => {
    fields.push({ ...initialPaymentDue });
    setActiveStep(fields.length);
  }, [fields]);

  const error = useMemo(
    () =>
      syncErrors[fields.name] && (
        <Typography color="error" variant="body2" className="text-pre-wrap" paragraph>
          {syncErrors[fields.name]._error}
        </Typography>
      ),
    [syncErrors, fields.name]
  );

  const all = useMemo(() => {
    const response = fields.getAll();

    response && response.sort(sortInvoicePaymentPlans);

    return response;
  }, [activeStep, fields.length, id]);

  return (
    <div>
      <div className="centeredFlex">
        <Typography
          className={clsx("heading", {
            "errorColor": error
          })}
        >
          {fields.length}
          {$t('payment_plan_payments')}
        </Typography>

        <AddButton onClick={addPaymentDue} />
      </div>

      {error}

      <Stepper
        activeStep={activeStep}
        classes={{
          root: classes.root
        }}
        orientation="vertical"
      >
        {all && all.map((i, index) => {
          const field: InvoicePaymentPlan = fields.get(index);

          const isPaymentDue = field.type === "Payment due";

          const item = `${fields.name}[${index}]`;

          return (
            <Step
              key={field.id}
              completed={field.successful}
              disabled={false}
              classes={{ root: classes.step }}
            >
              <StepButton onClick={() => handleStep(index)} component="div" classes={{ root: classes.stepButton }}>
                <StepLabel
                  classes={{
                    root: "w-100"
                  }}
                  error={!field.successful && !isPaymentDue}
                >
                  <InvoicePaymentPlanHeader
                    field={field}
                    index={index}
                    currency={currency}
                    classes={classes}
                    onDelete={onDelete}
                  />
                </StepLabel>
              </StepButton>
              <StepContent>
                <InvoicePaymentPlanContent
                  field={field}
                  fields={all}
                  item={item}
                  totalAmount={total}
                  currency={currency}
                  form={form}
                  dispatch={dispatch}
                />
              </StepContent>
            </Step>
          );
        })}
      </Stepper>
    </div>
  );
};

interface WrapperProps extends PaymentPlansProps {
  name: string;
  syncErrors: any;
  id: number;
  total: number;
}

const InvoicePaymentPlansWrapper: React.FC<WrapperProps> = props => {
  const { name, id, ...rest } = props;

  const validatePaymentPlans = useCallback((value: any[], allValues) => {
    if (!value || !value.find(v => v.type === "Payment due")) {
      return undefined;
    }

    if (value.find(v => v.type === "Payment due" && v.amount <= 0)) {
      return "Payment due amount must be greater than zero";
    }

    const total = value.filter(v => v.type === "Payment due").reduce((pre, cur) => decimalPlus(pre, cur.amount), 0);

    return total !== allValues.total
      ? `Payment plan adds up to ${formatCurrency(
          total,
          props.currency.shortCurrencySymbol
        )} but the Invoice total is ${formatCurrency(
          allValues.total,
          props.currency.shortCurrencySymbol
        )}.\nThese must match before you can save this invoice`
      : undefined;
  }, []);

  return (
    <FieldArray
      name={name}
      component={InvoicePaymentPlansBase}
      validate={validatePaymentPlans}
      props={{
        ...rest,
        id
      }}
    />
  );
};

export default withStyles(InvoicePaymentPlansWrapper, paymentPlanStyles) as React.FC<WrapperProps>;