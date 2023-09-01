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
import { CREATE_TAG_REQUEST, getAllTags } from "../actions";
import { TAGS_FORM_NAME } from "../constants";
import TagsService from "../services/TagsService";

const request: EpicUtils.Request<Tag[], Tag> = {
  type: CREATE_TAG_REQUEST,
  getData: tag => TagsService.create(tag),
  processData: (r, s, tag) => {
    if (s.nextLocation) {
      history.push(s.nextLocation);
    }
    return [
      initialize(TAGS_FORM_NAME, tag),
      {
        type: FETCH_SUCCESS,
        payload: { message: `${tag.type} was successfully created` }
      },
      getAllTags(tag.name)
    ];
  },
  processError: (r, t) => FetchErrorHandler(r, `Error. ${t.type} was not created`)
};

export const EpicCreateTag: Epic<any, any> = EpicUtils.Create(request);
