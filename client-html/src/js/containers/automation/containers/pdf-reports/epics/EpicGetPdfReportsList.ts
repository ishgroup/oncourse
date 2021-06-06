/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_AUTOMATION_PDF_REPORTS_LIST, GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED } from "../actions/";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CommonListItem } from "../../../../../model/common/sidebar";

const request: EpicUtils.Request<any, { selectFirst: boolean; keyCodeToSelect: string }> = {
  type: GET_AUTOMATION_PDF_REPORTS_LIST,
  getData: () => EntityService.getPlainRecords("Report", "name,keyCode,enabled", null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const pdfReports: CommonListItem[] = response.rows.map(r => ({
      id: Number(r.id),
      name: r.values[0],
      keyCode: r.values[1],
      hasIcon: r.values[1].startsWith("ish."),
      grayOut: r.values[2] === "false"
    }));

    pdfReports.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/pdf-reports`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/automation/pdf-reports/${pdfReports.find(t => t.keyCode === p.keyCodeToSelect).id}`);
      }
    }

    return [
      {
        type: GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED,
        payload: { pdfReports }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get PDF reports list")
};

export const EpicGetPdfReportsList: Epic<any, any> = EpicUtils.Create(request);
