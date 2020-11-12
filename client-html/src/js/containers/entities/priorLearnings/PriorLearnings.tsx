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
import { FilterGroup } from "../../../model/common/ListView";
import PriorLearningEditView from "./components/PriorLearningEditView";
import {
  clearListState,
  getFilters,
  setListEditRecord,
 } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import {
  createPriorLearning, deletePriorLearning, getPriorLearning, updatePriorLearning
} from "./actions";
import { getManualLink } from "../../../common/utils/getManualLink";

const nameCondition = (val: PriorLearning) => val.title;

interface PriorLearningsProps {
  getPriorLearningRecord?: () => void;
  onInit?: () => void;
  onSave?: (id: string, priorLearning: PriorLearning) => void;
  onCreate?: (priorLearning: PriorLearning) => void;
  getFilters?: () => void;
  clearListState?: () => void;
  checkPermissions?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  onDelete?: (id: string) => void;
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

const findRelatedGroup: any[] = [
  { title: "Students", list: "contact", expression: "student.priorLearnings.id" },
  { title: "Outcomes", list: "outcome", expression: "priorLearning.id" }
];

const manualLink = getManualLink("delivery_rpl");

const PriorLearnings: React.FC<PriorLearningsProps> = props => {
  const {
    getPriorLearningRecord,
    onInit,
    onSave,
    onCreate,
    getFilters,
    clearListState,
    onDelete,
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
        manualLink
      }}
      EditViewContent={PriorLearningEditView}
      getEditRecord={getPriorLearningRecord}
      rootEntity="PriorLearning"
      onInit={onInit}
      onSave={onSave}
      onCreate={onCreate}
      onDelete={onDelete}
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
  getPriorLearningRecord: (id: string) => dispatch(getPriorLearning(id)),
  onSave: (id: string, priorLearning: PriorLearning) => dispatch(updatePriorLearning(id, priorLearning)),
  onDelete: (id: string) => dispatch(deletePriorLearning(id)),
  onCreate: (priorLearning: PriorLearning) => dispatch(createPriorLearning(priorLearning)),
  checkPermissions: () => dispatch(checkPermissions({ path: fundingUploadsPath, method: "GET" }))
});

export default connect<any, any, any>(null, mapDispatchToProps)(PriorLearnings);
