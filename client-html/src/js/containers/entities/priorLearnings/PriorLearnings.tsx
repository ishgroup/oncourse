/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PriorLearning, TableModel } from '@api/model';
import React, { Dispatch, useEffect } from 'react';
import { connect } from 'react-redux';
import { initialize } from 'redux-form';
import { checkPermissions } from '../../../common/actions';
import { getFilters, setListEditRecord, } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { fundingUploadsPath } from '../../../constants/Api';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import PriorLearningEditView from './components/PriorLearningEditView';

const nameCondition = (val: PriorLearning) => val.title;

interface PriorLearningsProps {
  onInit?: () => void;
  onCreate?: (priorLearning: PriorLearning) => void;
  getFilters?: () => void;
  checkPermissions?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
}

const Initial: PriorLearning = {};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "VET",
        expression: "outcomes.vet is true",
        active: false
      },
      {
        name: "non-VET",
        expression: "outcomes.vet is false",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Students", list: "contact", expression: "student.priorLearnings.id" },
  { title: "Outcomes", list: "outcome", expression: "priorLearning.id" }
];

const manualLink = getManualLink("importing-and-adding-prior-learning");

const PriorLearnings: React.FC<PriorLearningsProps> = props => {
  const {
    onInit,
    getFilters,
    checkPermissions
  } = props;

  useEffect(() => {
    getFilters();
    checkPermissions();
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "student.contact.fullName",
        secondaryColumn: "title"
      }}
      editViewProps={{
        nameCondition,
        manualLink,
        hideTitle: true
      }}
      EditViewContent={PriorLearningEditView}
      rootEntity="PriorLearning"
      onInit={onInit}
      filterGroupsInitial={filterGroups}
      findRelated={findRelatedGroup}
      alwaysFullScreenCreateView
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("PriorLearning")),
  checkPermissions: () => dispatch(checkPermissions({ path: fundingUploadsPath, method: "GET" }))
});

export default connect<any, any, any>(null, mapDispatchToProps)(PriorLearnings);