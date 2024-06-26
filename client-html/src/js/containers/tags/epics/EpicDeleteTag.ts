/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import history from "../../../constants/History";
import { DELETE_TAG_REQUEST, getAllTags } from "../actions";
import TagsService from "../services/TagsService";

const request: EpicUtils.Request<any, Tag> = {
  type: DELETE_TAG_REQUEST,
  getData: payload => TagsService.remove(payload.id),
  processData: (v, s, p) => {
    p.type === "Tag" ? history.push("/tags/tagGroups") : history.push("/tags/checklists");
  
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${p.name} was successfully deleted` }
      },
      getAllTags()
    ];
  },
  processError: (r, t) => FetchErrorHandler(r, `Error. ${t.name} was not deleted`)
};

export const EpicDeleteTag: Epic<any, any> = EpicUtils.Create(request);
