/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from '@api/model';
import { Epic } from 'redux-observable';
import FetchErrorHandler from '../../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../../common/epics/EpicUtils';
import EntityService from '../../../../../common/services/EntityService';
import { CATALOG_ITEM_COLUMNS, mapListToCatalogItem } from '../../../../../common/utils/Catalog';
import history from '../../../../../constants/History';
import { CatalogItemType } from '../../../../../model/common/Catalog';
import { GET_EXPORT_TEMPLATES_LIST, getExportTemplatesListFulfilled } from '../actions';

const request: EpicUtils.Request<any, { selectFirst: boolean; keyCodeToSelect: string }> = {
  type: GET_EXPORT_TEMPLATES_LIST,
  getData: () =>
    EntityService.getPlainRecords("ExportTemplate", CATALOG_ITEM_COLUMNS, 'automationStatus != Hidden', null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const exportTemplates: CatalogItemType[] = response.rows.map(mapListToCatalogItem);

    exportTemplates.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/export-templates`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/automation/export-template/${exportTemplates.find(t => t.keyCode === p.keyCodeToSelect).id}`);
      }
    }

    return [getExportTemplatesListFulfilled(exportTemplates)];
  },
  processError: response => FetchErrorHandler(response, "Failed to get export templates list")
};

export const EpicGetExportTemplatesList: Epic<any, any> = EpicUtils.Create(request);
