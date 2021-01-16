/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CONTACT_CERTIFICATES, GET_CONTACT_CERTIFICATES_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

export const contactCertificatesMap = ({ values, id }) => {
  const fullQualification = values[0] === "true";
  const qualificationLevel = values[6];
  const qualificationName = qualificationLevel ? `${qualificationLevel} ${values[2]}` : values[2];

  return {
    id: Number(id),
    fullQualification,
    nationalCode: values[1],
    qualificationName,
    certificateNumber: values[3],
    createdOn: values[4],
    lastPrintedOn: values[5]
  };
};

const request: EpicUtils.Request<any, any, any> = {
  type: GET_CONTACT_CERTIFICATES,
  hideLoadIndicator: true,
  getData: contactId => EntityService.getPlainRecords(
      "Certificate",
      "isQualification,qualification.nationalCode,qualification.title,certificateNumber,createdOn,printedOn,qualification.level",
      `student.contact.id == ${contactId}`,
      0,
      0,
      "",
      true
    ),
  processData: (response: DataResponse) => {
    const certificates = response.rows.map(contactCertificatesMap);

    return [
      {
        type: GET_CONTACT_CERTIFICATES_FULFILLED,
        payload: { certificates }
      }
    ];
  }
};

export const EpicGetContactCertificates: Epic<any, any> = EpicUtils.Create(request);
