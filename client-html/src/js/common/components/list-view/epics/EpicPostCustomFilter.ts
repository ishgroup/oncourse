/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import CustomFiltersService from "../../../services/CustomFiltersService";
import { FETCH_SUCCESS } from "../../../actions/index";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";
import {
  GET_FILTERS_REQUEST,
  POST_FILTER_REQUEST,
  POST_FILTER_REQUEST_FULFILLED,
  SET_LIST_SAVING_FILTER
} from "../actions/index";

const request: EpicUtils.Request = {
  type: POST_FILTER_REQUEST,
  getData: payload => CustomFiltersService.saveCustomFilter(payload.filter, payload.entity),
  processData: (data, state, payload) => {
    return [
      {
        type: POST_FILTER_REQUEST_FULFILLED
      },
      {
        type: GET_FILTERS_REQUEST,
        payload: { entity: payload.entity }
      },
      {
        type: SET_LIST_SAVING_FILTER,
        payload: null
      },
      {
        type: FETCH_SUCCESS,
        payload: {
          message: "Custom filter successfully created"
        }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Custom filter was not created")
};

export const EpicPostCustomFilter: Epic<any, any> = EpicUtils.Create(request);
