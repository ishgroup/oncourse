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
import WarningMessage from "../../../../../common/components/form/fieldMessage/WarningMessage";
import FormField from "../../../../../common/components/form/formFields/FormField";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { accountLabelCondition } from "../../../accounts/utils";

const FORM: string = "TRANSFER_ENROLMENT_MODAL_FORM";
const CANCEL_FEE_AMOUNT_WARNING_MESSAGE = "The cancellation fee is greater than the fee paid";

const roundCancellationFeeExTax = val => val && new Decimal(val || 0).toDecimalPlaces(2).toNumber();

const addInvoiceLineTax = (cancellationFeeExTax: number, taxRate: number) => new Decimal(cancellationFeeExTax || 0)
  .mul(taxRate)
  .toDecimalPlaces(2)
  .plus(new Decimal(cancellationFeeExTax || 0))
  .toNumber();

const TransferEnrolmentInvoiceLines: React.FC<any> = ({
    fields, classes, dispatch, incomeAccounts, taxes
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

  return (
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
              className="checkbox pb-2 pt-2"
              control={(
                <FormField
                  type="checkbox"
                  name={`${item}.isReverseCreditNotes`}
                  color="secondary"
                  onChange={v => onCreateCreditNoteChange(v, index)}
                />
              )}
              label={`Create credit note to reverse the enrolment fee of $${field
              && field.finalPriceToPayIncTax} to ${field && field.contactName}`}
            />
            <Grid className="centeredFlex">
              <FormControlLabel
                className="checkbox pb-2 pt-2"
                control={<FormField type="checkbox" name={`${item}.isChargeFee`} color="secondary" fullWidth />}
                label={`Charge ${field && field.contactName} an administrative fee of `}
                disabled={!field.isReverseCreditNotes}
              />
              <FormField
                type="number"
                name={`${item}.cancellationFeeExTax`}
                normalize={roundCancellationFeeExTax}
                onChange={e => onCancelFeeChange(e, index)}
                disabled={!field.isReverseCreditNotes}
                formatting="inline"
                hideArrowshideArrows
                step="1"
              />
              <FormField
                type="select"
                name={`${item}.taxId`}
                items={taxes || []}
                selectValueMark="id"
                selectLabelMark="code"
                onChange={v => onTaxChange(v, index)}
                disabled={!field.isReverseCreditNotes}
                formatting="inline"
              />
              {"  "}
              <Uneditable
                value={field && field.chargedFee}
                label=""
                money
                className={`pb-0 ${classes.uneditableTextField}`}
              />
              <FormField
                type="select"
                name={`${item}.incomeAccountId`}
                items={incomeAccounts || []}
                formatting="inline"
                selectValueMark="id"
                selectLabelCondition={accountLabelCondition}
                disabled={!field.isReverseCreditNotes}
              />
            </Grid>
            <Grid className="centeredFlex">
              {getCancelFeeAmountWarning(index) && (
                <WarningMessage warning={getCancelFeeAmountWarning(index)} className="pb-0 pt-1" />
              )}
            </Grid>
          </div>
        );
      })
    }
    </div>
  );
};

export default TransferEnrolmentInvoiceLines;
