/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Create, Request } from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { GET_ENTITY_TAGS_REQUEST, GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../actions";
import { Tag } from "@api/model";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: Request<any, { entityName: string }> = {
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

export const EpicGetEntityTags: Epic<any, any> = Create(request);
