/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_ENTITY_TAGS_REQUEST, GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../actions";
import TagsService from "../services/TagsService";

const request: EpicUtils.Request<any, { entityName: string }> = {
  type: GET_ENTITY_TAGS_REQUEST,
  getData: payload => TagsService.getTags(payload.entityName),
  processData: (tags: Tag[], s, { entityName }) => {
    return [
      {
        type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
        payload: { tags, entityName }
      }
    ];
  },
  processError: response => {
    if (response && response.status === 403) {
      return [];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetEntityTags: Epic<any, any> = EpicUtils.Create(request);
