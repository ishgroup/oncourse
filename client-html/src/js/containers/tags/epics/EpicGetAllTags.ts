/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Tag } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_ALL_TAGS_FULFILLED, GET_ALL_TAGS_REQUEST } from "../actions";
import { CatalogItemType } from "../../../model/common/Catalog";
import EntityService from "../../../common/services/EntityService";

const request: EpicUtils.Request = {
  type: GET_ALL_TAGS_REQUEST,
  getData: () => EntityService.getPlainRecords("Tag", "name", null, null, null, "name", true),
  processData: response => {
    const allTags: CatalogItemType[] = response.rows.map(r => ({
      id: Number(r.id),
      title: r.values[0],
      installed: true,
      enabled: true,
      hideDot: true,
      hideShortDescription: true
    }));

    return [
      {
        type: GET_ALL_TAGS_FULFILLED,
        payload: { allTags }
      }
    ];
  }
};

export const EpicGetAllTags: Epic<any, any> = EpicUtils.Create(request);
