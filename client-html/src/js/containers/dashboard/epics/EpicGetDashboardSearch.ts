/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_DASHBOARD_SEARCH, GET_DASHBOARD_SEARCH_FULFILLED } from "../actions";
import DashboardService from "../services/DashboardService";
import { SearchGroup } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_DASHBOARD_SEARCH,
  getData: ({ search }) => (search ? DashboardService.getSearchResults(search) : new Promise(resolve => resolve(null))),
  processData: (results: SearchGroup[]) => {
    return [
      {
        type: GET_DASHBOARD_SEARCH_FULFILLED,
        payload: { searchResults: { updating: false, results } }
      }
    ];
  }
};

export const EpicGetDashboardSearch: Epic<any, any> = EpicUtils.Create(request);
