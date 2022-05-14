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

const mapTag = r => ({
  id: Number(r.id),
  title: r.values[0],
  installed: true,
  enabled: true,
  hideDot: true,
  hideShortDescription: true
});

const request: EpicUtils.Request = {
  type: GET_ALL_TAGS_REQUEST,
  getData: async () => {
    const tagsResponse = await EntityService.getPlainRecords("Tag", "name", "nodeType = TAG and parentTag = null", null, null, "name", true);
    const checklistsResponse = await EntityService.getPlainRecords("Tag", "name", "nodeType = CHECKLIST and parentTag = null", null, null, "name", true);

    const allTags: CatalogItemType[] = tagsResponse.rows.map(mapTag);
    const allChecklists: CatalogItemType[] = checklistsResponse.rows.map(mapTag);
    
    return { allTags, allChecklists };
  },
  processData: ({ allTags, allChecklists }) => [
      {
        type: GET_ALL_TAGS_FULFILLED,
        payload: { allTags, allChecklists }
      }
    ]
};

export const EpicGetAllTags: Epic<any, any> = EpicUtils.Create(request);
