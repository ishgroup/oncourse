/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Module } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import ModulesEditView from "./components/ModulesEditView";

const nameCondition = (values: Module) => values.title;

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Offered",
        expression: "isOffered == true",
        active: true
      },
      {
        name: "Not offered",
        expression: "isOffered == false",
        active: false
      }
    ]
  }
];

const Initial: Module = {
  creditPoints: null,
  expiryDays: null,
  fieldOfEducation: null,
  id: null,
  isCustom: true,
  type: "UNIT OF COMPETENCY",
  isOffered: false,
  nationalCode: null,
  nominalHours: null,
  specialization: null,
  title: null
};

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Module and entityId" },
  { title: "Certificates", list: "certificate", expression: "certificateOutcomes.outcome.module.id" },
  { title: "Classes", list: "class", expression: "course.modules.id" },
  { title: "Courses", list: "course", expression: "modules.id" },
  { title: "Enrolments", list: "enrolment", expression: "outcomes.module.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.outcomes.module.id" }
];

const manualLink = getManualLink("rto_createModules");

class Modules extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
  }

  componentWillUnmount() {
    this.props.clearListState();
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
          primaryColumn: "title",
          secondaryColumn: "nationalCode"
        }}
        editViewProps={{
          nameCondition,
          manualLink,
        }}
        EditViewContent={ModulesEditView}
        rootEntity="Module"
        onInit={onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
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
  getFilters: () => {
    dispatch(getFilters("Module"));
  },
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(null, mapDispatchToProps)(Modules);