/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { InvoicePaymentPlan } from "@api/model";
import IconButton from "@material-ui/core/IconButton/IconButton";
import Step from "@material-ui/core/Step";
import StepButton from "@material-ui/core/StepButton";
import StepContent from "@material-ui/core/StepContent";
import StepLabel from "@material-ui/core/StepLabel";
import Stepper from "@material-ui/core/Stepper";
import { withStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import AddCircle from "@material-ui/icons/AddCircle";
import clsx from "clsx";
import { format } from "date-fns";
import React, { useCallback, useMemo } from "react";
import { FieldArray, WrappedFieldArrayProps } from "redux-form";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { InvoicePaymentPlanContent, InvoicePaymentPlanHeader } from "../../../entities/invoices/components/InvoicePaymentPlanComponents";
import { paymentPlanStyles } from "../../../entities/invoices/styles/paymentPlanStyles";
import { sortInvoicePaymentPlans } from "../../../entities/invoices/utils";
import { getDeepValue } from "../../../../common/utils/common";

interface PaymentPlansProps {
  classes?: any;
  fields?: any[];
  currency?: any;
  syncErrors: any;
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
    classes, syncErrors, fields, currency, form, dispatch, total, dueDate, defaultOpened
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

  const error = useMemo(
    () => {
      const value = getDeepValue(syncErrors, fields.name);

      if (value) {
        return (
          <Typography color="error" variant="body2" className="text-pre-wrap" paragraph>
            {value._error}
          </Typography>
        );
      }
      return null;
    },
    [syncErrors, fields.name]
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
            "errorColor": error
          })}
        >
          Payment plan
        </Typography>

        <IconButton onClick={addPaymentDue}>
          <AddCircle className="addButtonColor" />
        </IconButton>
      </div>

      {error}

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

const CheckoutFundingInvoicePaymentPlans: React.FC<Props> = props => {
  const { name, total, ...rest } = props;

  const validatePaymentPlans = useCallback((value: any[]) => {
    if (!value || !value.find(v => v.type === "Payment due")) {
      return undefined;
    }

    const planTotal = value.filter(v => v.type === "Payment due").reduce((pre, cur) => decimalPlus(pre, cur.amount), 0);

    return planTotal !== total
      ? `Payment plan adds up to ${formatCurrency(
        planTotal,
        props.currency.shortCurrencySymbol
      )} but the Invoice total is ${formatCurrency(
        total,
        props.currency.shortCurrencySymbol
      )}.\nThese must match before you can save this invoice`
      : undefined;
  }, [total]);

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

export default withStyles(paymentPlanStyles)(CheckoutFundingInvoicePaymentPlans) as React.FC<Props>;
