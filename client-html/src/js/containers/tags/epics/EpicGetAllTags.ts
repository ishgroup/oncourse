/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { GET_ALL_TAGS_FULFILLED, GET_ALL_TAGS_REQUEST } from "../actions";
import { Tag } from "@api/model";
import history from "../../../constants/History";

const request: EpicUtils.Request = {
  type: GET_ALL_TAGS_REQUEST,
  getData: () => TagsService.getTags(),
  processData: (allTags: Tag[]) => {
    if (history.location.pathname === "/tags" && allTags.length) {
      history.push(`/tags/${allTags[0].id}`);
    }

    return [
      {
        type: GET_ALL_TAGS_FULFILLED,
        payload: { allTags }
      }
    ];
  }
};

export const EpicGetAllTags: Epic<any, any> = EpicUtils.Create(request);
