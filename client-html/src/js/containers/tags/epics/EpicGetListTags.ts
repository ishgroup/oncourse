/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Tag } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_FULFILLED, GET_LIST_TAGS_REQUEST } from "../actions";
import { SET_LIST_MENU_TAGS } from "../../../common/components/list-view/actions/index";
import { getMenuTags } from "../../../common/components/list-view/utils/listFiltersUtils";

const request: EpicUtils.Request<any, { entityName: string }> = {
  type: GET_LIST_TAGS_REQUEST,
  getData: payload => TagsService.getTags(payload.entityName),
  processData: (tags: Tag[], s, { entityName }) => {
    const menuTags = getMenuTags(tags, []);

    return [
      {
        type: GET_LIST_TAGS_FULFILLED
      },
      {
        type: SET_LIST_MENU_TAGS,
        payload: { menuTags }
      },
      {
        type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
        payload: { tags, entityName }
      }
    ];
  }
};

export const EpicGetListTags: Epic<any, any> = EpicUtils.Create(request);
