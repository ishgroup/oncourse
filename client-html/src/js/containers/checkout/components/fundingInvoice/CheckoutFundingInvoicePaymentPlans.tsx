/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { InvoicePaymentPlan } from '@api/model';
import { Typography } from '@mui/material';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import StepContent from '@mui/material/StepContent';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import { AddButton, decimalPlus, formatCurrency, YYYY_MM_DD_MINUSED } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { FieldArray, WrappedFieldArrayProps } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { CheckoutFundingInvoice } from '../../../../model/checkout/fundingInvoice';
import {
  InvoicePaymentPlanContent,
  InvoicePaymentPlanHeader
} from '../../../entities/invoices/components/InvoicePaymentPlanComponents';
import { sortInvoicePaymentPlans } from '../../../entities/invoices/utils';
import { paymentPlanStyles } from '../../../entities/invoices/styles/paymentPlanStyles';

interface PaymentPlansProps {
  classes?: any;
  fields?: any[];
  currency?: any;
  form?: any;
  dispatch?: any;
  total: number;
  dueDate?: string;
  defaultOpened?: number;
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
    classes, fields, currency, form, dispatch, total, dueDate, defaultOpened, meta: { error }
  } = props;
  const [activeStep, setActiveStep] = React.useState(defaultOpened || fields.length);


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
    const plan = { ...initialPaymentDue, date: format(dueDate ? new Date(dueDate) : new Date(), YYYY_MM_DD_MINUSED) };
    fields.push(plan);
    setActiveStep(fields.length);
  }, [fields, dueDate]);

  const errorRenderer = useMemo(
    () => {
      if (error) {
        return (
          <Typography color="error" variant="body2" className="text-pre-wrap">
            {error}
          </Typography>
        );
      }
      return null;
    },
    [error]
  );

  const all = useMemo(() => {
    const response = fields.getAll();
    response.sort(sortInvoicePaymentPlans);
    return response;
  }, [activeStep, fields.length]);

  return (
    <div>
      <div className="centeredFlex">
        <Typography
          className={clsx("heading", {
            "errorColor": errorRenderer
          })}
        >
          {$t('payment_plan')}
        </Typography>

        <AddButton onClick={addPaymentDue} />
      </div>

      {errorRenderer}

      <Stepper
        activeStep={activeStep}
        classes={{
          root: classes.root
        }}
        orientation="vertical"
      >
        {all.map((i, index) => {
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
                    onDelete={index === fields.length - 1 ? null : onDelete}
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
                  allowZeroValue
                  hideOwing
                />
              </StepContent>
            </Step>
          );
        })}
      </Stepper>
    </div>
  );
};

interface Props extends PaymentPlansProps {
  name: string;
  total: number;
}

const validatePaymentPlans = (value: any[], allValues: { fundingInvoices: CheckoutFundingInvoice[] }, props) => {
  if (!value || !value.find(v => v.type === "Payment due")) {
    return undefined;
  }

  const planTotal = value.filter(v => v.type === "Payment due").reduce((pre, cur) => decimalPlus(pre, cur.amount), 0);
  const total = allValues?.fundingInvoices && allValues.fundingInvoices[0].total;

  return planTotal !== total
    ? `Payment plan adds up to ${formatCurrency(
      planTotal,
      props.currency.shortCurrencySymbol
    )} but the Invoice total is ${formatCurrency(
      total,
      props.currency.shortCurrencySymbol
    )}.\nThese must match before you can save this invoice`
    : undefined;
}

const CheckoutFundingInvoicePaymentPlans: React.FC<Props> = props => {
  const { name, total, ...rest } = props;

  return (
    <FieldArray
      name={name}
      component={InvoicePaymentPlansBase}
      validate={validatePaymentPlans}
      defaultOpened={1000}
      props={{
        ...rest,
        total
      }}
    />
  );
};

export default withStyles(CheckoutFundingInvoicePaymentPlans, paymentPlanStyles) as React.FC<Props>;
