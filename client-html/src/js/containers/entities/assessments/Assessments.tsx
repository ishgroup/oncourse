/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Assessment } from "@api/model";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import {
  setListEditRecord, getFilters, clearListState
} from "../../../common/components/list-view/actions";
import {
  getAssessment, updateAssessment, removeAssessment, createAssessment
} from "./actions";
import AssessmentEditView from "./components/AssessmentEditView";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup } from "../../../model/common/ListView";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getListTags } from "../../tags/actions";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import { getGradingTypes } from "../../preferences/actions";

const manualLink = getManualLink("assessment");

interface AssessmentsProps {
  getAssessmentRecord?: () => void;
  onInit?: () => void;
  onSave?: (id: string, assessment: Assessment) => void;
  getFilters?: () => void;
  clearListState?: () => void;
  onDelete?: (id: string) => void;
  getTags?: () => void;
  getGradingTypes?: () => void;
  onCreate: (assessment: Assessment) => void;
}

const Initial: Assessment = {
  active: false,
  code: null,
  description: null,
  id: null,
  name: null,
  createdOn: null,
  modifiedOn: null,
  documents: [],
  tags: []
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Active",
        expression: "active == true",
        active: true
      },
      {
        name: "Not active",
        expression: "active == false",
        active: false
      }
    ]
  }
];

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Assessment and entityId" },
  { title: "Classes", list: "class", expression: "assessmentClasses.assessment.id" },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Assessment and attachmentRelations.entityRecordId"
  },
  { title: "Submissions", list: "assessmentSubmission", expression: "assessmentClass.assessment.id" }
];

const setRowClasses = ({ active }) => (active === "Yes" ? undefined : "op05");

const Assessments: React.FC<AssessmentsProps> = props => {
  const {
    getAssessmentRecord,
    onInit,
    onDelete,
    onSave,
    getFilters,
    getGradingTypes,
    clearListState,
    getTags,
    onCreate
  } = props;

  useEffect(() => {
    getFilters();
    getTags();
    getGradingTypes();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "code",
        secondaryColumn: "name",
        setRowClasses
      }}
      editViewProps={{
        manualLink,
        asyncValidate: notesAsyncValidate,
        asyncBlurFields: ["notes[].message"]
      }}
      EditViewContent={AssessmentEditView}
      CogwheelAdornment={BulkEditCogwheelOption}
      getEditRecord={getAssessmentRecord}
      rootEntity="Assessment"
      onInit={onInit}
      onCreate={onCreate}
      onDelete={onDelete}
      onSave={onSave}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getGradingTypes: () => dispatch(getGradingTypes()),
  getTags: () => dispatch(getListTags("Assessment")),
  getFilters: () => dispatch(getFilters("Assessment")),
  clearListState: () => dispatch(clearListState()),
  getAssessmentRecord: (id: string) => dispatch(getAssessment(id)),
  onSave: (id: string, assessment: Assessment) => dispatch(updateAssessment(id, assessment)),
  onDelete: (id: string) => dispatch(removeAssessment(id)),
  onCreate: (assessment: Assessment) => dispatch(createAssessment(assessment))
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(Assessments);
