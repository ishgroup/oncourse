/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PaymentMethod } from '@api/model';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import { AppTheme, normalizeNumberToPositive } from 'ish-ui';
import React from 'react';
import { FieldArray, WrappedFieldArrayProps } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { paymentPlanStyles } from '../../../../../entities/invoices/styles/paymentPlanStyles';

const styles = () => ({
  button: {
    padding: "0 9px",
    textTransform: "capitalize",
    fontSize: 12,
    marginTop: 5
  },
  inlineItem: {
    marginLeft: "0.3em"
  },
  stepLabel: {
    width: '100%',
    height: 48
  },
  container: {
    marginBottom: 50
  }
});

interface Props {
  classes?: any;
  id?: number;
  form?: any;
  dispatch?: any;
  selected?: boolean;
  validatePayNow?: any;
  onPayDateChange?: any;
  onPayNowFocus?: any;
  onPayNowBlur?: any;
  validateLockedDate?: any;
  onDueDateChange?: any;
  disabledStep?: boolean;
  canChangePaymentDate?: boolean;
  selectedPaymentType?: PaymentMethod;
}

const validateDueDate = (date, allValues) => {
  let error;
  if (date && (allValues?.paymentPlans[0]?.payDate ? new Date(allValues.paymentPlans[0].payDate) : new Date()) > new Date(date)) {
    error = "Due date should be after invoice date";
  }
  return error;
};

const CheckoutPaymentPlansBase = withStyles((props: WrappedFieldArrayProps & Props) => {
  const {
    classes,
    fields,
    selected,
    validatePayNow,
    onPayDateChange,
    onPayNowFocus,
    disabledStep,
    onDueDateChange,
    selectedPaymentType,
    validateLockedDate,
    canChangePaymentDate,
    onPayNowBlur
  } = props;

  return (
    <div className={classes.container}>
      <div className="centeredFlex mb-2">
        <Typography className="secondaryHeading">
          Payment plan
        </Typography>
      </div>

      <Stepper
        classes={{
          root: classes.root
        }}
        orientation="vertical"
      >
        {fields.map((f, i) => {
          const field = fields.get(i);
          const first = i === 0;
          const last = fields.length > 1 && i === (fields.length - 1) && !field.date;

          if (!first && field.amount === 0) {
            return null;
          }

          const stepContent = (
            <StepLabel
              classes={{
                labelContainer: classes.stepLabel
              }}
              className={first && selected ? "centeredFlex relative selectedItemArrow" : ""}
            >

              <FormField
                type="money"
                name={`${f}.amount`}
                format={normalizeNumberToPositive}
                normalize={normalizeNumberToPositive}
                onBlur={onPayNowBlur}
                disabled={!first || disabledStep}
                validate={first ? validatePayNow : undefined}
                debounced={false}
              />

              {first && (
                <Typography variant="caption" color="textSecondary" align="left" component="div" className={classes.inlineItem}>
                  {canChangePaymentDate && selectedPaymentType && selectedPaymentType.type !== "Credit card" ? (
                    <FormField
                      type="date"
                      name={`${f}.payDate`}
                      inline
                      placeholder="Pay now"
                      onChange={onPayDateChange}
                      validate={validateLockedDate}
                    />
                  ) : "Pay now"}
                </Typography>
            )}
              {!first && !last && (
              <Typography variant="caption" color="textSecondary" align="left" component="div" className={classes.inlineItem}>
                <FormField
                  type="date"
                  name={`${f}.date`}
                  inline
                  disabled={!field.dateEditable || disabledStep}
                  validate={(!field.dateEditable || disabledStep) ? undefined : validateDueDate}
                  onChange={(!field.dateEditable || disabledStep) ? undefined : onDueDateChange}
                />
              </Typography>
            )}
              {last && (
              <Typography variant="caption" color="textSecondary" align="left" component="div" className={classes.inlineItem}>
                Pay Later
              </Typography>
            )}
            </StepLabel>
          );

          return (
            <Step
              key={f}
              disabled={disabledStep}
              classes={{
                root: classes.step,
              }}
            >
              {first ? (
                <StepButton disableRipple onClick={onPayNowFocus} className="text-left">
                  {stepContent}
                </StepButton>
              ) : stepContent}
            </Step>
          );
        })}
      </Stepper>
    </div>
  );
}, (theme: AppTheme, p, classes) => ({
  ...paymentPlanStyles(theme, p, classes),
  ...styles()
}));

export default props => (
  <FieldArray
    name={props.name}
    component={CheckoutPaymentPlansBase as any}
    {...props}
    rerenderOnEveryChange
  />
);
