/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_EMAIL_TEMPLATES_LIST,
  getEmailTemplatesListFulfilled
} from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import history from "../../../../../constants/History";
import { CATALOG_ITEM_COLUMNS, mapListToCatalogItem } from "../../../../../common/utils/Catalog";
import { CatalogItemType } from "../../../../../model/common/Catalog";

const request: EpicUtils.Request<any, { selectFirst: boolean; keyCodeToSelect: string }> = {
  type: GET_EMAIL_TEMPLATES_LIST,
  getData: () => EntityService.getPlainRecords("EmailTemplate", CATALOG_ITEM_COLUMNS, "keyCode not is null", null, null, "id", true),
  processData: (response: DataResponse, s, p) => {
    const emailTemplates: CatalogItemType[] = response.rows.map(mapListToCatalogItem);

    emailTemplates.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

    if (p) {
      if (p.selectFirst) {
        history.push(`/automation/email-templates`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/automation/email-template/${emailTemplates.find(t => t.keyCode === p.keyCodeToSelect).id}`);
      }
    }

    return [getEmailTemplatesListFulfilled(emailTemplates)];
  },
  processError: response => FetchErrorHandler(response, "Failed to get email templates list")
};

export const EpicGetEmailTemplatesList: Epic<any, any> = EpicUtils.Create(request);
