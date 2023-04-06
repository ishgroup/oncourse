/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Outcome } from "@api/model";
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { checkPermissions } from "../../../common/actions";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { fundingUploadsPath } from "../../../constants/Api";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import { getOutcomeTags } from "./actions";
import OutcomeEditView from "./components/OutcomeEditView";

interface OutcomesProps {
  onInit?: () => void;
  getFilters?: () => void;
  getTags?: () => void;
  clearListState?: () => void;
  checkPermissions?: () => void;
}

export const OutcomeInitial: Outcome = {
  id: null,
  contactId: null,
  enrolmentId: null,
  studentName: null,
  moduleId: null,
  moduleCode: null,
  moduleName: null,
  startDate: null,
  endDate: null,
  reportableHours: 0,
  deliveryMode: "Not Set",
  fundingSource: "Commonwealth - specific",
  status: "Not set",
  hoursAttended: 0,
  vetPurchasingContractID: null,
  vetPurchasingContractScheduleID: null,
  vetFundingSourceStateID: null,
  specificProgramIdentifier: null,
  isPriorLearning: false,
  hasCertificate: null,
  printed: false,
  createdOn: null
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "In progress",
        expression: "status == STATUS_NOT_SET",
        active: true
      },
      {
        name: "Pass",
        expression: "(status == STATUS_ASSESSABLE_PASS) or (status == STATUS_NON_ASSESSABLE_COMPLETED)",
        active: false
      },
      {
        name: "Fail",
        expression: "(status == STATUS_ASSESSABLE_FAIL) or (status == STATUS_NON_ASSESSABLE_NOT_COMPLETED)",
        active: false
      },
      {
        name: "Withdrawn",
        expression: "(status == STATUS_ASSESSABLE_WITHDRAWN)",
        active: false
      },
      {
        name: "Other",
        expression:
          "(status != STATUS_NOT_SET)"
          + " and (status != STATUS_ASSESSABLE_PASS)"
          + " and (status != STATUS_NON_ASSESSABLE_COMPLETED)"
          + " and (status != STATUS_ASSESSABLE_FAIL)"
          + " and (status != STATUS_NON_ASSESSABLE_NOT_COMPLETED)"
          + " and (status != STATUS_ASSESSABLE_WITHDRAWN)",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Outcome and entityId" },
  { title: "Classes", list: "class", expression: "enrolments.outcomes.id" },
  { title: "Enrolments", list: "enrolment", expression: "outcomes.id" },
  { title: "Students", list: "contact", expression: "student.enrolments.outcomes.id" },
  { title: "Certificates", list: "certificate", expression: "certificateOutcomes.outcome.id" }

];

const nameCondition = (val: Outcome) => val.studentName;

const secondaryColumnCondition = rows => {
  const nationalCode = rows["module.nationalCode"];
  const courseName = rows["enrolment.courseClass.course.name"];
  const moduleTitle = rows["module.title"];

  if (nationalCode && moduleTitle) return `${nationalCode} ${moduleTitle}`;
  if (nationalCode && courseName) return `${nationalCode} ${courseName}`;
  if (nationalCode) return `${nationalCode}`;
  if (courseName) return `${courseName}`;
  if (moduleTitle) return `${moduleTitle}`;

  return "Non VET";
};

const manualLink = getManualLink("delivery_outcomes");

const Outcomes: React.FC<OutcomesProps> = props => {
  const {
    onInit,
    getFilters,
    getTags,
    clearListState,
    checkPermissions
  } = props;

  useEffect(() => {
    getFilters();
    getTags();
    checkPermissions();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "studentName",
        secondaryColumn: "module.title",
        secondaryColumnCondition
      }}
      editViewProps={{
        nameCondition,
        manualLink,
        hideTitle: true
      }}
      EditViewContent={OutcomeEditView}
      rootEntity="Outcome"
      onInit={onInit}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      createButtonDisabled
      CogwheelAdornment={BulkEditCogwheelOption}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(OutcomeInitial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, OutcomeInitial));
  },
  getTags: () => {
    dispatch(getOutcomeTags());
  },
  getFilters: () => dispatch(getFilters("Outcome")),
  clearListState: () => dispatch(clearListState()),
  checkPermissions: () => dispatch(checkPermissions({ path: fundingUploadsPath, method: "GET" }))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Outcomes);