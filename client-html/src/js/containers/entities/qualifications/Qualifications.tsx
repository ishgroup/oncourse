/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Qualification } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import QualificationsEditView from "./components/QualificationsEditView";

const filterGroups: FilterGroup[] = [
  {
    title: "STATUS",
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
  },

  {
    title: "TYPE",
    filters: [
      {
        name: "Qualification",
        expression: "type == QUALIFICATION_TYPE",
        active: false
      },
      {
        name: "Accredited courses",
        expression: "type == COURSE_TYPE",
        active: false
      },
      {
        name: "Skill sets",
        expression: "type == SKILLSET_TYPE",
        active: false
      },
      {
        name: "Local skill sets",
        expression: "type == SKILLSET_LOCAL_TYPE",
        active: false
      },
      {
        name: "Higher education",
        expression: "type == HIGHER_TYPE",
        active: false
      }
    ]
  }
];

const Initial: Qualification = {
  id: null,
  type: "Qualification",
  nationalCode: null,
  qualLevel: null,
  title: null,
  isOffered: false,
  fieldOfEducation: null,
  anzsco: null,
  newApprenticeship: null,
  isCustom: true,
  nominalHours: null,
  reviewDate: null,
  specialization: null
};

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Qualification and entityId" },
  { title: "Certificates", list: "certificate", expression: "qualification.id" },
  { title: "Class", list: "class", expression: "course.qualification.id" },
  { title: "Courses", list: "course", expression: "qualification.id" },
  { title: "Enrolments", list: "enrolment", expression: "courseClass.course.qualification.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.courseClass.course.qualification.id" }
];

const manualLink = getManualLink("rto_createQual");

const nameCondition = (item: Qualification) => item.title;

class Qualifications extends React.Component<any, any> {
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
    const { onInit } = this.props;

    return (
      <ListView
        listProps={{
          primaryColumn: "title",
          secondaryColumn: "nationalCode"
        }}
        editViewProps={{
          manualLink,
          nameCondition
        }}
        EditViewContent={QualificationsEditView}
        rootEntity="Qualification"
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
    dispatch(getFilters("Qualification"));
  },
  clearListState: () => dispatch(clearListState()),
});

export default connect<any, any, any>(null, mapDispatchToProps)(Qualifications);