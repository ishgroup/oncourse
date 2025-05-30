/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Assessment } from '@api/model';
import React, { Dispatch, useEffect } from 'react';
import { connect } from 'react-redux';
import { initialize } from 'redux-form';
import { notesAsyncValidate } from '../../../common/components/form/notes/utils';
import { getFilters, setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { getGradingTypes } from '../../preferences/actions';
import { getListTags } from '../../tags/actions';
import BulkEditCogwheelOption from '../common/components/BulkEditCogwheelOption';
import AssessmentEditView from './components/AssessmentEditView';

const manualLink = getManualLink("assessment-tasks-list-view");

interface AssessmentsProps {
  onInit?: () => void;
  getFilters?: () => void;
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

const findRelatedGroup: FindRelatedItem[] = [
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
    getTags
  } = props;

  useEffect(() => {
    getFilters();
    getTags();
    getGradingTypes();
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
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(Assessments);