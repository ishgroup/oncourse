/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Report } from "@api/model";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import PdfService from "../../../../../../containers/automation/containers/pdf-reports/services/PdfService";
import { GET_OVERLAY_ITEMS, GET_PDF_REPORTS, GET_PDF_REPORTS_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_PDF_REPORTS,
  getData: payload => PdfService.getAllPrints(payload),
  processData: (pdfReports: Report[]) => {
    pdfReports.forEach(r => {
      r.variables.forEach(v => {
        if (v.type === "Checkbox" && v.value === null) {
          v.value = "true";
        } else if (v.value === null && v.valueDefault) {
          v.value = v.valueDefault;
        }
      });
    });

    return [
      {
        type: GET_PDF_REPORTS_FULFILLED,
        payload: { pdfReports }
      },
      {
        type: GET_OVERLAY_ITEMS
      }
    ];
  },
  processError: response => {
    if (response && response.status === 403) {
      return [];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetShareList: Epic<any, any> = EpicUtils.Create(request);
