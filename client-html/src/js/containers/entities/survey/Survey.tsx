/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { Dispatch, useEffect, useState } from "react";
import { initialize } from "redux-form";
import { connect } from "react-redux";
import { CustomFieldType, SurveyItem, TableModel } from "@api/model";
import { defaultContactName } from "../contacts/utils";
import { updateSurveyItem, getSurveyItem } from "./actions";
import {
  getFilters,
  setListEditRecord,
  clearListState
} from "../../../common/components/list-view/actions";
import { FilterGroup } from "../../../model/common/ListView";
import ListView from "../../../common/components/list-view/ListView";
import SurveyEditView from "./components/SurveyEditView";
import { State } from "../../../reducers/state";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { Classes } from "../../../model/entities/CourseClass";

interface StudentFeedbackProps {
  getStudentFeedbackRecord?: () => void;
  onInit?: (initial: SurveyItem) => void;
  onSave?: (id: string, studentFeedback: SurveyItem) => void;
  getFilters?: () => void;
  clearListState?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  customFieldTypesUpdating?: boolean;
  customFieldTypes?: CustomFieldType[];
}

const Initial: SurveyItem = {
  comment: null,
  classId: 0,
  className: null,
  courseScore: 0,
  customFields: null,
  id: 0,
  netPromoterScore: 0,
  roomId: 0,
  roomName: null,
  siteId: 0,
  siteName: null,
  studentContactId: 0,
  studentName: null,
  testimonial: null,
  tutorScore: 0,
  venueScore: 0,
  visibility: "Waiting review"
};

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

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Survey and entityId" },
  { title: "Classes", list: Classes.path, expression: "enrolments.surveys.id" },
  { title: "Courses", list: "course", expression: "courseClasses.enrolments.surveys.id " },
  { title: "Rooms", list: "room", expression: "courseClasses.enrolments.surveys.id" },
  { title: "Sites", list: "site", expression: "rooms.courseClasses.enrolments.surveys.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.surveys.id" },
  { title: "Tutors", list: "contact", expression: "tutor.courseClassRoles.courseClass.enrolments.surveys.id" }
];

const StudentFeedbackComp: React.FC<StudentFeedbackProps> = props => {
  const {
    getStudentFeedbackRecord,
    onInit,
    onSave,
    getFilters,
    customFieldTypesUpdating,
    customFieldTypes
  } = props;

  const [initNew, setInitNew] = useState(false);

  useEffect(() => {
    getFilters();
    return () => {
      clearListState();
    };
  }, []);

  useEffect(() => {
    if (initNew && customFieldTypes && !customFieldTypesUpdating) {
      setInitNew(false);
      const customFields = {};
      // customFieldTypes.forEach((field: CustomFieldType) => {
      //   if (field.defaultValue && !field.defaultValue.match(/[;*]/g)) {
      //     customFields[field.fieldKey] = field.defaultValue;
      //   }
      // });
      onInit({ ...Initial, customFields });
    }
  }, [initNew, customFieldTypes, customFieldTypesUpdating]);

  return (
    <div>
      <ListView
        listProps={{
          primaryColumn: "enrolment.student.contact.fullName",
          secondaryColumn: "enrolment.courseClass.course.name"
        }}
        editViewProps={{
          nameCondition: values => values && defaultContactName(values.studentName)
        }}
        EditViewContent={SurveyEditView}
        getEditRecord={getStudentFeedbackRecord}
        rootEntity="Survey"
        onInit={() => setInitNew(true)}
        onSave={onSave}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        defaultDeleteDisabled
        noListTags
      />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  customFieldTypesUpdating: state.customFieldTypes.updating,
  customFieldTypes: state.customFieldTypes.types["Survey"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: (initial: SurveyItem) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getFilters: () => dispatch(getFilters("Survey")),
  clearListState: () => dispatch(clearListState()),
  getStudentFeedbackRecord: (id: string) => dispatch(getSurveyItem(id)),
  onSave: (id: string, surveyItem: SurveyItem) => dispatch(updateSurveyItem(id, surveyItem)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(StudentFeedbackComp);
