/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import history from "../../../constants/History";
import { getAllTags, UPDATE_TAG_REQUEST } from "../actions";
import TagsService from "../services/TagsService";

const request: EpicUtils.Request<any, { form: string, tag: Tag }> = {
  type: UPDATE_TAG_REQUEST,
  getData: ({ tag }) => TagsService.updateTag(tag.id, tag),
  retrieveData: ({ tag }) => TagsService.getTag(tag.id),
  processData: (r, s, { form }) => {
    if (s.nextLocation) {
      history.push(s.nextLocation);
    }
    
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${r.name} was successfully updated` }
      },
      initialize(form, r),
      getAllTags()
    ];
  },
  processError: (r, { tag }) => FetchErrorHandler(r, `Error. ${tag.name} was not updated`)
};

export const EpicUpdateTag: Epic<any, any> = EpicUtils.Create(request);