/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { connect } from "react-redux";
import { Dispatch } from "redux";
import * as React from "react";
import { initialize } from "redux-form";
import { Account } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup } from "../../../model/common/ListView";
import AccountsEditView from "./components/AccountsEditView";
import {
  setListEditRecord,
  getFilters,
 clearListState
} from "../../../common/components/list-view/actions";
import {
  getAccount, updateAccount, createAccount, removeAccount
} from "./actions";
import { getTaxTypes } from "../../preferences/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

const Initial: Account = {
  accountCode: null,
  description: null,
  isEnabled: true,
  tax: null,
  type: null
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Enabled",
        expression: "isEnabled is true",
        active: false
      },
      {
        name: "Not enabled",
        expression: "isEnabled not is true  ",
        active: false
      }
    ]
  }
];

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Account and entityId" },
  { title: "Invoices", list: "invoice", expression: "invoiceLines.account.id" },
  { title: "Transactions", list: "transaction", expression: "account.id" }
];

const nameCondition = (values: Account) => values.accountCode;

const manualLink = getManualLink("accounting");

class Accounts extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.onInit();
    this.props.getTaxTypes();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  shouldComponentUpdate() {
    return false;
  }

  render() {
    const {
      onCreate, getAccountRecord, onDelete, onSave, onInit
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "description",
            secondaryColumn: "accountCode"
          }}
          editViewProps={{
            nameCondition,
            manualLink
          }}
          EditViewContent={AccountsEditView}
          getEditRecord={getAccountRecord}
          rootEntity="Account"
          onInit={onInit}
          onCreate={onCreate}
          onDelete={onDelete}
          onSave={onSave}
          filterGroupsInitial={filterGroups}
          findRelated={findRelatedGroup}
          noListTags
        />
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getTaxTypes: () => dispatch(getTaxTypes()),
  getFilters: () => {
    dispatch(getFilters("Account"));
  },
  clearListState: () => dispatch(clearListState()),
  getAccountRecord: (id: string) => dispatch(getAccount(id)),
  onSave: (id: string, account: Account) => dispatch(updateAccount(id, account)),
  onCreate: (account: Account) => dispatch(createAccount(account)),
  onDelete: (id: string) => dispatch(removeAccount(id))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Accounts);
