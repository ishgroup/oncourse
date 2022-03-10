/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_SCRIPTS_LIST, getScriptsListFulfilled } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CatalogItemType } from "../../../../../model/common/Catalog";
import { CATALOG_ITEM_COLUMNS, mapListToCatalogItem } from "../../../../../common/utils/Catalog";

const request: EpicUtils.Request<any, { nameToSelect: string; selectFirst: boolean }> = {
  type: GET_SCRIPTS_LIST,
  getData: () => EntityService.getPlainRecords("Script", CATALOG_ITEM_COLUMNS, null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const scripts: CatalogItemType[] = response.rows.map(mapListToCatalogItem);

    scripts.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.nameToSelect) {
        history.push(`/automation/script/${scripts.find(s => s.title === p.nameToSelect).id}`);
      }
      if (p.selectFirst) {
        history.push(`/automation/scripts`);
      }
    }

    return [getScriptsListFulfilled(scripts)];
  },
  processError: response => FetchErrorHandler(response, "Failed to get scripts")
};

export const EpicGetScriptsList: Epic<any, any> = EpicUtils.Create(request);
