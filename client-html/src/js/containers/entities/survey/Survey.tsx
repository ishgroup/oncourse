/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CustomFieldType, SurveyItem, TableModel } from "@api/model";
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { clearListState, getFilters } from "../../../common/components/list-view/actions";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import SurveyEditView from "./components/SurveyEditView";

interface StudentFeedbackProps {
  onInit?: (initial: SurveyItem) => void;
  getFilters?: () => void;
  clearListState?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  customFieldTypesUpdating?: boolean;
  customFieldTypes?: CustomFieldType[];
}

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Waiting review",
        expression: "visibility == REVIEW",
        active: false
      },
      {
        name: "Public testimonial",
        expression: "visibility == TESTIMONIAL",
        active: false
      },
      {
        name: "Not testimonial",
        expression: "visibility == NOT_TESTIMONIAL",
        active: false
      },
      {
        name: "Hidden by student",
        expression: "visibility == STUDENT_HIDDEN",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Survey and entityId" },
  { title: "Classes", list: "class", expression: "enrolments.surveys.id" },
  { title: "Courses", list: "course", expression: "courseClasses.enrolments.surveys.id " },
  { title: "Rooms", list: "room", expression: "courseClasses.enrolments.surveys.id" },
  { title: "Sites", list: "site", expression: "rooms.courseClasses.enrolments.surveys.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.surveys.id" },
  { title: "Tutors", list: "contact", expression: "tutor.courseClassRoles.courseClass.enrolments.surveys.id" }
];

const StudentFeedbackComp: React.FC<StudentFeedbackProps> = props => {
  const {
    getFilters,
  } = props;

  useEffect(() => {
    getFilters();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "enrolment.student.contact.fullName",
        secondaryColumn: "enrolment.courseClass.course.name"
      }}
      editViewProps={{
        nameCondition: values => values?.studentName,
        hideTitle: true
      }}
      EditViewContent={SurveyEditView}
      rootEntity="Survey"
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      createButtonDisabled
      defaultDeleteDisabled
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getFilters: () => dispatch(getFilters("Survey")),
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(null, mapDispatchToProps)(StudentFeedbackComp);