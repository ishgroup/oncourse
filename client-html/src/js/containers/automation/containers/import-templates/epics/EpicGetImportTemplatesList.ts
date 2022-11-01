/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_IMPORT_TEMPLATES_LIST,
  getImportTemplatesListFulfilled
} from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CatalogItemType } from "../../../../../model/common/Catalog";
import { CATALOG_ITEM_COLUMNS, mapListToCatalogItem } from "../../../../../common/utils/Catalog";

const request: EpicUtils.Request<any, { selectFirst: boolean; keyCodeToSelect: string }> = {
  type: GET_IMPORT_TEMPLATES_LIST,
  getData: () =>
    EntityService.getPlainRecords("Import", CATALOG_ITEM_COLUMNS, null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const importTemplates: CatalogItemType[] = response.rows.map(mapListToCatalogItem);

    importTemplates.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/import-templates`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/automation/import-template/${importTemplates.find(t => t.keyCode === p.keyCodeToSelect).id}`);
      }
    }

    return [getImportTemplatesListFulfilled(importTemplates)];
  },
  processError: response => FetchErrorHandler(response, "Failed to get import templates list")
};

export const EpicGetImportTemplatesList: Epic<any, any> = EpicUtils.Create(request);
