/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Request, Create } from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_REQUEST } from "../actions";
import { setListMenuTags } from "../../../common/components/list-view/actions";
import { getMenuTags } from "ish-ui";

const request: Request<any, { entityName: string }> = {
  type: GET_LIST_TAGS_REQUEST,
  getData: async payload => {
    const tags = await TagsService.getTags(payload.entityName);
    const checklists = await TagsService.getChecklists(payload.entityName);
    return { tags, checklists };
  },
  processData: ({ tags, checklists }, s, { entityName }) => {
    const menuTags = getMenuTags(tags, []);
    const checkedChecklists = getMenuTags(checklists, []);
    const uncheckedChecklists = [...checkedChecklists];

    return [
      setListMenuTags(menuTags, checkedChecklists, uncheckedChecklists),
      {
        type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
        payload: { tags, entityName }
      }
    ];
  }
};

export const EpicGetListTags: Epic<any, any> = Create(request);
