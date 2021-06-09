/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import ListView from "../../../common/components/list-view/ListView";
import { createTransaction, getTransaction } from "./actions";
import { Account, Transaction } from "@api/model";
import { FilterGroup } from "../../../model/common/ListView";
import TransactionsEditView from "./components/TransactionsEditView";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { State } from "../../../reducers/state";
import { getPlainAccounts } from "../accounts/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

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

const findRelatedGroup: any[] = [
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
    const {
      onCreate, getTransactionRecord, updateTableModel
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "account.accountCode",
            secondaryColumn: "account.type",
            primaryColumnCondition,
            secondaryColumnCondition
          }}
          updateTableModel={updateTableModel}
          EditViewContent={TransactionsEditView}
          getEditRecord={getTransactionRecord}
          rootEntity="AccountTransaction"
          editViewProps={{
            nameCondition: this.getTransactionAccountName
          }}
          onInit={this.onInit}
          onCreate={onCreate}
          onDelete={() => null}
          onSave={() => null}
          findRelated={findRelatedGroup}
          filterGroupsInitial={filterGroups}
          defaultDeleteDisabled
          noListTags
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  accounts: state.plainSearchRecords.Account.items,
  currency: state.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getTransactionRecord: (id: string) => dispatch(getTransaction(id)),
  getFilters: () => {
    dispatch(getFilters("AccountTransaction"));
  },
  getAccounts: () => getPlainAccounts(dispatch),
  clearListState: () => dispatch(clearListState()),
  onCreate: (transaction: Transaction) => dispatch(createTransaction(transaction))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(Transactions);
