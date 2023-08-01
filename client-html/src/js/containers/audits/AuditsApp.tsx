/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { format } from "date-fns";
import { III_DD_MMM_YYYY_HH_MM_SPECIAL } from "ish-ui";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { clearListState, getFilters, getRecords, } from "../../common/components/list-view/actions";
import ListView from "../../common/components/list-view/ListView";
import { getManualLink } from "../../common/utils/getManualLink";
import { FilterGroup, FindRelatedItem } from "../../model/common/ListView";
import AuditFindRelatedMenu from "./components/AuditFindRelatedMenu";
import AuditsEditView from "./components/AuditsEditView";

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
const findRelatedGroup: FindRelatedItem[] = [
  {
    title: "Accounts", list: "account", expression: "id"
  },
  {
    title: "Units Of Competency", list: "module", expression: "id"
  },
  {
    title: "Qualifications", list: "qualification", expression: "id"
  },
  {
    title: "Rooms", list: "room", expression: "id"
  },
  {
    title: "Sites", list: "site", expression: "id"
  },
  {
    title: "Transactions", list: "transaction", expression: "id"
  },
  {
    title: "Invoices", list: "invoice", expression: "id"
  },
  {
    title: "Banking Deposits", list: "banking", expression: "id"
  },
  {
    title: "Corporate Pass", list: "corporatePass", expression: "id"
  },
  {
    title: "Payments In", list: "paymentIn", expression: "id"
  },
  {
    title: "Tutor Pay", list: "payslip", expression: "id"
  },
  { title: "Classes", destination: "CourseClass" },
  { title: "Contacts", destination: "Contact" },
  { title: "Courses", destination: "Course" },
  { title: "Enrolments", destination: "Enrolment" }
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