/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import EntityService from "../../../common/services/EntityService";
import history from "../../../constants/History";
import { CatalogItemType } from "../../../model/common/Catalog";
import { checkSpecialTypesAsync } from "../../preferences/utils";
import { GET_ALL_TAGS_FULFILLED, GET_ALL_TAGS_REQUEST } from "../actions";
import { plainTagToCatalogItem } from "../utils";

const request: EpicUtils.Request = {
  type: GET_ALL_TAGS_REQUEST,
  getData: async ({ nameToSelect }) => {
    const extendedSearchTypes = await checkSpecialTypesAsync();

    const tagsResponse = await EntityService.getPlainRecords(
      "Tag",
      "name",
      `nodeType = TAG and parentTag = null${extendedSearchTypes ? " and (specialType = null OR (specialType != CLASS_EXTENDED_TYPES and specialType != COURSE_EXTENDED_TYPES and specialType != SUBJECTS))" : ""}`,
      null,
      null,
      "name",
      true
    );
    const checklistsResponse = await EntityService.getPlainRecords("Tag", "name", "nodeType = CHECKLIST and parentTag = null", null, null, "name", true);

    const allTags: CatalogItemType[] = tagsResponse.rows.map(plainTagToCatalogItem);
    const allChecklists: CatalogItemType[] = checklistsResponse.rows.map(plainTagToCatalogItem);
    
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