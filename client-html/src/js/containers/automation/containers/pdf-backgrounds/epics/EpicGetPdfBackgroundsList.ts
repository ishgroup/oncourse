/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_AUTOMATION_PDF_BACKGROUNDS_LIST, GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CommonListItem } from "../../../../../model/common/sidebar";

const request: EpicUtils.Request<any, { selectFirst: boolean; filenameToSelect: string }> = {
  type: GET_AUTOMATION_PDF_BACKGROUNDS_LIST,
  getData: () => EntityService.getPlainRecords("ReportOverlay", "name,portrait", null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const pdfBackgrounds: CommonListItem[] = response.rows.map(r => ({
      id: Number(r.id),
      name: r.values[0],
      isPortrait: r.values[1] === "true",
      hasIcon: true
    }));

    pdfBackgrounds.sort((a, b) => (a.name[0].toLowerCase() > b.name[0].toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/pdf-backgrounds`);
      }
      if (p.filenameToSelect) {
        history.push(`/automation/pdf-backgrounds/${pdfBackgrounds.find(t => t.name === p.filenameToSelect).id}`);
      }
    }

    return [
      {
        type: GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED,
        payload: { pdfBackgrounds }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get PDF backgrounds list")
};

export const EpicGetPdfBackgroundsList: Epic<any, any> = EpicUtils.Create(request);
