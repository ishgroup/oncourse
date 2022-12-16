/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Assessment } from "@api/model";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
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
  onInit?: () => void;
  getFilters?: () => void;
  clearListState?: () => void;
  getTags?: () => void;
  getGradingTypes?: () => void;
}

const Initial: Assessment = {
  active: false,
  code: null,
  description: null,
  id: null,
  name: null,
  createdOn: null,
  modifiedOn: null,
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

const setRowClasses = ({ active }) => (active === "Yes" ? undefined : "text-op05");

const Assessments: React.FC<AssessmentsProps> = props => {
  const {
    onInit,
    getFilters,
    getGradingTypes,
    clearListState,
    getTags
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
        asyncChangeFields: ["notes[].message"],
        hideTitle: true
      }}
      EditViewContent={AssessmentEditView}
      CogwheelAdornment={BulkEditCogwheelOption}
      rootEntity="Assessment"
      onInit={onInit}
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
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(Assessments);