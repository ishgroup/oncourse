/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { DELETE_TAG_REQUEST, getAllTags } from "../actions";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import history from "../../../constants/History";

const request: EpicUtils.Request = {
  type: DELETE_TAG_REQUEST,
  getData: payload => TagsService.remove(payload.id),
  processData: (v, s, p) => {
    p.type === "Tag" ? history.push("/tags/tagGroups") : history.push("/tags/checklists");
  
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${p.type} was successfully deleted` }
      },
      getAllTags()
    ];
  },
  processError: (r, t) => FetchErrorHandler(r, `Error. ${t.type} was not deleted`)
};

export const EpicDeleteTag: Epic<any, any> = EpicUtils.Create(request);
