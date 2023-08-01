/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { getMenuTags } from "ish-ui";
import { Epic } from "redux-observable";
import { setListMenuTags } from "../../../common/components/list-view/actions";
import { Create, Request } from "../../../common/epics/EpicUtils";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_REQUEST } from "../actions";
import TagsService from "../services/TagsService";

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
