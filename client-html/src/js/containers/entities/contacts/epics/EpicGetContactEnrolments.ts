/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CONTACT_ENROLMENTS, GET_CONTACT_ENROLMENTS_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

export const contactEnrolmentMap = ({ values, id }) => ({
  id: Number(id),
  invoiceNumber: values[0],
  createdOn: values[1],
  uniqueCode: values[2],
  courseName: values[3],
  status: values[4]
});

const request: EpicUtils.Request<any, any, any> = {
  type: GET_CONTACT_ENROLMENTS,
  hideLoadIndicator: true,
  getData: contactId =>
    EntityService.getPlainRecords(
      "Enrolment",
      "invoiceLine.invoice.invoiceNumber,createdOn,courseClass.uniqueCode,courseClass.course.name,displayStatus",
      `student.contact.id == ${contactId}`,
      0,
      0,
      "",
      true
    ),
  processData: (response: DataResponse) => {
    const enrolments = response.rows.map(contactEnrolmentMap);

    return [
      {
        type: GET_CONTACT_ENROLMENTS_FULFILLED,
        payload: { enrolments }
      }
    ];
  }
};

export const EpicGetContactEnrolments: Epic<any, any> = EpicUtils.Create(request);
