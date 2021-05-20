/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
