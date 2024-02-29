/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { SpecialTag } from "@api/model";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { EntityName } from "../../../model/entities/common";
import { GET_ENTITY_SPECIAL_TAGS_REQUEST, GET_ENTITY_SPECIAL_TAGS_REQUEST_FULFILLED } from "../actions";
import TagsService from "../services/TagsService";

const request: EpicUtils.Request<SpecialTag, EntityName> = {
  type: GET_ENTITY_SPECIAL_TAGS_REQUEST,
  getData: entityName => TagsService.getSpecialTags(entityName),
  processData: (specialTag, s, entityName) => {
    return [
      {
        type: GET_ENTITY_SPECIAL_TAGS_REQUEST_FULFILLED,
        payload: { tags: specialTag.childTags, entityName }
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

export const EpicGetEntitySpecialTags: Epic<any, any> = EpicUtils.Create(request);