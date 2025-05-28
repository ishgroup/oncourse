/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { getFilters, setListEditRecord, } from '../../../common/components/list-view/actions';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup } from '../../../model/common/ListView';
import { getAccountTransactionLockedDate, getLocation } from '../../preferences/actions';
import { getAdministrationSites } from '../sites/actions';
import { initDeposit } from './actions';
import BankingCogwheelOptions from './components/BankingCogwheelOptions';
import BankingEditViewResolver from './components/BankingEditViewResolver';

const manualLink = getManualLink("banking-and-reconciliation");

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
  getCurrency: () => dispatch(getLocation()),
  getFilters: () => {
    dispatch(getFilters("Banking"));
  },
  getLockedDate: () => dispatch(getAccountTransactionLockedDate())
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(BankingListView);