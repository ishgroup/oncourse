/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, Transaction } from '@api/model';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import { clearListState, getFilters, setListEditRecord, } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getPlainAccounts } from '../accounts/actions';
import TransactionsEditView from './components/TransactionsEditView';

const primaryColumnCondition = rows => `${rows["transactionDate"]}  ${rows["amount"]}`;

const secondaryColumnCondition = rows => `${rows["account.description"]} ${rows["account.accountCode"]}`;

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Invoices",
        expression: "tableName == 'I'",
        active: true
      },
      {
        name: "Payments",
        expression: "tableName == 'P' or tableName == 'O'",
        active: true
      },
      {
        name: "Other",
        expression: "tableName != 'O' and tableName != 'P' and tableName != 'I'",
        active: false
      }
    ]
  }
];

const Initial: Transaction = {
  fromAccount: null,
  toAccount: null,
  amount: null,
  transactionDate: null
};

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == AccountTransaction and entityId" },
  { title: "Contacts", list: "contact", expression: "accountTransactions.id" },
  { title: "Invoices", list: "invoice", expression: "accountTransactions.id" },
  { title: "Payment In", list: "paymentIn", expression: "accountTransactions.id" },
  { title: "Payment Out", list: "paymentOut", expression: "accountTransactions.id" }
];

class Transactions extends React.Component<any, any> {
  componentDidMount() {
    this.props.getAccounts();
    this.props.getFilters();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  shouldComponentUpdate() {
    return false;
  }

  onInit = () => {
    const { dispatch, accounts } = this.props;

    Initial.fromAccount = accounts[1].id;
    Initial.toAccount = accounts[2].id;
    Initial.amount = 0;
    Initial.transactionDate = new Date().toISOString();

    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  };

  getTransactionAccountName = (value: Transaction) => {
    const { accounts } = this.props;

    const account = accounts && accounts.find((acc: Account) => acc.id === value.fromAccount);

    return account ? `${account.description} ${account.accountCode}` : "";
  };

  render() {
    return (
      <ListView
        listProps={{
          primaryColumn: "account.accountCode",
          secondaryColumn: "account.type",
          primaryColumnCondition,
          secondaryColumnCondition
        }}
        EditViewContent={TransactionsEditView}
        rootEntity="AccountTransaction"
        editViewProps={{
          nameCondition: this.getTransactionAccountName
        }}
        onInit={this.onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        defaultDeleteDisabled
        noListTags
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  accounts: state.plainSearchRecords.Account.items,
  currency: state.location.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getFilters: () => {
    dispatch(getFilters("AccountTransaction"));
  },
  getAccounts: () => getPlainAccounts(dispatch),
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(Transactions);