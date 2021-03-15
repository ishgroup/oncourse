/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { AssessmentSubmission as AssessmentSubmissionModel } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import { clearListState, getFilters } from "../../../common/components/list-view/actions";
import { getListTags } from "../../tags/actions";
import { FilterGroup } from "../../../model/common/ListView";
import AssessmentSubmissionEditView from "./components/AssessmentSubmissionsEditView";
import { getAssessmentSubmissionsItem, removeAssessmentSubmissionsItem, updateAssessmentSubmissionsItem } from "./actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Marked",
        expression: "markedOn not is null",
        active: false
      }
    ]
  }
];

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == AssessmentSubmission and entityId" },
  { title: "Assessments", list: "assessment", expression: "assessmentClasses.assessmentSubmissions.id" },
  { title: "Classes", list: "class", expression: "assessmentClasses.assessmentSubmissions.id" },
  { title: "Enrolments", list: "enrolment", expression: "assessmentSubmissions.id" },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == AssessmentSubmission and attachmentRelations.entityRecordId"
  },
];

const nameCondition = (val: AssessmentSubmissionModel) => val.studentName;

const AssessmentSubmission = (props: any) => {
  const {
    clearListState, getAssessmentSubmissionsItem, getFilters, getTags, onDelete, onSave
  } = props;
  useEffect(() => {
    getFilters();
    getTags();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "studentName",
        secondaryColumn: "createdOn",
      }}
      editViewProps={{
        nameCondition,
        asyncValidate: notesAsyncValidate,
        asyncBlurFields: ["notes[].message"]
      }}
      EditViewContent={AssessmentSubmissionEditView}
      getEditRecord={getAssessmentSubmissionsItem}
      rootEntity="AssessmentSubmission"
      filterGroupsInitial={filterGroups}
      findRelated={findRelatedGroup}
      onSave={onSave}
      onDelete={onDelete}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getAssessmentSubmissionsItem: (id: number) => dispatch(getAssessmentSubmissionsItem(id)),
  clearListState: () => dispatch(clearListState()),
  getFilters: () => dispatch(getFilters("AssessmentSubmission")),
  getTags: () => dispatch(getListTags("AssessmentSubmission")),
  onSave: (id: number, assessmentSubmission: AssessmentSubmissionModel) => (
    dispatch(updateAssessmentSubmissionsItem(id, assessmentSubmission))),
  onDelete: (id: number) => dispatch(removeAssessmentSubmissionsItem(id))
});

export default connect<any, any, any>(null, mapDispatchToProps)(AssessmentSubmission);