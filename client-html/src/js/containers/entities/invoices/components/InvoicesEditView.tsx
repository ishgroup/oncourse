/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import Grid from "@mui/material/Grid";
import {
 arrayInsert, arrayRemove, change, initialize
} from "redux-form";
import { RouteComponentProps, withRouter } from "react-router";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { Account, Currency, Tag, Tax } from "@api/model";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import { addDays } from "date-fns";
import FormField from "../../../../common/components/form/formFields/FormField";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import { validateMinMaxDate, validateSingleMandatoryField } from "../../../../common/utils/validation";
import { State } from "../../../../reducers/state";
import { getListNestedEditRecord } from "../../../../common/components/list-view/actions";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import MinifiedEntitiesList from "../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import { getInvoiceClosestPaymentDueDate } from "../utils";
import { HeaderContent, InvoiceLines } from "./InvoiceLines";
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
import { leadLabelCondition, openLeadLink } from "../../leads/utils";
import LeadSelectItemRenderer from "../../leads/components/LeadSelectItemRenderer";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";

interface Props extends EditViewProps {
  currency: Currency;
  accounts: Account[];
  taxes: Tax[];
  values: InvoiceWithTotalLine;
  classes: any;
  defaultTerms: number;
  setSelectedContact: AnyArgFunction;
  selectedContact: any;
  tags?: Tag[];
  history?: any;
}

const sortAccounts = (a: Account, b: Account) => (a.description[0].toLowerCase() > b.description[0].toLowerCase() ? 1 : -1);

const InvoiceEditView: React.FunctionComponent<Props & RouteComponentProps> = props => {
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
    tags,
    selectedContact,
    history,
  } = props;

  useEffect(() => {
    if (values.type === "Quote" && isNew) dispatch(change(form, "sendEmail", false));
  }, []);

  useEffect(() => {
    if (values.type === "Quote" && history.location.state?.type === "Invoice") dispatch(change(form, "type", "Invoice"));
  }, [values.type]);

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
        {...props}
        currency={currency}
        isNew={isNew}
        twoColumn={twoColumn}
        dispatch={dispatch}
        form={form}
        taxes={taxes}
        incomeAndCosAccounts={incomeAndCosAccounts}
        type={values.type}
      />
      ),
    [currency, isNew, twoColumn, form]
  );

  const addInvoiceLine = useCallback(
    isNew || values.type === "Quote"
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
    (isNew || values.type === "Quote")
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

  const onLeadChange = value => {
    dispatch(change(form, "leadId", value.id));
    dispatch(change(form, "contactId", value["customer.id"]));
    dispatch(change(form, "contactName", value["customer.fullName"]));
  };

  const validateTagListCallback = useCallback(
    (value, allValues) => {
      const updProps = { ...props, rootEntity: "Invoice" };

      return (tags && tags.length ? validateTagsList(tags, value, allValues, updProps) : undefined);
    }, [tags]
  );

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

  const validateTagList = (value, allValues) => validateTagsList(tags, value, allValues, props);

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
  }, [values.paymentPlans?.length]);

  useEffect(() => {
    if (!isNew && prevId !== values.id && hasPaymentDues) {
      const closest = getInvoiceClosestPaymentDueDate(values);

      if (closest) {
        dispatch(initialize(form, { ...values, dateDue: formatToDateOnly(closest) }));
      }
    }
  }, [values.id]);

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="p-3 saveButtonTableOffset defaultBackgroundColor">
      <Grid item xs={12}>
        <FormField
          type="tags"
          name="tags"
          tags={tags}
          validate={validateTagListCallback}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="text"
          name="title"
          label="Title"
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Lead"
          name="leadId"
          label="Lead"
          selectValueMark="id"
          selectLabelCondition={leadLabelCondition}
          defaultDisplayValue={values && values.leadCustomerName}
          labelAdornment={
            <LinkAdornment linkHandler={openLeadLink} link={values.leadId} disabled={!values.leadId} />
          }
          onInnerValueChange={onLeadChange}
          itemRenderer={LeadSelectItemRenderer}
          disabled={values.type !== "Quote" && !isNew}
          rowHeight={55}
          required={values.type === "Quote"}
        />
      </Grid>

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
          disabled={values.type !== "Quote" && !isNew}
          rowHeight={55}
          required={values.type === "Invoice"}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="text" name="customerReference" label="Customer reference" />
      </Grid>

      {values.type !== "Quote" && (
        <Grid item xs={twoColumn ? 3 : 12} className="textField">
          <div>
            <Typography variant="caption" color="textSecondary">
              Overdue
            </Typography>
            <Typography className="money">{overdue}</Typography>
          </div>
        </Grid>
      )}

      {!isNew && values.type === "Invoice" && (
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
          disabled={values.type !== "Quote" && !isNew}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField
          type="date"
          name="dateDue"
          label="Due date"
          minDate={values.invoiceDate}
          validate={[validateSingleMandatoryField, validateMinDate]}
          disabled={values.type !== "Quote" && hasPaymentDues}
        />
      </Grid>

      <Grid item xs={twoColumn ? 3 : 12}>
        <FormField type="multilineText" name="billToAddress" label="Billing address"/>
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField type="multilineText" name="shippingAddress" label="Shipping address"/>
      </Grid>

      <Grid item xs={twoColumn ? 6 : 12} className="pb-2">
        <FormField
          type="multilineText"
          name="description"
          label="Description"
          disabled={values.type !== "Quote" && !isNew}
        />
      </Grid>

      <Grid item xs={12}>
        <MinifiedEntitiesList
          name="invoiceLines"
          header={values.type === "Quote" ? "Quote Lines" : "Invoice Lines"}
          oneItemHeader={values.type === "Quote" ? "Quote Line" : "Invoice Line"}
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
        {values.type !== "Quote" && (
          <div className="centeredFlex pt-1 pr-4 justify-content-end">
            <Typography variant="subtitle2" noWrap>
              Owing
            </Typography>
            <Typography variant="body2" color="textSecondary" className="pl-1 money">
              {totalOwing}
            </Typography>
          </div>
        )}
      </Grid>

      {values.type === "Invoice" && (
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
      )}

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

      {values.type === "Invoice" && (
        <Grid item xs={12} className="pb-2">
          <FormControlLabel
            classes={{
              root: "checkbox"
            }}
            control={<FormField type="checkbox" name="sendEmail" color="primary" />}
            label="Send email"
          />
        </Grid>
      )}

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
  tags: state.tags.entityTags["AbstractInvoice"],
  accounts: state.plainSearchRecords.Account.items,
  currency: state.currency,
  taxes: state.taxes.items,
  defaultTerms: state.invoices.defaultTerms,
  selectedContact: state.invoices.selectedContact
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  openNestedEditView: (entity: string, id: number) => dispatch(getListNestedEditRecord(entity, id)),
  setSelectedContact: (selectedContact: any) => dispatch(setSelectedContact(selectedContact))
});

const Connected = connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(InvoiceEditView));

export default pops => (pops.values ? <Connected {...pops} /> : null);
