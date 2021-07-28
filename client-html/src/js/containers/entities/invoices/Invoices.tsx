/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Invoice } from "@api/model";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import ListView from "../../../common/components/list-view/ListView";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import {
 createInvoice, getDefaultInvoiceTerms, getInvoice, removeInvoice, updateInvoice
} from "./actions";
import { FilterGroup } from "../../../model/common/ListView";
import InvoicesEditView from "./components/InvoicesEditView";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getPlainAccounts } from "../accounts/actions";
import { getPlainTaxes } from "../taxes/actions";
import InvoiceCogwheel from "./components/InvoiceCogwheel";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { formatToDateOnly } from "../../../common/utils/dates/datesNormalizing";
import AddPaymentOutEditView from "../paymentsOut/components/AddPaymentOutEditView";
import { getAdministrationSites } from "../sites/actions";
import { checkPermissions } from "../../../common/actions";
import { getAccountTransactionLockedDate } from "../../preferences/actions";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Payment plan",
        expression: "invoiceDate after yesterday",
        active: false
      },
      {
        name: "Credit notes",
        expression: "amountOwing != null and amountOwing < 0",
        active: false
      },
      {
        name: "Unpaid invoices",
        expression: "amountOwing != null and amountOwing > 0",
        active: true
      },
      {
        name: "Overdue",
        expression: "overdue > 0 and amountOwing > 0 and dateDue < today",
        active: false
      },
      {
        name: "Balanced (paid)",
        expression: "amountOwing == 0",
        active: false
      },
      {
        name: "Quote",
        expression: "type == 2",
        active: false
      }
    ]
  }
];

const Initial: Invoice = {
  billToAddress: null,
  createdByUser: null,
  dateDue: null,
  invoiceDate: null,
  invoiceNumber: 0,
  publicNotes: null,
  shippingAddress: null,
  customerReference: null,
  sendEmail: true,
  invoiceLines: [],
  paymentPlans: [
    {
      amount: 0,
      date: null,
      entityName: "Invoice",
      id: null,
      successful: true,
      type: "Invoice office"
    }
  ],
  overdue: 0
};

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Invoice and entityId" },
  { title: "Contacts", list: "contact", expression: "invoices.id" },
  { title: "Enrolments", list: "enrolment", expression: "invoiceLines.invoice.id" },
  { title: "Classes", list: "class", expression: "enrolments.invoiceLines.invoice.id" },
  { title: "Payment In", list: "paymentIn", expression: "paymentInLines.invoice.id" },
  { title: "Payment Out", list: "paymentOut", expression: "paymentOutLines.invoice.id" },
  { title: "Transactions", list: "transaction", expression: "invoice.id" },
  { title: "Voucher redeemed", list: "sale", expression: "redeemedInvoice.id" }
];

const nameCondition = (invoice: Invoice) => (invoice.invoiceNumber ? "#" + invoice.invoiceNumber : "New");

const nestedEditFields = {
  PaymentOut: props => <AddPaymentOutEditView {...props} />,
  SendMessage: props => <SendMessageEditView {...props} />
};

const manualLink = getManualLink("invoice");

const Invoices = React.memo<any>(({
  getFilters,
  getAccounts,
  getTaxes,
  getDefaultTerms,
  getAdministrationSites,
  getQePermissions,
  clearListState,
  onCreate,
  onSave,
  getInvoiceRecord,
  onDelete,
  onInit
  }) => {
  useEffect(() => {
    getFilters();
    getAccounts();
    getTaxes();
    getDefaultTerms();
    getAdministrationSites();
    getQePermissions();

    return clearListState;
  }, []);

  return (
    <div>
      <ListView
        listProps={{
          primaryColumn: "contact.fullName",
          secondaryColumn: "invoiceNumber"
        }}
        editViewProps={{
          manualLink,
          nameCondition,
          asyncValidate: notesAsyncValidate,
          asyncBlurFields: ["notes[].message"]
        }}
        nestedEditFields={nestedEditFields}
        getEditRecord={getInvoiceRecord}
        rootEntity="AbstractInvoice"
        onCreate={onCreate}
        onSave={onSave}
        onInit={onInit}
        onDelete={onDelete}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        EditViewContent={InvoicesEditView}
        CogwheelAdornment={InvoiceCogwheel}
        defaultDeleteDisabled
        noListTags
      />
    </div>
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    const today = formatToDateOnly(new Date());
    Initial.invoiceDate = today;
    Initial.dateDue = today;
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getAccounts: () => getPlainAccounts(dispatch),
  getTaxes: () => dispatch(getPlainTaxes()),
  getAdministrationSites: () => dispatch(getAdministrationSites()),
  getFilters: () => dispatch(getFilters("Invoice")),
  getDefaultTerms: () => {
    dispatch(getDefaultInvoiceTerms());
    dispatch(getAccountTransactionLockedDate());
  },
  clearListState: () => dispatch(clearListState()),
  getInvoiceRecord: (id: string) => dispatch(getInvoice(id)),
  onSave: (id: string, invoice: Invoice) => dispatch(updateInvoice(id, invoice)),
  onCreate: (invoice: Invoice) => dispatch(createInvoice(invoice)),
  onDelete: (id: string) => dispatch(removeInvoice(id)),
  getQePermissions: () => dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Invoices);
