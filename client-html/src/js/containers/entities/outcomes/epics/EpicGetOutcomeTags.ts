/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { getMenuTags } from "../../../../common/components/list-view/utils/listFiltersUtils";
import TagsService from "../../../tags/services/TagsService";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_FULFILLED } from "../../../tags/actions";
import { SET_LIST_MENU_TAGS } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_OUTCOME_TAGS } from "../actions";

const request: EpicUtils.Request<any, never> = {
  type: GET_OUTCOME_TAGS,
  getData: () => TagsService.getTags("Enrolment"),
  processData: tags => {
    const menuTags = getMenuTags(tags, [], null, null, "Enrolment", "enrolment");

    return [
      {
        type: GET_LIST_TAGS_FULFILLED
      },
      {
        type: SET_LIST_MENU_TAGS,
        payload: { menuTags }
      },
      {
        type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
        payload: { tags, entityName: "Enrolment" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get contact tags")
};

export const EpicGetOutcomeTags: Epic<any, any> = EpicUtils.Create(request);
