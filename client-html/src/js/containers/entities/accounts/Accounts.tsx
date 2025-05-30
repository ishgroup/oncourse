/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account } from '@api/model';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import { getFilters, setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { getTaxTypes } from '../../preferences/actions';
import AccountsEditView from './components/AccountsEditView';

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

const findRelatedGroup: FindRelatedItem[] = [
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

  shouldComponentUpdate() {
    return false;
  }

  render() {
    const {
      onInit
    } = this.props;

    return (
      <ListView
        listProps={{
          primaryColumn: "description",
          secondaryColumn: "accountCode"
        }}
        editViewProps={{
          nameCondition,
          manualLink,
          hideTitle: true
        }}
        EditViewContent={AccountsEditView}
        rootEntity="Account"
        onInit={onInit}
        filterGroupsInitial={filterGroups}
        findRelated={findRelatedGroup}
        noListTags
      />
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
  }
});

export default connect<any, any, any>(null, mapDispatchToProps)(Accounts);