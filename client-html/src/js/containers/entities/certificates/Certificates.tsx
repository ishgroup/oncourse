/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Certificate } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import {
  setListEditRecord,
  getFilters,
  clearListState
} from "../../../common/components/list-view/actions";
import { FilterGroup } from "../../../model/common/ListView";
import { defaultContactName } from "../contacts/utils";
import CertificateEditView from "./components/CertificateEditView";
import { getManualLink } from "../../../common/utils/getManualLink";
import USIAlert from "./components/USIAlert";
import RevokeCertificateCogwheel from "./components/RevokeCertificateCogwheel";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

interface CertificatesProps {
  onInit?: () => void;
  getFilters?: () => void;
  clearListState?: () => void;
}

const Initial: Certificate = {
  awardedOn: null,
  code: null,
  expiryDate: null,
  id: null,
  isQualification: false,
  issuedOn: null,
  level: null,
  nationalCode: null,
  // eslint-disable-next-line id-blacklist
  number: null,
  outcomes: [],
  printedOn: null,
  privateNotes: null,
  publicNotes: null,
  qualificationId: null,
  revokedOn: null,
  studentContactId: null,
  studentName: null,
  title: null
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Draft",
        expression: "printedOn is null and revokedOn is null",
        active: false
      },
      {
        name: "Printed",
        expression: "printedOn not is null and revokedOn is null",
        active: false
      },
      {
        name: "Revoked",
        expression: "revokedOn not is null",
        active: false
      },
    ]
  }
];

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Certificate and entityId" },
  { title: "Enrolments", list: "enrolment", expression: "outcomes.certificateOutcomes.certificate.id" },
  { title: "Students", list: "contact", expression: "student.certificates.id" },
  { title: "Outcomes", list: "outcome", expression: "certificateOutcomes.certificate.id" }

];

const manualLink = getManualLink("certification");

const nameCondition = (value: Certificate) => defaultContactName(value.studentName);

const secondaryColumnCondition = rows => rows["qualification.title"] || "No Qualification";

const Certificates: React.FC<CertificatesProps> = props => {
  const {
    onInit,
    getFilters
  } = props;

  useEffect(() => {
    getFilters();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "student.contact.fullName",
        secondaryColumn: "qualification.title",
        secondaryColumnCondition
      }}
      editViewProps={{
        manualLink,
        nameCondition,
        hideTitle: true
      }}
      EditViewContent={CertificateEditView}
      CogwheelAdornment={RevokeCertificateCogwheel}
      rootEntity="Certificate"
      onInit={onInit}
      findRelated={findRelatedGroup}
      ShareContainerAlertComponent={USIAlert}
      filterGroupsInitial={filterGroups}
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => {
    dispatch(getFilters("Certificate"));
  },
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(null, mapDispatchToProps)(Certificates);