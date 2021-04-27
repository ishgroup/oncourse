/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import { GET_RECORDS_FULFILLED, SET_LIST_SEARCH, setListSearchError } from "../actions/index";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, { search: string; entity: string }> = {
  type: SET_LIST_SEARCH,
  getData: ({ entity }, state) => EntityService.getList({ entity }, state),
  processData: ([records, searchQuery], state, payload) => [
    {
      type: GET_RECORDS_FULFILLED,
      payload: { records, payload, searchQuery }
    },
    ...(state.list.searchError ? [setListSearchError(false)] : [])
  ],
  processError: response => {
    if (response && response.status === 400 && response.data.errorMessage.includes("Invalid search expression")) {
      return [setListSearchError(true)];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetSearchResults: Epic<any, any> = EpicUtils.Create(request);
