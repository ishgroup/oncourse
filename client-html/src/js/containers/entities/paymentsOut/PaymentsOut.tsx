/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import ExitToApp from '@mui/icons-material/ExitToApp';
import Link from '@mui/material/Link';
import Popover from '@mui/material/Popover';
import zIndex from '@mui/material/styles/zIndex';
import $t from '@t';
import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { getFilters, setListFullScreenEditView, setListSelection, } from '../../../common/components/list-view/actions';
import ListView from '../../../common/components/list-view/ListView';
import { updateHistory } from '../../../common/utils/common';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup } from '../../../model/common/ListView';
import { getAccountTransactionLockedDate } from '../../preferences/actions';
import { getPlainAccounts } from '../accounts/actions';
import { getAmountOwing, setContraInvoices } from '../invoices/actions';
import { getAdministrationSites } from '../sites/actions';
import { getActivePaymentOutMethods, getAddPaymentOutContact } from './actions';
import AddPaymentOutEditView from './components/AddPaymentOutEditView';
import PaymentsOutEditView from './components/PaymentOutEditView';
import { PaymentOutModel } from './reducers/state';

const manualLink = getManualLink("refunding-a-credit-note-via-payment-out");

const nameCondition = (paymentOut: PaymentOutModel) => paymentOut.type;

const getWindowWidth = () => window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 1920;

const getWindowHeight = () => window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 1080;

const styles = theme => ({
  dialog: {
    zIndex: zIndex.tooltip,
    padding: theme.spacing(1)
  },
  createLink: {
    display: "flex",
    alignItems: "center"
  },
  exitToApp: {
    marginLeft: theme.spacing(1),
    fontSize: "1.2rem"
  }
});

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Success",
        expression: "status is SUCCESS",
        active: false
      },
      {
        name: "Other",
        expression: "status not is SUCCESS",
        active: false
      }
    ]
  }
];

function PaymentsOut(
  {
    getFilters,
    getActivePaymentOutMethods,
    getAccounts,
    getLockedDate,
    getAdministrationSites,
    clearContraInvoices,
    location,
    onInit,
    updateSelection,
    setListFullScreenEditView,
    match: { params },
    classes
  })  {
  const [createNewDialogOpen, setCreateNewDialogOpen] = useState(false);

  const urlParams = useRef(new URLSearchParams(location.search));

  useEffect(() => {
    urlParams.current = new URLSearchParams(location.search);
  }, [location.search]);

  useEffect(() => {
    getFilters();
    getActivePaymentOutMethods();
    getAccounts();
    getLockedDate();
    getAdministrationSites();
    
    return () => {
      clearContraInvoices();
    };
  }, []);

  const closeCreateNewDialog = () => {
    if (!urlParams.current.get("invoiceId")) {
      updateHistory(location.search, location.pathname.replace('/new', ''));
    }
    setCreateNewDialogOpen(false);
  };

  const onCreateNew = () => {
    closeCreateNewDialog();
    updateSelection(["new"]);
    onInit(urlParams.current.get("invoiceId"));
    setListFullScreenEditView(true);
  };

  const openCreateNewDialog = () =>  {
    if (params.id === "new" && window.location.search?.includes("invoiceId")) {
      onCreateNew();
    } else if (!createNewDialogOpen) {
      setCreateNewDialogOpen(true);
    }
  };

  return (
    <div>
      <ListView
        listProps={{
          primaryColumn: "payee.fullName",
          secondaryColumn: "paymentMethod.name"
        }}
        editViewProps={{
          manualLink,
          nameCondition
        }}
        EditViewContent={props => props.isNew ?  <AddPaymentOutEditView {...props}/> : <PaymentsOutEditView {...props}/>}
        rootEntity="PaymentOut"
        filterGroupsInitial={filterGroups}
        findRelated={[
          { title: "Contacts", list: "contact", expression: "paymentsOut.id" },
          { title: "Invoices", list: "invoice", expression: "paymentOutLines.paymentOut" },
          { title: "Transactions", list: "transaction", expression: "paymentOut.id" },
          { title: "Audits", list: "audit", expression: "entityIdentifier == PaymentOut and entityId" }
        ]}
        defaultDeleteDisabled
        customOnCreate={() => openCreateNewDialog()}
        noListTags
      />
      <Popover
        open={createNewDialogOpen}
        onClose={() => closeCreateNewDialog()}
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
        <div className={classes.dialog}>
          <Link
            href={`${window.location.origin}/invoice?filter=@Credit_notes`}
            target="_blank"
            color="textSecondary"
            underline="none"
            className={classes.createLink}
          >
            <span>{$t('use_the_cogwheel_to_refund_credit_notes')}</span>
            {" "}
            <ExitToApp className={classes.exitToApp} />
          </Link>
        </div>
      </Popover>
    </div>
  );
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: id => {
    dispatch(getAddPaymentOutContact(id));
    dispatch(getAmountOwing(id));
  },
  getLockedDate: () => {
    dispatch(getAccountTransactionLockedDate());
  },
  getFilters: () => {
    dispatch(getFilters("PaymentOut"));
  },
  getAdministrationSites: () => dispatch(getAdministrationSites()),
  getAccounts: () => getPlainAccounts(dispatch),
  getActivePaymentOutMethods: () => dispatch(getActivePaymentOutMethods()),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  clearContraInvoices: () => dispatch(setContraInvoices(null)),
  setListFullScreenEditView: (fullScreenEditView: boolean) => dispatch(setListFullScreenEditView(fullScreenEditView)),
});

export default connect<any, any, any>(null, mapDispatchToProps)(withStyles(PaymentsOut, styles));