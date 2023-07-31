/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { Decimal } from "decimal.js-light";
import React, { useCallback } from "react";
import { change } from "redux-form";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { accountLabelCondition } from "../../../accounts/utils";
import { formatCurrency, WarningMessage } from "ish-ui";
import { useAppSelector } from "../../../../../common/utils/hooks";

const FORM: string = "CANCEL_ENROLMENT_FORM";
const CANCEL_FEE_AMOUNT_WARNING_MESSAGE = "The cancellation fee is greater than the fee paid";

const roundCancellationFeeExTax = val => val && new Decimal(val || 0).toDecimalPlaces(2).toNumber();

const addInvoiceLineTax = (cancellationFeeExTax: number, taxRate: number) => {
  const cancellationFee = new Decimal(cancellationFeeExTax || 0);
  return cancellationFee
    .mul(taxRate)
    .toDecimalPlaces(2)
    .plus(cancellationFee)
    .toNumber();
};

const CancelEnrolmentInvoiceLines: React.FC<any> = ({
    fields, dispatch, incomeAccounts, taxes
  }) => {
  const onCreateCreditNoteChange = useCallback((val, ind) => {
    if (!val) {
      dispatch(change(FORM, `${fields.name}[${ind}].sendInvoice`, false));
      dispatch(change(FORM, `${fields.name}[${ind}].isChargeFee`, false));
    }
  }, []);

  const onCancelFeeChange = useCallback(
    (e, ind) => {
      const field = fields.get(ind);

      const cancellationFeeIncTax = addInvoiceLineTax(
        roundCancellationFeeExTax(e.target.value),
        taxes.find(t => t.id === field.taxId).rate
      );
      dispatch(change(FORM, `invoices[${ind}].chargedFee`, cancellationFeeIncTax));
    },
    [taxes]
  );

  const onTaxChange = useCallback(
    (v, ind) => {
      const field = fields.get(ind);
      const cancellationFeeIncTax = addInvoiceLineTax(field.cancellationFeeExTax, taxes.find(t => t.id === v).rate);
      dispatch(change(FORM, `invoices[${ind}].chargedFee`, cancellationFeeIncTax));
    },
    [taxes]
  );

  const getCancelFeeAmountWarning = ind => {
    const field = fields.get(ind);
    return (field.finalPriceToPayIncTax < field.chargedFee ? CANCEL_FEE_AMOUNT_WARNING_MESSAGE : null);
  };

  const currencySymbol = useAppSelector(state => state.currency.shortCurrencySymbol);

  return (
    <>
      <div>
        {
          fields.map((item, index) => {
            const field = fields.get(index);

            return (
              <div className="pt-2" key={field.id}>
                <Typography variant="body2" className="normalHeading">
                  Invoice
                  {' '}
                  {field && field.invoiceNumber}
                </Typography>
                <FormControlLabel
                  control={(
                    <FormField
                      type="checkbox"
                      name={`${item}.isReverseCreditNotes`}
                      color="secondary"
                      onChange={v => onCreateCreditNoteChange(v, index)}
                      debounced={false}
                    />
                  )}
                  label={
                    <>
                      Create credit note to reverse the enrolment fee of <span className="money">
                        {formatCurrency(field.finalPriceToPayIncTax, currencySymbol)}
                      </span> to {field && field.contactName}
                    </>
                  }
                />
                <Grid>
                  <FormControlLabel
                    classes={{
                      label: "mt-0-5"
                    }}
                    control={<FormField type="checkbox" name={`${item}.isChargeFee`} color="secondary" />}
                    label={
                      <>
                        Charge {field && field.contactName} an administrative fee of
                        {" "}
                        <FormField
                          type="number"
                          name={`${item}.cancellationFeeExTax`}
                          normalize={roundCancellationFeeExTax}
                          onChange={e => onCancelFeeChange(e, index)}
                          debounced={false}
                          disabled={!field.isReverseCreditNotes}
                          inline
                          step="1"
                        />
                        {" "}
                        <FormField
                          type="select"
                          name={`${item}.taxId`}
                          items={taxes || []}
                          selectValueMark="id"
                          selectLabelMark="code"
                          onChange={v => onTaxChange(v, index)}
                          debounced={false}
                          disabled={!field.isReverseCreditNotes}
                          inline
                        />
                        {" "}
                        <span className="money">
                          {formatCurrency(field.chargedFee, currencySymbol)}
                        </span>
                        {" "}
                        <FormField
                          type="select"
                          name={`${item}.incomeAccountId`}
                          items={incomeAccounts || []}
                          selectValueMark="id"
                          selectLabelCondition={accountLabelCondition}
                          disabled={!field.isReverseCreditNotes}
                          inline
                        />
                      </>
                    }
                    disabled={!field.isReverseCreditNotes}
                  />
                </Grid>
                <Grid className="centeredFlex">
                  {getCancelFeeAmountWarning(index) && (
                    <WarningMessage warning={getCancelFeeAmountWarning(index)} className="pb-0 pt-1" />
                  )}
                </Grid>

                <FormControlLabel
                  control={(
                    <FormField
                      type="checkbox"
                      name={`${item}.sendInvoice`}
                      color="secondary"
                      disabled={!field.isReverseCreditNotes}
                                          />
                  )}
                  label="Send credit note email."
                />
              </div>
            );
          })
        }
      </div>
    </>
  );
};

export default CancelEnrolmentInvoiceLines;
