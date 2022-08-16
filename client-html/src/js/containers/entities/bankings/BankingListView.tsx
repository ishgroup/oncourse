/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { connect } from "react-redux";
import { Dispatch } from "redux";
import * as React from "react";
import { Banking } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import {
  getFilters,
  clearListState,
  setListEditRecord,
} from "../../../common/components/list-view/actions";
import { FilterGroup } from "../../../model/common/ListView";
import { initDeposit } from "./actions";
import BankingEditViewResolver from "./components/BankingEditViewResolver";
import BankingCogwheelOptions from "./components/BankingCogwheelOptions";
import { getAccountTransactionLockedDate, getCurrency } from "../../preferences/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getAdministrationSites } from "../sites/actions";

const manualLink = getManualLink("accounting_Deposit");

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Manual",
        expression: "type is MANUAL ",
        active: false
      },
      {
        name: "Auto",
        expression: "type not is MANUAL ",
        active: false
      }
    ]
  }
];

class BankingListView extends React.Component<any, null> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getCurrency();
    this.props.getLockedDate();
    this.props.getAdministrationSites();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  render() {
    const { onInit } = this.props;
    return (
      <ListView
        listProps={{
          primaryColumn: "settlementDate",
          secondaryColumn: "type"
        }}
        EditViewContent={BankingEditViewResolver}
        rootEntity="Banking"
        onInit={onInit}
        editViewProps={{
          manualLink,
          hideTitle: true
        }}
        filterGroupsInitial={filterGroups}
        findRelated={[
          { title: "Transactions", list: "transaction", expression: "banking.id" },
          { title: "Payments in", list: "paymentIn", expression: "banking.id" },
          { title: "Payments out", list: "paymentOut", expression: "banking.id" },
          { title: "Invoices", list: "invoice", expression: "banking.id" },
          { title: "Audits", list: "audit", expression: "entityIdentifier == Banking and entityId" }
        ]}
        CogwheelAdornment={BankingCogwheelOptions}
        alwaysFullScreenCreateView
        noListTags
      />
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(null));
    dispatch(initDeposit());
  },
  getAdministrationSites: () => dispatch(getAdministrationSites()),
  getCurrency: () => dispatch(getCurrency()),
  getFilters: () => {
    dispatch(getFilters("Banking"));
  },
  getLockedDate: () => dispatch(getAccountTransactionLockedDate()),
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(BankingListView);