/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_ENROLMENT_INVOICE_LINES, SET_ENROLMENT_INVOICE_LINES } from "../actions";

const defaultCourseMap = ({ id, values }) => ({
  id: Number(id),
  invoiceNumber: values[0],
  finalPriceToPayIncTax: values[1],
  contactName: (values[4] === "true" || (values[2] === values[3])) ? values[2] : `${values[3]} ${values[2]}`
});

const request: EpicUtils.Request<any, string> = {
  type: GET_ENROLMENT_INVOICE_LINES,
  hideLoadIndicator: true,
  getData: id =>
    EntityService.getPlainRecords(
      "InvoiceLine",
      "invoice.invoiceNumber,finalPriceToPayIncTax,invoice.contact.lastName,invoice.contact.firstName,invoice.contact.isCompany",
      `enrolment.id=${id}`,
      0,
      0,
      "",
      true
    ),
  processData: (response: any) => {
    const invoiceLines = response.rows.map(defaultCourseMap);

    return [
      {
        type: SET_ENROLMENT_INVOICE_LINES,
        payload: { invoiceLines }
      }
    ];
  }
};

export const EpicGetEnrolmentInvoiceLine: Epic<any, any> = EpicUtils.Create(request);
