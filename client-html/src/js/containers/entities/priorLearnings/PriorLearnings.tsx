/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { PriorLearning, TableModel } from "@api/model";
import { checkPermissions } from "../../../common/actions";
import ListView from "../../../common/components/list-view/ListView";
import { fundingUploadsPath } from "../../../constants/Api";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import PriorLearningEditView from "./components/PriorLearningEditView";
import {
  clearListState,
  getFilters,
  setListEditRecord,
 } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getManualLink } from "../../../common/utils/getManualLink";

const nameCondition = (val: PriorLearning) => val.title;

interface PriorLearningsProps {
  onInit?: () => void;
  onCreate?: (priorLearning: PriorLearning) => void;
  getFilters?: () => void;
  clearListState?: () => void;
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

const manualLink = getManualLink("delivery_rpl");

const PriorLearnings: React.FC<PriorLearningsProps> = props => {
  const {
    onInit,
    getFilters,
    clearListState,
    checkPermissions
  } = props;

  useEffect(() => {
    getFilters();
    checkPermissions();
    return () => {
      clearListState();
    };
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
  clearListState: () => dispatch(clearListState()),
  checkPermissions: () => dispatch(checkPermissions({ path: fundingUploadsPath, method: "GET" }))
});

export default connect<any, any, any>(null, mapDispatchToProps)(PriorLearnings);