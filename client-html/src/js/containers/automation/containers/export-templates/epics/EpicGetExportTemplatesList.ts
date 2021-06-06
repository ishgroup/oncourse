/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_EXPORT_TEMPLATES_LIST, GET_EXPORT_TEMPLATES_LIST_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CommonListItem } from "../../../../../model/common/sidebar";

const request: EpicUtils.Request<any, { selectFirst: boolean; keyCodeToSelect: string }> = {
  type: GET_EXPORT_TEMPLATES_LIST,
  getData: () =>
    EntityService.getPlainRecords("ExportTemplate", "name,keyCode,enabled", null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const exportTemplates: CommonListItem[] = response.rows.map(r => ({
      id: Number(r.id),
      name: r.values[0],
      keyCode: r.values[1],
      hasIcon: r.values[1].startsWith("ish."),
      grayOut: r.values[2] === "false"
    }));

    exportTemplates.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/export-templates`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/automation/export-templates/${exportTemplates.find(t => t.keyCode === p.keyCodeToSelect).id}`);
      }
    }

    return [
      {
        type: GET_EXPORT_TEMPLATES_LIST_FULFILLED,
        payload: { exportTemplates }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get export templates list")
};

export const EpicGetExportTemplatesList: Epic<any, any> = EpicUtils.Create(request);
