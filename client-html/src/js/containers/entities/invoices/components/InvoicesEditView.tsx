/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import Grid from "@material-ui/core/Grid";
import {
  arrayInsert, arrayRemove, change, initialize
} from "redux-form";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
 Currency, Account, Tax
} from "@api/model";
import Typography from "@material-ui/core/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { addDays } from "date-fns";
import FormField from "../../../../common/components/form/form-fields/FormField";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import { validateSingleMandatoryField, validateMinMaxDate } from "../../../../common/utils/validation";
import { State } from "../../../../reducers/state";
import { getListNestedEditRecord } from "../../../../common/components/list-view/actions";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import MinifiedEntitiesList from "../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import { getInvoiceClosestPaymentDueDate } from "../utils";
import { InvoiceLines, HeaderContent } from "./InvoiceLines";
import { formatToDateOnly } from "../../../../common/utils/dates/datesNormalizing";
import { EditViewProps } from "../../../../model/common/ListView";
import InvoicePaymentPlans from "./InvoicePaymentPlans";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { InvoiceLineWithTotal, InvoiceWithTotalLine } from "../../../../model/entities/Invoice";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { setSelectedContact } from "../actions";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { usePrevious } from "../../../../common/utils/hooks";

interface Props extends EditViewProps {
  currency: Currency;
  accounts: Account[];
  taxes: Tax[];
  values: InvoiceWithTotalLine;
  classes: any;
  defaultTerms: number;
  setSelectedContact: AnyArgFunction;
  selectedContact: any;
}

const sortAccounts = (a: Account, b: Account) => (a.description[0].toLowerCase() > b.description[0].toLowerCase() ? 1 : -1);

const InvoiceEditView: React.FunctionComponent<Props> = props => {
  const {
    isNew,
    values,
    dispatch,
    twoColumn,
    form,
    currency,
    syncErrors,
    accounts,
    taxes,
    defaultTerms,
    setSelectedContact,
    selectedContact
  } = props;

  const prevId = usePrevious(values.id);

  const onInvoiceDateChange = useCallback(
    value => {
      dispatch(change(form, "paymentPlans[0].date", formatToDateOnly(value)));
    },
    [form]
  );

  const validateMaxDate = useCallback((value, allValues) => validateMinMaxDate(
      value,
      "",
      allValues.dateDue,
      "",
      "Invoice date should be before invoice due date or have the same date"
    ), []);

  const validateMinDate = useCallback((value, allValues) => validateMinMaxDate(
      value,
      allValues.invoiceDate,
      "",
      "Invoice due date should be after invoice date or have the same date"
    ), []);

  const validateInvoiceLines = useCallback(
    (value, allValues) => (allValues.invoiceLines && allValues.invoiceLines.length ? undefined : "Please enter some invoice lines"),
    []
  );

  const LineHeader = useCallback(
    props => <HeaderContent currency={currency} {...props} />,
    [currency]
  );

  const incomeAndCosAccounts = useMemo(() => {
    const income = accounts.filter(a => a.type === "income");
    income.sort(sortAccounts);
    const cos = accounts.filter(a => a.type === "COS");
    cos.sort(sortAccounts);

    return [income, cos];
  }, [accounts.length]);

  const InvoiceLineComponent = useCallback(
    props => (
      <InvoiceLines
        currency={currency}
        isNew={isNew}
        twoColumn={twoColumn}
        dispatch={dispatch}
        form={form}
        taxes={taxes}
        incomeAndCosAccounts={incomeAndCosAccounts}
        {...props}
      />
      ),
    [currency, isNew, twoColumn, form]
  );

  const addInvoiceLine = useCallback(
    isNew
      ? () => {
          const newLine: InvoiceLineWithTotal = {
            quantity: 1,
            incomeAccountId: incomeAndCosAccounts[0].length > 0 ? incomeAndCosAccounts[0][0].id : null,
            taxId:
              selectedContact && selectedContact["taxOverride.id"]
                ? Number(selectedContact["taxOverride.id"])
                : taxes.length ? taxes[0].id : null,
            taxEach: 0,
            discountEachExTax: 0,
            priceEachExTax: 0,
            total: 0
          };

          dispatch(arrayInsert(form, "invoiceLines", 0, newLine));
        }
      : undefined,
    [form, isNew, taxes, incomeAndCosAccounts, selectedContact]
  );

  const deleteInvoiceLine = useCallback(
    isNew
      ? index => {
          dispatch(arrayRemove(form, "invoiceLines", index));

          const total = values.invoiceLines.reduce((pr, cur, ind) => decimalPlus(pr, ind === index ? 0 : cur.total), 0);

          dispatch(change(form, "total", total));
          dispatch(change(form, "paymentPlans[0].amount", total));
        }
      : undefined,
    [values.invoiceLines, form, isNew]
  );

  const total = useMemo(() => formatCurrency(values.total, currency.shortCurrencySymbol), [
    values.total,
    currency,
    form
  ]);

  const totalOwing = useMemo(() => formatCurrency(values.amountOwing, currency.shortCurrencySymbol), [
    values.amountOwing,
    currency
  ]);

  const overdue = useMemo(() => formatCurrency(values.overdue, currency.shortCurrencySymbol), [
    values.overdue,
    currency
  ]);

  const hasPaymentDues = useMemo(() => values.paymentPlans && values.paymentPlans.some(p => p.type === "Payment due"), [
    values.paymentPlans
  ]);

  const invoiceLinesCount = useMemo(() => (values.invoiceLines && values.invoiceLines.length) || 0, [
    values.invoiceLines
  ]);

  const onContactChange = useCallback(
    value => {
      setSelectedContact(value);

      dispatch(change(form, "contactName", contactLabelCondition(value)));
      dispatch(
        change(
          form,
          "billToAddress",
          `${value["street"] ? value["street"] + "\n" : ""}${value["suburb"] ? value["suburb"] + " " : ""}${
            value["state"] ? value["state"] + " " : ""
          }${value["postcode"] ? value["postcode"] + " " : ""}`
        )
      );

      if (!hasPaymentDues) {
        dispatch(
          change(
            form,
            "dateDue",
            formatToDateOnly(addDays(new Date(), value["invoiceTerms"] ? Number(value["invoiceTerms"]) : defaultTerms))
          )
        );
      }

      if (value["taxOverride.id"] && invoiceLinesCount) {
        let count = invoiceLinesCount;
        while (count--) {
          dispatch(change(form, `invoiceLines[${count}].taxId`, Number(value["taxOverride.id"])));
        }
      }
    },
    [form, defaultTerms, hasPaymentDues, invoiceLinesCount]
  );

  const updateDateDue = useCallback(() => {
    if (hasPaymentDues) {
      const closest = getInvoiceClosestPaymentDueDate(values);
      if (closest) {
        dispatch(change(form, "dateDue", formatToDateOnly(closest)));
      }
    }
  }, [form, values.paymentPlans, hasPaymentDues]);

  useEffect(() => {
    if (!isNew) {
      updateDateDue();
    }
  }, [values.paymentPlans.length]);

  useEffect(() => {
    if (!isNew && prevId !== values.id && hasPaymentDues) {
      const closest = getInvoiceClosestPaymentDueDate(values);

      if (closest) {
        dispatch(initialize(form, { ...values, dateDue: formatToDateOnly(closest) }));
      }
    }
  }, [values.id]);

  return (
    <Grid container className="p-3 saveButtonTableOffset defaultBackgroundColor">
      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Contact"
          name="contactId"
          label="Invoice to"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={values && defaultContactName(values.contactName)}
          labelAdornment={
            <LinkAdornment linkHandler={openContactLink} link={values.contactId} disabled={!values.contactId} />
          }
          onInnerValueChange={onContactChange}
          itemRenderer={ContactSelectItemRenderer}
          disabled={!isNew}
          rowHeight={55}
          required
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="text" name="customerReference" label="Customer reference" />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12} className="textField">
        <div>
          <Typography variant="caption" color="textSecondary">
            Overdue
          </Typography>
          <Typography className="money">{overdue}</Typography>
        </div>
      </Grid>

      {!isNew && (
        <Grid item xs={twoColumn ? 3 : 12}>
          <FormField type="text" name="invoiceNumber" label="Invoice number" disabled />
        </Grid>
      )}

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="date"
          name="invoiceDate"
          label="Invoice date"
          maxDate={values.dateDue}
          onChange={onInvoiceDateChange}
          validate={[validateSingleMandatoryField, validateMaxDate]}
          disabled={!isNew}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="date"
          name="dateDue"
          label="Due date"
          minDate={values.invoiceDate}
          validate={[validateSingleMandatoryField, validateMinDate]}
          disabled={hasPaymentDues}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="multilineText" name="billToAddress" label="Billing address" fullWidth />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="multilineText" name="shippingAddress" label="Shipping address" fullWidth />
      </Grid>

      <Grid item xs={12}>
        <MinifiedEntitiesList
          name="invoiceLines"
          header="Invoice Lines"
          oneItemHeader="Invoice Line"
          count={invoiceLinesCount}
          FieldsContent={InvoiceLineComponent}
          HeaderContent={LineHeader}
          onAdd={addInvoiceLine}
          onDelete={deleteInvoiceLine}
          syncErrors={syncErrors}
          validate={validateInvoiceLines}
          accordion
        />
      </Grid>
      <Grid item xs={12}>
        <div className="centeredFlex pt-1 pr-4 justify-content-end">
          <Typography variant="subtitle2" noWrap>
            Total
          </Typography>
          <Typography variant="body2" color="textSecondary" className="pl-1 money">
            {total}
          </Typography>
        </div>
        <div className="centeredFlex pt-1 pr-4 justify-content-end">
          <Typography variant="subtitle2" noWrap>
            Owing
          </Typography>
          <Typography variant="body2" color="textSecondary" className="pl-1 money">
            {totalOwing}
          </Typography>
        </div>
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12} className="pb-2">
        <InvoicePaymentPlans
          name="paymentPlans"
          currency={currency}
          syncErrors={syncErrors}
          id={values.id}
          form={form}
          dispatch={dispatch}
          total={values.total}
        />
      </Grid>

      <Grid item xs={12}>
        <FormField type="multilineText" name="publicNotes" label="Public notes" fullWidth />
      </Grid>

      <Grid item xs={12}>
        <OwnApiNotes
          {...props}
          notesHeader="Private Note"
          leftOffset
        />
      </Grid>

      <Grid item xs={12} className="pb-2">
        <FormControlLabel
          classes={{
            root: "checkbox"
          }}
          control={<FormField type="checkbox" name="sendEmail" color="primary" />}
          label="Send email"
        />
      </Grid>

      <Grid item xs={12} className="textField money">
        <div>
          <Typography variant="caption" color="textSecondary">
            Source
          </Typography>
          <Typography>{values.source}</Typography>
        </div>
      </Grid>

      <Grid item xs={12} className="textField">
        <div>
          <Typography variant="caption" color="textSecondary">
            Created by
          </Typography>
          <Typography>{values.createdByUser}</Typography>
        </div>
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  accounts: state.accounts.items,
  currency: state.currency,
  taxes: state.taxes.items,
  defaultTerms: state.invoices.defaultTerms,
  selectedContact: state.invoices.selectedContact
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  openNestedEditView: (entity: string, id: number) => dispatch(getListNestedEditRecord(entity, id)),
  setSelectedContact: (selectedContact: any) => dispatch(setSelectedContact(selectedContact))
});

const Connected = connect<any, any, any>(mapStateToProps, mapDispatchToProps)(InvoiceEditView);

export default pops => (pops.values ? <Connected {...pops} /> : null);
