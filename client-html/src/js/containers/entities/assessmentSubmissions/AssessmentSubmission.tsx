/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { Dispatch, useCallback, useEffect } from "react";
import { connect } from "react-redux";
import { AssessmentSubmission as AssessmentSubmissionModel } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import { clearListState, getFilters } from "../../../common/components/list-view/actions";
import { getListTags } from "../../tags/actions";
import { FilterGroup } from "../../../model/common/ListView";
import AssessmentSubmissionEditView from "./components/AssessmentSubmissionsEditView";
import { getAssessmentSubmissionsItem, removeAssessmentSubmissionsItem, updateAssessmentSubmissionsItem } from "./actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import { State } from "../../../reducers/state";
import EntityService from "../../../common/services/EntityService";
import { getContactName } from "../contacts/utils";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";

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
    clearListState, getAssessmentSubmissionsItem, getFilters, getTags, onDelete, onSave, selection, dispatch
  } = props;

  useEffect(() => {
    getFilters();
    getTags();
    return () => {
      clearListState();
    };
  }, []);

  const getCustomBulkEditFields = useCallback(async () => {
    if (!selection || !selection.length) return [];

    let result = [];

    await EntityService.getPlainRecords(
      "Contact",
      "firstName,lastName",
      `tutor.assessmentClassTutors.assessmentClass.assessmentSubmissions.id ${selection.length > 1 ? "in" : "is"} ${selection}`
    )
      .then(res => {
        const tutors = (res.rows.map(r => ({
          label: getContactName({ firstName: r.values[0], lastName: r.values[1] }),
          value: Number(r.id),
        })));

        result = [
          {
            keyCode: "submittedOn",
            label: "Submitted On",
            name: "Submitted On",
            type: "Date"
          },
          {
            keyCode: "markedOn",
            label: "Marked On",
            name: "Marked On",
            type: "Date"
          },
          {
            keyCode: "markedById",
            label: "Assessor",
            name: "Assessor",
            type: "Select",
            items: tutors,
            defaultValue: "Not set",
          }
        ];
      })
      .catch(err => instantFetchErrorHandler(dispatch, err));

    return result;
  }, [selection]);

  return (
    <ListView
      listProps={{
        primaryColumn: "studentName",
        secondaryColumn: "assessmentName",
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
      CogwheelAdornment={BulkEditCogwheelOption}
      getCustomBulkEditFields={getCustomBulkEditFields}
    />
  );
};

const mapStateToProps = (state: State) => ({
  selection: state.list.selection,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getAssessmentSubmissionsItem: (id: number) => dispatch(getAssessmentSubmissionsItem(id)),
  clearListState: () => dispatch(clearListState()),
  getFilters: () => dispatch(getFilters("AssessmentSubmission")),
  getTags: () => dispatch(getListTags("AssessmentSubmission")),
  onSave: (id: number, assessmentSubmission: AssessmentSubmissionModel) => (
    dispatch(updateAssessmentSubmissionsItem(id, assessmentSubmission))),
  onDelete: (id: number) => dispatch(removeAssessmentSubmissionsItem(id))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(AssessmentSubmission);