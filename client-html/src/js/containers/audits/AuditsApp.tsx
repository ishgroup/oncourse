/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { format } from "date-fns";
import ListView from "../../common/components/list-view/ListView";
import { FilterGroup } from "../../model/common/ListView";
import {
  getRecords,
  clearListState,
  getFilters,
 } from "../../common/components/list-view/actions";
import AuditsEditView from "./components/AuditsEditView";
import AuditFindRelatedMenu from "./components/AuditFindRelatedMenu";
import { III_DD_MMM_YYYY_HH_MM_SPECIAL } from "../../common/utils/dates/format";
import { getManualLink } from "../../common/utils/getManualLink";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Mine",
        expression: "systemUser is me",
        active: false
      },
      {
        name: "Others",
        expression: "systemUser not is me",
        active: false
      }
    ]
  }
];

// 'entityIdentifier' here is a special field for filtering in AuditFindRelatedMenu
const findRelatedGroup: any[] = [
  {
    title: "Accounts", list: "account", expression: "id", entityIdentifier: "Account"
  },
  {
    title: "Units Of Competency", list: "module", expression: "id", entityIdentifier: "Module"
  },
  {
    title: "Qualifications", list: "qualification", expression: "id", entityIdentifier: "Qualification"
  },
  {
    title: "Rooms", list: "room", expression: "id", entityIdentifier: "Room"
  },
  {
    title: "Sites", list: "site", expression: "id", entityIdentifier: "Site"
  },
  {
    title: "Transactions", list: "transaction", expression: "id", entityIdentifier: "AccountTransaction"
  },
  {
    title: "Invoices", list: "invoice", expression: "id", entityIdentifier: "Invoice"
  },
  {
    title: "Banking Deposits", list: "banking", expression: "id", entityIdentifier: "Banking"
  },
  {
    title: "Corporate Pass", list: "corporatePass", expression: "id", entityIdentifier: "CorporatePass"
  },
  {
    title: "Payments In", list: "paymentIn", expression: "id", entityIdentifier: "PaymentIn"
  },
  {
    title: "Tutor Pay", list: "payslip", expression: "id", entityIdentifier: "Payslip"
  },
  { title: "Classes", destination: "CourseClass", entityIdentifier: "CourseClass" },
  { title: "Contacts", destination: "Contact", entityIdentifier: "Contact" },
  { title: "Courses", destination: "Course", entityIdentifier: "Course" },
  { title: "Enrolments", destination: "Enrolment", entityIdentifier: "Enrolment" }
];

const customColumnFormats = {
  created: v => (v ? format(new Date(v), III_DD_MMM_YYYY_HH_MM_SPECIAL) : null)
};

const manualLink = getManualLink("advancedSetup_Help");

const nameCondition = audit => audit.action;

class AuditsApp extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getRecords();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  render() {
    return (
      <ListView
        listProps={{
          customColumnFormats,
          primaryColumn: "action",
          secondaryColumn: "created"
        }}
        editViewProps={{
          manualLink,
          nameCondition
        }}
        EditViewContent={AuditsEditView}
        rootEntity="Audit"
        filterGroupsInitial={filterGroups}
        findRelated={findRelatedGroup}
        CustomFindRelatedMenu={AuditFindRelatedMenu}
        defaultDeleteDisabled
        createButtonDisabled
        noListTags
      />
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getRecords: () => {
    dispatch(getRecords({ entity: "Audit" }));
  },
  getFilters: () => {
    dispatch(getFilters("Audit"));
  },
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(AuditsApp);