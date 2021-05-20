/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import CustomFiltersService from "../../../services/CustomFiltersService";
import { FETCH_SUCCESS } from "../../../actions";
import { DELETE_FILTER_REQUEST, GET_FILTERS_REQUEST } from "../actions";

const request: EpicUtils.Request = {
  type: DELETE_FILTER_REQUEST,
  getData: payload => CustomFiltersService.deleteCustomFilter(payload.entity, payload.id),
  processData: (data, state, payload) => [
      {
        type: GET_FILTERS_REQUEST,
        payload: { entity: payload.entity, listUpdate: payload.checked }
      },
      {
        type: FETCH_SUCCESS,
        payload: {
          message: "Custom filter successfully deleted"
        }
      }
    ]
};

export const EpicDeleteCustomFilter: Epic<any, any> = EpicUtils.Create(request);
