/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { Tag } from "@api/model";
import { UPDATE_TAG_REQUEST, UPDATE_TAG_REQUEST_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: UPDATE_TAG_REQUEST,
  getData: payload => TagsService.updateTag(payload.id, payload.tag),
  retrieveData: () => TagsService.getTags(),
  processData: (allTags: Tag[]) => {
    return [
      {
        type: UPDATE_TAG_REQUEST_FULFILLED,
        payload: { allTags }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tag was successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Tag was not updated");
  }
};

export const EpicUpdateTag: Epic<any, any> = EpicUtils.Create(request);
