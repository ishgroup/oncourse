/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { getMenuTags } from "ish-ui";
import TagsService from "../../../tags/services/TagsService";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../tags/actions";
import { setListMenuTags } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_OUTCOME_TAGS } from "../actions";

const request: EpicUtils.Request<any, never> = {
  type: GET_OUTCOME_TAGS,
  getData: () => TagsService.getTags("Enrolment"),
  processData: tags => {
    const menuTags = getMenuTags(tags, [], null, null, "Enrolment", "enrolment");

    return [
      setListMenuTags(
        menuTags,
        [],
        []
      ),
      {
        type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
        payload: { tags, entityName: "Enrolment" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get contact tags")
};

export const EpicGetOutcomeTags: Epic<any, any> = EpicUtils.Create(request);
