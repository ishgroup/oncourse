/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Invoice } from '@api/model';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import { formatToDateOnly } from 'ish-ui';
import React, { useCallback, useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import { checkPermissions, getUserPreferences } from '../../../common/actions';
import { notesAsyncValidate } from '../../../common/components/form/notes/utils';
import {
  getFilters,
  setListEditRecord, setListFullScreenEditView,
  setListSelection,
} from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getWindowHeight, getWindowWidth } from '../../../common/utils/common';
import { getManualLink } from '../../../common/utils/getManualLink';
import { ACCOUNT_DEFAULT_INVOICELINE_ID } from '../../../constants/Config';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getAccountTransactionLockedDate } from '../../preferences/actions';
import { getListTags } from '../../tags/actions';
import { getPlainAccounts } from '../accounts/actions';
import LeadService from '../leads/services/LeadService';
import { getAdministrationSites } from '../sites/actions';
import { getPlainTaxes } from '../taxes/actions';
import { getDefaultInvoiceTerms } from './actions';
import InvoiceCogwheel from './components/InvoiceCogwheel';
import InvoicesEditView from './components/InvoicesEditView';
import { isInvoiceType } from './utils';

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

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Invoice and entityId" },
  { title: "Contacts", list: "contact", expression: "invoices.id" },
  { title: "Enrolments", list: "enrolment", expression: "abstractInvoiceLines.abstractInvoice.id " },
  { title: "Classes", list: "class", expression: "enrolments.abstractInvoiceLines.abstractInvoice.id" },
  { title: "Payment In", list: "paymentIn", expression: "paymentInLines.invoice.id" },
  { title: "Payment Out", list: "paymentOut", expression: "paymentOutLines.invoice.id" },
  { title: "Sales", list: "sale", expression: "invoiceLine.invoice.id" },
  { title: "Transactions", list: "transaction", expression: "invoice.id" },
  { title: "Voucher redeemed", list: "sale", expression: "redeemedInvoice.id" }
];

const nameCondition = (invoice: Invoice) => {
  let result;
  if (invoice.type === "Invoice") {
    result = invoice.invoiceNumber ? "Invoice #" + invoice.invoiceNumber : "New";
  } else {
    result = invoice.id ? "Quote #" + invoice.quoteNumber : "New";
  }

  return result;
};

const manualLink = getManualLink("about-invoices");

const secondaryColumnCondition = row => (row.invoiceNumber ? "Invoice #" + row.invoiceNumber : "Quote #" + row.quoteNumber);

const Invoices = React.memo<any>(({
  setListFullScreenEditView,
  selection,
  updateSelection,
  location,
  listRecords,
  match: { params, url },
  onInit,
  onMount,
  }) => {
  useEffect(() => {
    onMount();
  }, []);

  const [createMenuOpened, setCreateMenuOpened] = useState(false);

  const closeCreateMenu = () => {
    setCreateMenuOpened(false);
  };

  const openCreateMenu = () => {
    setCreateMenuOpened(true);
  };

  const onCreateNew = useCallback((type, lead?) => {
    closeCreateMenu();
    updateSelection(["new"]);

    if (lead) {
      Initial.leadId = lead.id;
      Initial.leadCustomerName = lead.contactName;
      Initial.contactId = lead.contactId;
      Initial.contactName = lead.contactName;
    }

    Initial.type = type;
    onInit();
    setListFullScreenEditView(true);
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
          asyncChangeFields: ["notes[].message"]
        }}
        scriptsFilterColumn="type"
        rootEntity="AbstractInvoice"
        filterEntity="Invoice"
        onInit={onInit}
        customOnCreate={customOnCreate}
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
          {$t('create_invoice')}
        </MenuItem>
        <MenuItem
          onClick={() => onCreateNew("Quote")}
          classes={{
            root: "listItemPadding"
          }}
        >
          {$t('create_quote')}
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
  onMount: () => {
    dispatch(getFilters("Invoice"));
    getPlainAccounts(dispatch);
    dispatch(getPlainTaxes());
    dispatch(getDefaultInvoiceTerms());
    dispatch(getAccountTransactionLockedDate());
    dispatch(getAdministrationSites());
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(getListTags("AbstractInvoice"));
    dispatch(getUserPreferences([ACCOUNT_DEFAULT_INVOICELINE_ID]));
  },
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  setListFullScreenEditView: (fullScreenEditView: boolean) => dispatch(setListFullScreenEditView(fullScreenEditView)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Invoices);