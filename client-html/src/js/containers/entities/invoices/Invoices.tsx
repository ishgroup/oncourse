/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Invoice } from "@api/model";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import ListView from "../../../common/components/list-view/ListView";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import {
  createInvoice,
  deleteQuote,
  getDefaultInvoiceTerms,
  getInvoice,
  removeInvoice,
  updateInvoice
} from "./actions";
import { FilterGroup } from "../../../model/common/ListView";
import InvoicesEditView from "./components/InvoicesEditView";
import {
  clearListState,
  getFilters,
  setListCreatingNew,
  setListEditRecord,
  setListSelection,
} from "../../../common/components/list-view/actions";
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
import { getWindowHeight, getWindowWidth } from "../../../common/utils/common";
import LeadService from "../leads/services/LeadService";
import { isInvoiceType } from "./utils";
import { State } from "../../../reducers/state";
import { getListTags } from "../../tags/actions";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Payment plan",
        expression: "invoiceDate after yesterday and type == 1",
        active: false
      },
      {
        name: "Credit notes",
        expression: "amountOwing != null and amountOwing < 0 and type == 1",
        active: false
      },
      {
        name: "Unpaid invoices",
        expression: "amountOwing != null and amountOwing > 0 and type == 1",
        active: true
      },
      {
        name: "Overdue",
        expression: "overdue > 0 and amountOwing > 0 and dateDue < today and type == 1",
        active: false
      },
      {
        name: "Balanced (paid)",
        expression: "amountOwing == 0 and type == 1",
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
  leadId: null,
  leadCustomerName: null,
  contactId: null,
  contactName: null,
  publicNotes: null,
  shippingAddress: null,
  customerReference: null,
  sendEmail: true,
  tags: [],
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
  { title: "Enrolments", list: "enrolment", expression: "abstractInvoiceLines.abstractInvoice.id " },
  { title: "Classes", list: "class", expression: "enrolments.abstractInvoiceLines.abstractInvoice.id" },
  { title: "Payment In", list: "paymentIn", expression: "paymentInLines.invoice.id" },
  { title: "Payment Out", list: "paymentOut", expression: "paymentOutLines.invoice.id" },
  { title: "Transactions", list: "transaction", expression: "invoice.id" },
  { title: "Voucher redeemed", list: "sale", expression: "redeemedInvoice.id" }
];

const nameCondition = (invoice: Invoice) => {
  let result = "";
  if (invoice.type === "Invoice") {
    result = invoice.invoiceNumber ? "Invoice #" + invoice.invoiceNumber : "New";
  } else {
    result = invoice.id ? "Quote #" + invoice.quoteNumber : "New";
  }

  return result;
};

const nestedEditFields = {
  PaymentOut: props => <AddPaymentOutEditView {...props} />,
  SendMessage: props => <SendMessageEditView {...props} />
};

const manualLink = getManualLink("invoice");

const secondaryColumnCondition = row => (row.invoiceNumber ? "Invoice #" + row.invoiceNumber : "Quote #" + row.quoteNumber);

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
  setListCreatingNew,
  onDeleteQuote,
  selection,
  history,
  updateSelection,
  location,
  listRecords,
  match: { params, url },
  onInit,
  getTags,
  }) => {
  useEffect(() => {
    getFilters();
    getAccounts();
    getTaxes();
    getDefaultTerms();
    getAdministrationSites();
    getQePermissions();
    getTags();

    return clearListState;
  }, []);

  const [createMenuOpened, setCreateMenuOpened] = useState(false);

  const closeCreateMenu = () => {
    setCreateMenuOpened(false);
  };

  const openCreateMenu = () => {
    setCreateMenuOpened(true);
  };

  const updateHistory = (pathname, search) => {
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search
      });
    }
  };

  const onCreateNew = useCallback((type, lead?) => {
    closeCreateMenu();
    updateHistory(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new", location.search);

    setListCreatingNew(true);
    updateSelection(["new"]);

    if (lead) {
      Initial.leadId = lead.id;
      Initial.leadCustomerName = lead.contactName;
      Initial.contactId = lead.contactId;
      Initial.contactName = lead.contactName;
    }

    Initial.type = type;
    onInit();
  }, [params, location, url, listRecords]);

  const customOnCreate = async () => {
    if (params.id === "new" && window.location.search?.includes("lead.id")) {
      const matchedValue = window.location.search.match(/\d+$/);
      const leadId = matchedValue && matchedValue[0];

      const lead = await LeadService.getLead(+leadId);

      onCreateNew("Quote", lead);
    } else {
      openCreateMenu();
    }
  };

  const defaultDeleteDisabled = (selection.length !== 1) || (selection.length === 1 && isInvoiceType(selection[0], listRecords));

  return (
    <div>
      <ListView
        listProps={{
          primaryColumn: "contact.fullName",
          secondaryColumn: "invoiceNumber",
          secondaryColumnCondition,
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
        filterEntity="Invoice"
        onCreate={onCreate}
        onSave={onSave}
        onInit={onInit}
        customOnCreate={customOnCreate}
        onDelete={onDeleteQuote}
        defaultDeleteDisabled={defaultDeleteDisabled}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        EditViewContent={InvoicesEditView}
        CogwheelAdornment={InvoiceCogwheel}
        alwaysFullScreenCreateView
        noListTags
      />
      <Menu
        id="createMenu"
        open={createMenuOpened}
        onClose={closeCreateMenu}
        disableAutoFocusItem
        anchorReference="anchorPosition"
        anchorPosition={{ top: getWindowHeight() - 80, left: getWindowWidth() - 200 }}
        anchorOrigin={{
          vertical: "center",
          horizontal: "center"
        }}
        transformOrigin={{
          vertical: "center",
          horizontal: "center"
        }}
      >
        <MenuItem
          onClick={() => onCreateNew("Invoice")}
          classes={{
            root: "listItemPadding"
          }}
        >
          Create Invoice
        </MenuItem>
        <MenuItem
          onClick={() => onCreateNew("Quote")}
          classes={{
            root: "listItemPadding"
          }}
        >
          Create Quote
        </MenuItem>
      </Menu>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  listRecords: state.list.records,
  selection: state.list.selection,
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
  onDeleteQuote: (id: string) => dispatch(deleteQuote(id)),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  getTags: () => dispatch(getListTags("AbstractInvoice")),
  getQePermissions: () => dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" })),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Invoices);
