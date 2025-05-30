/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { PaymentIn } from '@api/model';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { checkPermissions } from '../../../common/actions';
import { getFilters, } from '../../../common/components/list-view/actions';
import ListView from '../../../common/components/list-view/ListView';
import { getWindowHeight, getWindowWidth } from '../../../common/utils/common';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getAccountTransactionLockedDate } from '../../preferences/actions';
import { getAdministrationSites } from '../sites/actions';
import PaymentInCogwheel from './components/PaymentInCogwheel';
import PaymentInsEditView from './components/PaymentInEditView';

const isNotFailed = "(status !== FAILED and status !== FAILED_CARD_DECLINED and status !== FAILED_NO_PLACES and status !== CORRUPTED)";
const isSuccess = "status == SUCCESS";
const isFailed = "(status == FAILED or status == FAILED_CARD_DECLINED or status == FAILED_NO_PLACES or status == CORRUPTED)";
const bankingIsNull = "bankingId == null";
const bankingIsNotNull = "bankingId !== null";

const isNotSystem = "(paymentMethod.type !== CONTRA and paymentMethod.type !== INTERNAL and paymentMethod.type !== REVERSE and paymentMethod.type !== VOUCHER)";
const reversable = "(paymentMethod.type !== INTERNAL and paymentMethod.type !== REVERSE and paymentMethod.type !== VOUCHER)";

const isSystem = "(paymentMethod.type == CONTRA or paymentMethod.type == INTERNAL or paymentMethod.type == REVERSE or paymentMethod.type == VOUCHER)";
const isNotReversed = "reversedById == null";
const isReversal = "reversalOfId !== null";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Unbanked",
        // tslint:disable-next-line:max-line-length
        expression: `${bankingIsNull} and ${isNotFailed} and ${isNotSystem}`,
        active: true
      },
      {
        name: "Banked",
        expression: `${bankingIsNotNull} and ${isNotFailed} and ${isNotSystem} and ${isNotReversed}`,
        active: false
      },
      {
        name: "Failed",
        expression: `${isFailed} and ${isNotSystem}`,
        active: false
      },
      {
        name: "System",
        expression: isSystem,
        active: false
      },
      {
        name: "Reversed",
        expression: `${isSuccess} and ${reversable} and ${isReversal}`,
        active: false
      }
    ]
  }
];

const manualLink = getManualLink("processing-a-payment-in");

const nameCondition = (paymentIn: PaymentIn) => paymentIn.paymentInType;

const openCheckout = () => window.open("/checkout", "_self");
const openBatchPayment = () => window.open("/batchPayment", "_self");

const PaymentsIn = ({
    getFilters,
    getLockedDate,
    getAdministrationSites,
    getQePermissions,
    hasQePermissions
  }) => {
  const [createMenuOpen, setCreateCreateMenuOpen] = useState(false);

  useEffect(() => {
    getFilters();
    getLockedDate();
    getAdministrationSites();
    getQePermissions();
  }, []);

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "payer.fullName",
            secondaryColumn: "paymentMethod.name"
          }}
          editViewProps={{
            manualLink,
            nameCondition
          }}
          EditViewContent={PaymentInsEditView}
          rootEntity="PaymentIn"
          onInit={() => setCreateCreateMenuOpen(true)}
          findRelated={[
            { title: "Audits", list: "audit", expression: "entityIdentifier == PaymentIn and entityId" },
            { title: "Contacts", list: "contact", expression: "paymentsIn.id" },
            { title: "Invoices", list: "invoice", expression: "paymentInLines.paymentIn" },
            { title: "Transactions", list: "transaction", expression: "paymentIn.id" },
            { title: "Voucher redeemed", list: "sale", expression: "redeemedPaymentIn.id" }
          ]}
          filterGroupsInitial={filterGroups}
          CogwheelAdornment={PaymentInCogwheel}
          createButtonDisabled={!hasQePermissions}
          defaultDeleteDisabled
          customOnCreate
          noListTags
        />

        <Menu
          id="createMenu"
          open={createMenuOpen}
          onClose={() => setCreateCreateMenuOpen(false)}
          disableAutoFocusItem
          anchorReference="anchorPosition"
          anchorPosition={{ top: getWindowHeight() - 100, left: getWindowWidth() - 200 }}
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
            onClick={openCheckout}
            disabled={!hasQePermissions}
            classes={{
              root: "listItemPadding"
            }}
          >
            {$t('create_one_payment')}
          </MenuItem>
          <MenuItem
            onClick={openBatchPayment}
            disabled={!hasQePermissions}
            classes={{
              root: "listItemPadding"
            }}
          >
            {$t('process_all_due_payments')}
          </MenuItem>
        </Menu>
      </div>
    );
};

const mapStateToProps = (state: State) => ({
  hasQePermissions: state.access["PAYMENT_IN_CREATE"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getLockedDate: () => {
    dispatch(getAccountTransactionLockedDate());
  },
  getFilters: () => {
    dispatch(getFilters("PaymentIn"));
  },
  getAdministrationSites: () => dispatch(getAdministrationSites()),
  getQePermissions: () => dispatch(checkPermissions({ keyCode: "PAYMENT_IN_CREATE" }))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PaymentsIn);