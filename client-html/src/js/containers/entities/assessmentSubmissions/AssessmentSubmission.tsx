/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import ListView from "../../../common/components/list-view/ListView";
import { clearListState, getFilters } from "../../../common/components/list-view/actions";
import { getListTags } from "../../tags/actions";
import {FilterGroup} from "../../../model/common/ListView";

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

const AssessmentSubmission = (props: any) => {
  const { clearListState, getFilters, getTags } = props;
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
        primaryColumn: "createdOn",
        secondaryColumn: "studentName",
      }}
      EditViewContent={<div />}
      getEditRecord={() => []}
      rootEntity="AssessmentSubmission"
      filterGroupsInitial={filterGroups}
      findRelated={findRelatedGroup}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  clearListState: () => dispatch(clearListState()),
  getFilters: () => dispatch(getFilters("AssessmentSubmission")),
  getTags: () => dispatch(getListTags("AssessmentSubmission")),
});

export default connect<any, any, any>(null, mapDispatchToProps)(AssessmentSubmission);