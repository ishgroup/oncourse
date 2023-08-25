/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import history from "../../../constants/History";
import { getAllTags, UPDATE_TAG_REQUEST } from "../actions";
import { TAGS_FORM_NAME } from "../constants";
import TagsService from "../services/TagsService";

const request: EpicUtils.Request = {
  type: UPDATE_TAG_REQUEST,
  getData: payload => TagsService.updateTag(payload.id, payload.tag),
  retrieveData: payload => TagsService.getTag(payload.id),
  processData: (r, s) => {
    if (s.nextLocation) {
      history.push(s.nextLocation);
    }
    
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${r.type} was successfully updated` }
      },
      initialize(TAGS_FORM_NAME, r),
      getAllTags()
    ];
  },
  processError: (r, t) => FetchErrorHandler(r, `Error. ${t.type} was not updated`)
};

export const EpicUpdateTag: Epic<any, any> = EpicUtils.Create(request);
