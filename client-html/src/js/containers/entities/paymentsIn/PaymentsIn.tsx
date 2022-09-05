/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { useEffect, useState } from "react";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { PaymentIn } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import { getWindowHeight, getWindowWidth } from "../../../common/utils/common";
import { FilterGroup } from "../../../model/common/ListView";
import PaymentInsEditView from "./components/PaymentInEditView";
import {
  clearListState,
  getFilters,
 } from "../../../common/components/list-view/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getAccountTransactionLockedDate } from "../../preferences/actions";
import PaymentInCogwheel from "./components/PaymentInCogwheel";
import { getAdministrationSites } from "../sites/actions";
import { State } from "../../../reducers/state";
import { checkPermissions } from "../../../common/actions";

const isNotFailed = "(status !== FAILED and status !== FAILED_CARD_DECLINED and status !== FAILED_NO_PLACES and status !== CORRUPTED)";
const isSuccess = "status == SUCCESS";
const isFailed = "(status == FAILED or status == FAILED_CARD_DECLINED or status == FAILED_NO_PLACES or status == CORRUPTED)";
const bankingIsNull = "bankingId == null";
const bankingIsNotNull = "bankingId !== null";
// eslint-disable-next-line max-len
const isNotSystem = "(paymentMethod.type !== CONTRA and paymentMethod.type !== INTERNAL and paymentMethod.type !== REVERSE and paymentMethod.type !== VOUCHER)";
const reversable = "(paymentMethod.type !== INTERNAL and paymentMethod.type !== REVERSE and paymentMethod.type !== VOUCHER)";
// eslint-disable-next-line max-len
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

const manualLink = getManualLink("processingEnrolments_PaymentIn");

const nameCondition = (paymentIn: PaymentIn) => paymentIn.paymentInType;

const openCheckout = () => window.open("/checkout", "_self");
const openBatchPayment = () => window.open("/batchPayment", "_self");

const PaymentsIn = ({
    getFilters,
    getLockedDate,
    getAdministrationSites,
    getQePermissions,
    clearListState,
    hasQePermissions
  }) => {
  const [createMenuOpen, setCreateCreateMenuOpen] = useState(false);

  useEffect(() => {
    getFilters();
    getLockedDate();
    getAdministrationSites();
    getQePermissions();

    return clearListState;
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
            Create one payment...
          </MenuItem>
          <MenuItem
            onClick={openBatchPayment}
            disabled={!hasQePermissions}
            classes={{
              root: "listItemPadding"
            }}
          >
            Process all due payments...
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
  clearListState: () => dispatch(clearListState()),
  getQePermissions: () => dispatch(checkPermissions({ keyCode: "PAYMENT_IN_CREATE" }))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PaymentsIn);