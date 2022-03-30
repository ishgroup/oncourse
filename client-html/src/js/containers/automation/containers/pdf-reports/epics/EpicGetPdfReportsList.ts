/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import {
  GET_AUTOMATION_PDF_REPORTS_LIST,
  getAutomationPdfReportsListFulfilled
} from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CatalogItemType } from "../../../../../model/common/Catalog";
import { CATALOG_ITEM_COLUMNS, mapListToCatalogItem } from "../../../../../common/utils/Catalog";
import { Create, Request } from "../../../../../common/epics/EpicUtils";

const request: Request<any, { selectFirst: boolean; keyCodeToSelect: string }> = {
  type: GET_AUTOMATION_PDF_REPORTS_LIST,
  getData: () => EntityService.getPlainRecords("Report", CATALOG_ITEM_COLUMNS, null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const pdfReports: CatalogItemType[] = response.rows.map(mapListToCatalogItem);

    pdfReports.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/pdf-reports`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/automation/pdf-report/${pdfReports.find(t => t.keyCode === p.keyCodeToSelect).id}`);
      }
    }

    return [getAutomationPdfReportsListFulfilled(pdfReports)];
  },
  processError: response => FetchErrorHandler(response, "Failed to get PDF reports list")
};

export const EpicGetPdfReportsList: Epic<any, any> = Create(request);
