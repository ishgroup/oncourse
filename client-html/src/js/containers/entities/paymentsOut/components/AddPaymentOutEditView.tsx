/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import { connect } from "react-redux";
import {
  change, FieldArray, initialize
} from "redux-form";
import Grid from "@material-ui/core/Grid";
import { compareAsc, format as formatDate } from "date-fns";
import { Currency, PaymentMethod } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { openInternalLink } from "../../../../common/utils/links";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { State } from "../../../../reducers/state";
import Uneditable from "../../../../common/components/form/Uneditable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { D_MMM_YYYY, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM } from "../../../../common/utils/dates/format";
import { EditViewProps } from "../../../../model/common/ListView";
import { validateSingleMandatoryField, greaterThanNullValidation } from "../../../../common/utils/validation";
import { getActivePaymentOutMethods } from "../actions";
import ChequeSummaryRenderer from "./ChequeSummaryRenderer";
import { defaultCurrencySymbol } from "../../common/bankingPaymentUtils";
import { PaymentOutModel } from "../reducers/state";
import { SiteState } from "../../sites/reducers/state";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";

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
  formInitialValues: PaymentOutModel;
  currency?: Currency;
  postPaymentOut?: () => void;
  onInit?: any;
  paymentOutMethods: PaymentMethod[];
  refundablePayments?: any;
  accountItems?: any;
  adminSites?: SiteState["adminSites"];
  lockedDate?: any;
  selection?: string[];
}

const getTotalOwing = invoices => invoices.reduce((acc, invoice) => Math.round(acc * 100 + invoice.amountOwing * 100) / 100, 0);

const getTotalOutstanding = invoices => invoices.reduce((acc, invoice) => Math.round(acc * 100 + invoice.outstanding * 100) / 100, 0);

const getInitialTotalOwing = invoices => {
  if (Array.isArray(invoices)) {
    return invoices
      .filter(invoice => invoice.amountOwing < 0)
      .reduce((acc, invoice) => Math.round(acc * 100 + invoice.amountOwing * 100) / 100, 0);
  }
  return null;
};

const getInitialTotalOutstanding = (invoices, amount) => {
  const totalOwing = getInitialTotalOwing(invoices);

  return Math.round((totalOwing || 1) * 100 + amount * 100) / 100;
};

const getAmountToAllocate = (invoices, amount) => {
  const checkedInvoices = invoices.filter(invoice => invoice.payable);
  const checkedSum = getTotalOwing(checkedInvoices);

  return Math.round(amount * 100 + checkedSum * 100) / 100;
};

const AddPaymentOutWrapper = props => {
  const {
    values,
    formInitialValues,
    dispatch,
    form
  } = props;

  const initForm = model => {
    dispatch(initialize(form, model));
  };

  useEffect(() => {
    dispatch(getActivePaymentOutMethods());

    initForm({
      ...formInitialValues,
      amount: 0,
      datePayed: formatDate(Date.now(), III_DD_MMM_YYYY),
      dateBanked: ""
    });
  }, [formInitialValues]);

  return values ? <AddPaymentOutEditView {...props} /> : null;
};

const AddPaymentOutEditView: React.FunctionComponent<AddPaymentOutEditViewProps> = props => {
  const {
    values,
    currency,
    formInitialValues,
    refundablePayments,
    dispatch,
    form,
    paymentOutMethods,
    accountItems,
    adminSites,
    lockedDate,
    selection
  } = props;

  const addPaymentOutTitle = useMemo(
    () => `pay item${values && values.invoices && values.invoices.length !== 1 ? "s" : ""}`,
    [values ? values.invoices : null]
  );

  const initialTotalOwing = useMemo(() => getInitialTotalOwing(formInitialValues.invoices), [formInitialValues.invoices]);

  const initialTotalOutstanding = useMemo(
    () => getInitialTotalOutstanding(formInitialValues.invoices, values.amount),
    [formInitialValues.invoices, values.amount]
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
      const lockedDateValue = new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth);
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
      } else {
        const amountToAllocate = getAmountToAllocate(values.invoices, values.amount);

        if (amountToAllocate > 0) {
          owing = row.outstanding + amountToAllocate;
          if (owing > 0) {
            owing = 0;
          }
        }
      }
      dispatch(change(form, `invoices[${fieldIndex}].outstanding`, owing));
    };

  useEffect(() => {
    const invoiceIndex = values.invoices?.findIndex(i => i.id === Number(selection[0]));
    if (invoiceIndex !== -1) {
      const amount = Math.abs(values.invoices[invoiceIndex].amountOwing);
      const amountToAllocate = getAmountToAllocate(values.invoices, amount);
      dispatch(change(form, "amount", amount));
      dispatch(change(form, `invoices[${invoiceIndex}].payable`, true));
      dispatch(change(form, `invoices[${invoiceIndex}].outstanding`, values.invoices[invoiceIndex].outstanding + amountToAllocate));
    }
  }, [selection]);

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
          amount,
          privateNotes
        } = payment;

        const shortCurrencySymbol = currency != null ? currency.shortCurrencySymbol : defaultCurrencySymbol;
        const formattedAmount = shortCurrencySymbol + amount;
        const formattedDate = formatDate(new Date(createdOn), III_DD_MMM_YYYY_HH_MM);

        return {
          value: refundableId,
          label: `${formattedDate} [${gatewayReference}/${creditCardClientReference}] ${formattedAmount} ${
            privateNotes || ""
          }`
        };
      });
  }, [refundablePayments, values.amount, currency]);

  const invoiceDisabledCondition = () => {
    const totalOutstanding = getInitialTotalOutstanding(formInitialValues.invoices, values.amount);
    return totalOutstanding > 0 || totalOutstanding === null || totalOutstanding < getInitialTotalOwing(formInitialValues.invoices);
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
      if (value === 2) {
        if (!refundablePayments) {
          return "There are no refundable payments. Choose another payment method.";
        }

        if (!refundablePayments.some(p => p.amount >= allValues.amount)) {
          return "There are no available credit card payments to refund this amount.";
        }
      }
      return undefined;
    },
    [refundablePaymentRecords]
  );

  const validateInvoices = useCallback(
    (value, allValues) => {
      const amountToAllocate = Math.round((allValues.amount + initialTotalOwing - getTotalOutstanding(allValues.invoices)) * 100) / 100;

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
        return { ...formInitialValues.invoices.find(i => i.id === invoice.id) };
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
      <Grid container>
        <Grid item xs={12}>
          <Uneditable value={values.payeeName} label="Payment to" url={`/contact/${values.payeeId}`} />
        </Grid>

        <Grid item xs={4}>
          <FormField
            type="select"
            name="paymentMethodId"
            label="Type"
            items={paymentTypes}
            validate={[validateSingleMandatoryField, validatePaymentMethodField]}
            onChange={handlePaymentMethodChange}
          />
        </Grid>

        <Grid item xs={4}>
          <FormField
            type="searchSelect"
            name="administrationCenterId"
            label="Site"
            defaultDisplayValue={values.administrationCenterName}
            selectLabelCondition={getAdminCenterLabel}
            validate={
              typeof values.paymentMethodId === "number" && values.paymentMethodId !== 2
                ? validateSingleMandatoryField
                : undefined
            }
            items={adminSites || []}
            labelAdornment={<LinkAdornment link={values.administrationCenterId} linkHandler={openSiteLink} />}
          />
        </Grid>

        <Grid item xs={4} />

        {values.paymentMethodId === 2 && Boolean(refundablePaymentRecords && refundablePaymentRecords.length) && (
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

        {values.paymentMethodId === 1 && <ChequeSummaryRenderer />}

        <Grid item xs={4}>
          <FormField
            type="money"
            value={values.amount}
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
          <FormField type="multilineText" name="privateNotes" label="Private notes" fullWidth />
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
      />
    </div>

  ) : null;
};

const mapStateToProps = (state: State) => ({
  formInitialValues: state.paymentsOut.addPaymentOutValues,
  paymentOutMethods: state.paymentsOut.paymentOutMethods,
  refundablePayments: state.paymentsOut.refundablePayments,
  currency: state.currency,
  accountItems: state.accounts.items,
  adminSites: state.sites.adminSites,
  lockedDate: state.lockedDate,
  selection: state.list.selection
});

export default connect<any, any, any>(mapStateToProps, null)(AddPaymentOutWrapper);
