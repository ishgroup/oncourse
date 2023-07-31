/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { change, FieldArray } from "redux-form";
import Grid from "@mui/material/Grid";
import { compareAsc, format as formatDate } from "date-fns";
import { Currency, PaymentMethod } from "@api/model";
import { ContactLinkAdornment } from "../../../../common/components/form/formFields/FieldAdornments";
import FormField from "../../../../common/components/form/formFields/FormField";
import { openInternalLink } from "ish-ui";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { State } from "../../../../reducers/state";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { D_MMM_YYYY, III_DD_MMM_YYYY_HH_MM } from  "ish-ui";
import { EditViewProps } from "../../../../model/common/ListView";
import { greaterThanNullValidation, validateSingleMandatoryField } from "../../../../common/utils/validation";
import ChequeSummaryRenderer from "./ChequeSummaryRenderer";
import { defaultCurrencySymbol } from "../../common/bankingPaymentUtils";
import { PaymentOutModel } from "../reducers/state";
import { SiteState } from "../../sites/reducers/state";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { LinkAdornment } from  "ish-ui";
import { getAmountToAllocate, getInitialTotalOutstanding, getInitialTotalOwing, getTotalOutstanding } from "../utils";

const addPaymentOutColumnsBase: NestedTableColumn[] = [
  {
    name: "payable",
    title: "Payable",
    type: "checkbox",
    width: 80
  },
  {
    name: "dateDue",
    title: "Due date",
    type: "date",
    width: 180
  },
  {
    name: "invoiceNumber",
    title: "Invoice#",
    width: 80
  },
  {
    name: "amountOwing",
    title: "Owing",
    type: "currency"
  },
  {
    name: "outstanding",
    title: "Outstanding",
    type: "currency"
  }
];

const openRow = value => {
  openInternalLink(`/invoice/${value.id}`);
};

interface AddPaymentOutEditViewProps extends EditViewProps {
  classes?: any;
  values: PaymentOutModel;
  currency?: Currency;
  postPaymentOut?: () => void;
  onInit?: any;
  paymentOutMethods: PaymentMethod[];
  refundablePayments?: any;
  accountItems?: any;
  adminSites?: SiteState["adminSites"];
  lockedDate?: any;
}

const AddPaymentOutEditView: React.FunctionComponent<AddPaymentOutEditViewProps> = props => {
  const {
    values,
    currency,
    refundablePayments,
    dispatch,
    form,
    paymentOutMethods,
    accountItems,
    adminSites,
    lockedDate
  } = props;

  useEffect(() => {
    dispatch(change(form, "selectedPaymentMethod", paymentOutMethods?.find(p => p.id === values.paymentMethodId)?.type));
  }, [paymentOutMethods, values.paymentMethodId]);

  const addPaymentOutTitle = useMemo(
    () => `pay item${values && values.invoices && values.invoices.length !== 1 ? "s" : ""}`,
    [values ? values.invoices : null]
  );

  const initialTotalOwing = useMemo(() => getInitialTotalOwing(values.invoices), [values.invoices]);

  const initialTotalOutstanding = useMemo(
    () => getInitialTotalOutstanding(values.invoices, values.amount),
    [values.invoices, values.amount]
  );

  const paymentTypes = useMemo(
    () => (paymentOutMethods ? paymentOutMethods
      .filter(({ active }) => active)
      .map(({ name, id }) => (name ? { value: id, label: name } : null)) : []),
    [paymentOutMethods]
  );

  const validateLockedDate = useCallback(
    settlementDate => {
      if (!lockedDate || !settlementDate) return undefined;
      const lockedDateValue = new Date(lockedDate);
      return compareAsc(lockedDateValue, new Date(settlementDate)) === 1
        ? `You must choose date after "Transaction locked" date (${formatDate(lockedDateValue, D_MMM_YYYY)})`
        : undefined;
    },
    [lockedDate]
  );

  const onPaymentCheck = (row, checked) => {
      let owing;
      const fieldIndex = values.invoices.findIndex(i => i.id === row.id);

      if (!checked) {
        owing = row.amountOwing;

        if (!values.invoices.some(e => e.payable && e.id !== row.id)) {
          dispatch(change(form, `invoices[${fieldIndex}].outstanding`, owing));
        } else if (values.invoices.some(e => e.payable && e.id !== row.id)) {
          let amount = values.amount;

          const invoices = values.invoices.map(elem => {
            if (elem.id === row.id) {
              elem.outstanding = owing;
              return elem;
            }

            if (!elem.payable) return elem;

            if (amount > 0 && elem.outstanding < 0) {
              const outstanding = elem.outstanding;
              elem.outstanding = outstanding + amount;

              amount -= Math.abs(outstanding);

              return elem;
            }

            return elem;
          });

          dispatch(change(form, "invoices", invoices));
        }
      } else {
        const amountToAllocate = getAmountToAllocate(values.invoices, values.amount);
        if (amountToAllocate > 0) {
          owing = row.outstanding + amountToAllocate;
          if (owing > 0) {
            owing = 0;
          }
        }
        dispatch(change(form, `invoices[${fieldIndex}].outstanding`, owing));
      }
    };

  const refundablePaymentRecords = useMemo(() => {
    if (!refundablePayments || !refundablePayments.length) {
      return null;
    }
    return refundablePayments
      .filter(({ amount }) => values.amount <= amount)
      .map(payment => {
        const {
          refundableId,
          createdOn,
          gatewayReference,
          creditCardClientReference,
          amount
        } = payment;

        const shortCurrencySymbol = currency != null ? currency.shortCurrencySymbol : defaultCurrencySymbol;
        const formattedAmount = shortCurrencySymbol + amount;
        const formattedDate = formatDate(new Date(createdOn), III_DD_MMM_YYYY_HH_MM);

        return {
          value: refundableId,
          label: `${formattedDate} [${gatewayReference}/${creditCardClientReference}] ${formattedAmount}`
        };
      });
  }, [refundablePayments, values.amount, currency]);

  const invoiceDisabledCondition = () => {
    const totalOutstanding = getInitialTotalOutstanding(values.invoices, values.amount);
    return totalOutstanding > 0 || totalOutstanding === null || totalOutstanding < getInitialTotalOwing(values.invoices);
  };

  const addPaymentOutColumns = useMemo(() => {
    const columns = [...addPaymentOutColumnsBase];

    columns[0].onChangeHandler = onPaymentCheck;
    columns[0].disabledHandler = invoiceDisabledCondition;

    return columns;
  }, [onPaymentCheck, invoiceDisabledCondition]);

  const validateAmountField = useCallback(() => {
    if (initialTotalOutstanding > 0) {
      return "The amount to pay cannot be greater than the total owing.";
    }
    return undefined;
  }, [refundablePayments, initialTotalOutstanding]);

  const validatePaymentMethodField = useCallback(
    (value, allValues) => {

      if (values.selectedPaymentMethod === "Credit card") {
        if (!refundablePayments) {
          return "There are no refundable payments. Choose another payment method.";
        }

        if (!refundablePayments.some(p => p.amount >= allValues.amount)) {
          return "There are no available credit card payments to refund this amount.";
        }
      }
      return undefined;
    },
    [refundablePaymentRecords, values.selectedPaymentMethod]
  );

  const validateInvoices = useCallback(
    (value, allValues) => {
      const amountToAllocate = allValues.invoices ? (Math.round((allValues.amount + initialTotalOwing - getTotalOutstanding(allValues.invoices)) * 100) / 100) : 0;
      return amountToAllocate > 0 ? "Payment amount does not match to allocated invoices amount." : undefined;
    },
    [initialTotalOwing]
  );

  const setPayableInvoices = (e, amount) => {
    const { invoices } = values;
    let leftAmount = amount;

    const updatedInvoices = invoices.map(invoice => {
      if (!invoice.payable) {
        return invoice;
      }

      const updatedInvoice = { ...invoice };
      const owing = invoice.amountOwing;

      if (leftAmount <= 0) {
        return { ...values
            .invoices.find(i => i.id === invoice.id) };
      }

      if (owing + leftAmount >= 0) {
        updatedInvoice.outstanding = 0;
        leftAmount = owing + leftAmount;
      } else {
        updatedInvoice.outstanding = owing + leftAmount;
        leftAmount = 0;
      }

      return updatedInvoice;
    });

    dispatch(change(form, "invoices", updatedInvoices));
  };

  const getAccountById = (accountItems, id) => {
    let accountName = "";
    const account = accountItems.find(account => account.id === id);
    const accountDescription = account ? account.description : "";
    const accountCode = account ? account.accountCode : "";

    if (accountDescription && accountCode) {
      accountName = `${accountDescription} ${accountCode}`;
    }

    return accountName;
  };

  const handlePaymentMethodChange = methodId => {
    const { accountId } = paymentOutMethods.find(method => method.id === methodId);
    dispatch(change(form, "account", getAccountById(accountItems, accountId)));
  };

  return values ? (
    <div className="p-3 h-100 flex-column">
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item xs={12}>
          <Uneditable value={values.payeeName} label="Payment to" labelAdornment={
            <ContactLinkAdornment id={values.payeeId} />
          } />
        </Grid>

        <Grid item xs={4}>
          <FormField
            type="select"
            name="paymentMethodId"
            label="Type"
            items={paymentTypes}
            validate={[validateSingleMandatoryField, validatePaymentMethodField]}
            onChange={handlePaymentMethodChange}
            debounced={false}
          />
        </Grid>

        <Grid item xs={4}>
          <FormField
            type="select"
            name="administrationCenterId"
            label="Site"
            defaultValue={values.administrationCenterName}
            selectLabelCondition={getAdminCenterLabel}
            validate={
              typeof values.paymentMethodId === "number" && values.selectedPaymentMethod !== "Credit card"
                ? validateSingleMandatoryField
                : undefined
            }
            items={adminSites || []}
            labelAdornment={<LinkAdornment link={values.administrationCenterId} linkHandler={openSiteLink} />}
          />
        </Grid>

        <Grid item xs={4} />

        {values.selectedPaymentMethod === "Credit card" && Boolean(refundablePaymentRecords && refundablePaymentRecords.length) && (
          <Grid item xs={12}>
            <FormField
              type="select"
              name="refundableId"
              label="Refundable payment"
              items={refundablePaymentRecords}
              required
            />
          </Grid>
        )}

        {values.selectedPaymentMethod === "Cheque" && <ChequeSummaryRenderer />}

        <Grid item xs={4}>
          <FormField
            type="money"
            name="amount"
            label="Amount paid"
            validate={[validateSingleMandatoryField, greaterThanNullValidation, validateAmountField]}
            onBlur={setPayableInvoices}
          />
        </Grid>
        <Grid item xs={4}>
          <FormField type="text" name="account" label="Account" disabled />
        </Grid>
        <Grid item xs={4} />

        <Grid item xs={4}>
          <FormField
            type="date"
            name="datePayed"
            label="Date paid"
            validate={validateLockedDate}
          />
        </Grid>
        <Grid item xs={4}>
          <Uneditable value={initialTotalOutstanding} money label="Total outstanding" />
        </Grid>

        <Grid item xs={4} />

        <Grid item xs={4}>
          <FormField type="date" name="dateBanked" label="Date banked" disabled />
        </Grid>

        <Grid item xs={4}>
          <Uneditable value={initialTotalOwing} money label="Total owing" />
        </Grid>

        <Grid item xs={4} />

        <Grid item xs={12}>
          <FormField type="multilineText" name="privateNotes" label="Private notes"  />
        </Grid>
      </Grid>

      <FieldArray
        name="invoices"
        goToLink="/invoice"
        title={addPaymentOutTitle}
        component={NestedTable}
        columns={addPaymentOutColumns}
        onRowDoubleClick={openRow}
        rerenderOnEveryChange
        hideHeader
        validate={validateInvoices}
        sortBy={(a, b) => b.invoiceNumber - a.invoiceNumber}
        className="mt-2"
        calculateHeight
      />
    </div>

  ) : null;
};

const mapStateToProps = (state: State) => ({
  paymentOutMethods: state.paymentsOut.paymentOutMethods,
  refundablePayments: state.paymentsOut.refundablePayments,
  currency: state.currency,
  accountItems: state.plainSearchRecords.Account.items,
  adminSites: state.sites.adminSites,
  lockedDate: state.lockedDate,
  selection: state.list.selection
});

export default connect<any, any, any>(mapStateToProps)((props: AddPaymentOutEditViewProps) => props.values ? <AddPaymentOutEditView {...props} /> : null);