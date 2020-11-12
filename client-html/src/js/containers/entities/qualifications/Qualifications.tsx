/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Dispatch } from "redux";
import ListView from "../../../common/components/list-view/ListView";
import {
  createQualification, getQualification, removeQualification, updateQualification
} from "./actions";
import { Qualification } from "@api/model";
import { FilterGroup } from "../../../model/common/ListView";
import QualificationsEditView from "./components/QualificationsEditView";
import {
  clearListState,
  getFilters,
  setListEditRecord
} from "../../../common/components/list-view/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

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

const findRelatedGroup: any[] = [
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

  onSave = (id: string, item: Qualification) => {
    if (item.isOffered === undefined) {
      item.isOffered = false;
    }

    this.props.onSave(id, item);
  };

  onCreate = (item: Qualification) => {
    if (item.isOffered === undefined) {
      item.isOffered = false;
    }

    this.props.onCreate(item);
  };

  render() {
    const {
      onDelete, getQualificationRecord, onInit
    } = this.props;

    return (
      <div>
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
          getEditRecord={getQualificationRecord}
          rootEntity="Qualification"
          onDelete={onDelete}
          onInit={onInit}
          onCreate={this.onCreate}
          onSave={this.onSave}
          findRelated={findRelatedGroup}
          filterGroupsInitial={filterGroups}
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
  getFilters: () => {
    dispatch(getFilters("Qualification"));
  },
  clearListState: () => dispatch(clearListState()),
  getQualificationRecord: (id: string) => dispatch(getQualification(id)),
  onSave: (id: string, qualification: Qualification) => dispatch(updateQualification(id, qualification)),
  onCreate: (qualification: Qualification) => dispatch(createQualification(qualification)),
  onDelete: (id: string) => dispatch(removeQualification(id))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Qualifications);
