/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_INVOICE_LINE_ENROLMENTS, SET_INVOICE_LINE_ENROLMENTS } from "../actions";
import { DataResponse } from "@api/model";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request<any, number> = {
  type: GET_INVOICE_LINE_ENROLMENTS,
  hideLoadIndicator: true,
  getData: courseClassId =>
    EntityService.getPlainRecords(
      "Enrolment",
      "student.contact.lastName,student.contact.firstName,status",
      `courseClass.id == ${courseClassId}`,
      0,
      0,
      "",
      true
    ),
  processData: (response: DataResponse) => {
    const selectedLineEnrolments = response.rows.map(({ id, values }) => ({
      id: Number(id),
      label: `${values[0]}, ${values[1]} (${values[2]})`
    }));

    return [
      {
        type: SET_INVOICE_LINE_ENROLMENTS,
        payload: { selectedLineEnrolments }
      }
    ];
  }
};

export const EpicGetInvoiceLineEnrolments: Epic<any, any> = EpicUtils.Create(request);
