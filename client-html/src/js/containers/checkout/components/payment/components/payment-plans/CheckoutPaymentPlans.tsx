/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PaymentMethod } from "@api/model";
import React from "react";
import { FieldArray, WrappedFieldArrayProps } from "redux-form";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import StepButton from "@material-ui/core/StepButton";
import Stepper from "@material-ui/core/Stepper";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { normalizeNumberToPositive } from "../../../../../../common/utils/numbers/numbersNormalizing";
import { AppTheme } from "../../../../../../model/common/Theme";
import { paymentPlanStyles } from "../../../../../entities/invoices/styles/paymentPlanStyles";

const styles = () => createStyles({
  button: {
    padding: "0 9px",
    textTransform: "capitalize",
    fontSize: 12,
    marginTop: 5
  },
  inlineItem: {
    marginLeft: "0.3em"
  }
});

interface Props {
  classes?: any;
  id?: number;
  form?: any;
  dispatch?: any;
  selected?: boolean;
  validatePayNow?: any;
  onPayNowChange?: any;
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

const CheckoutPaymentPlansBase = withStyles((theme: AppTheme) => ({
  ...paymentPlanStyles(theme),
  ...styles()
}))((props: WrappedFieldArrayProps & Props) => {
  const {
    classes,
    fields,
    selected,
    validatePayNow,
    onPayNowChange,
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
    <div className="mb-3">
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
              root: "w-100"
            }}
              className={first && selected ? "centeredFlex relative selectedItemArrow" : ""}
            >

              <FormField
                type="money"
                name={`${f}.amount`}
                formatting="custom"
                normalize={normalizeNumberToPositive}
                listSpacing={false}
                onBlur={onPayNowBlur}
                onChange={onPayNowChange}
                disabled={!first || disabledStep}
                validate={first ? validatePayNow : undefined}
              />

              {first && (
                <Typography variant="caption" color="textSecondary" align="left" component="div" className={classes.inlineItem}>
                  {canChangePaymentDate && selectedPaymentType && selectedPaymentType.type !== "Credit card" ? (
                    <FormField
                      type="date"
                      name={`${f}.payDate`}
                      formatting="inline"
                      placeHolder="Pay now"
                      onChange={onPayDateChange}
                      validate={validateLockedDate}
                      fieldClasses={{
                        placeholder: "textSecondaryColor"
                      }}
                    />
                  ) : "Pay now"}
                </Typography>
            )}
              {!first && !last && (
              <Typography variant="caption" color="textSecondary" align="left" component="div" className={classes.inlineItem}>
                <FormField
                  type="date"
                  name={`${f}.date`}
                  formatting="inline"
                  disabled={!field.dateEditable || disabledStep}
                  validate={(!field.dateEditable || disabledStep) ? undefined : validateDueDate}
                  onChange={onDueDateChange}
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
              classes={{ root: classes.step }}
            >
              {first ? (
                <StepButton onClick={onPayNowFocus} className="text-left">
                  {stepContent}
                </StepButton>
              ) : stepContent}
            </Step>
          );
        })}
      </Stepper>
    </div>
  );
});

export default props => <FieldArray name={props.name} component={CheckoutPaymentPlansBase as any} {...props} />;
