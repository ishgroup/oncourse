/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { Tag } from "@api/model";
import { CREATE_TAG_REQUEST, CREATE_TAG_REQUEST_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: CREATE_TAG_REQUEST,
  getData: payload => TagsService.create(payload.tag),
  retrieveData: () => TagsService.getTags(),
  processData: (allTags: Tag[]) => {
    return [
      {
        type: CREATE_TAG_REQUEST_FULFILLED,
        payload: { allTags }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tag was successfully created" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Tag was not created");
  }
};

export const EpicCreateTag: Epic<any, any> = EpicUtils.Create(request);
