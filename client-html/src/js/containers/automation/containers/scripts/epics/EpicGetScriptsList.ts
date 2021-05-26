/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_SCRIPTS_LIST, GET_SCRIPTS_LIST_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import { CommonListItem } from "../../../../../model/common/sidebar";
import history from "../../../../../constants/History";

const request: EpicUtils.Request<any,{ nameToSelect: string; selectFirst: boolean }> = {
  type: GET_SCRIPTS_LIST,
  getData: () => EntityService.getPlainRecords("Script", "name,enabled,keyCode", null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const scripts: CommonListItem[] = response.rows.map(r => ({
      id: Number(r.id),
      name: r.values[0],
      grayOut: r.values[1] === "false",
      keyCode: r.values[2],
      hasIcon: r.values[2] && r.values[2].startsWith("ish.")
    }));

    scripts.sort((a, b) => (a.name[0].toLowerCase() > b.name[0].toLowerCase() ? 1 : -1));

    if (p) {
      if (p.nameToSelect) {
        history.push(`/automation/script/${scripts.find(s => s.name === p.nameToSelect).id}`);
      }
      if (p.selectFirst) {
        history.push(`/automation/script`);
      }
    }

    return [
      {
        type: GET_SCRIPTS_LIST_FULFILLED,
        payload: { scripts }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get scripts")
};

export const EpicGetScriptsList: Epic<any, any> = EpicUtils.Create(request);
