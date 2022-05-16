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
import history from "../../../constants/History";

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
  getData: async ({ nameToSelect }) => {
    const tagsResponse = await EntityService.getPlainRecords("Tag", "name", "nodeType = TAG and parentTag = null", null, null, "name", true);
    const checklistsResponse = await EntityService.getPlainRecords("Tag", "name", "nodeType = CHECKLIST and parentTag = null", null, null, "name", true);

    const allTags: CatalogItemType[] = tagsResponse.rows.map(mapTag);
    const allChecklists: CatalogItemType[] = checklistsResponse.rows.map(mapTag);
    
    return { allTags, allChecklists, nameToSelect };
  },
  processData: ({ allTags, allChecklists, nameToSelect }) => {

    if (nameToSelect) {
      const tag = allTags.find(t => t.title === nameToSelect);
      if (tag) {
        history.push(`/tags/tagGroup/${tag.id}`);
      } else {
        const checklist = allChecklists.find(t => t.title === nameToSelect);
        if (checklist) {
          history.push(`/tags/checklist/${checklist.id}`);
        }
      }
    }
    
    return [
      {
        type: GET_ALL_TAGS_FULFILLED,
        payload: { allTags, allChecklists }
      }
    ];
  }
};

export const EpicGetAllTags: Epic<any, any> = EpicUtils.Create(request);
