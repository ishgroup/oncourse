/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { getAllTags, UPDATE_TAG_REQUEST } from "../actions";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: UPDATE_TAG_REQUEST,
  getData: payload => TagsService.updateTag(payload.id, payload.tag),
  processData: (r, s, p) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${p.tag.type} was successfully updated` }
      },
      getAllTags()
    ],
  processError: (r, t) => FetchErrorHandler(r, `Error. ${t.type} was not updated`)
};

export const EpicUpdateTag: Epic<any, any> = EpicUtils.Create(request);
