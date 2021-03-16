/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_DASHBOARD_CATEGORIES, GET_DASHBOARD_CATEGORIES_FULFILLED } from "../actions";
import { DashboardLinks } from "@api/model";
import { getMainRouteUrl } from "../../../routes/routesMapping";
import UserPreferenceService from "../../../common/services/UserPreferenceService";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_DASHBOARD_CATEGORIES,
  getData: () => UserPreferenceService.getCategories(),
  processData: (links: DashboardLinks) => {
    // map react categories routes
    links.categories.forEach(c => {
      if (!c.url) {
        c.url = getMainRouteUrl(c.category);
      }
    });

    // Removed checkout from dashboard category
    //links.categories = links.categories.filter(c => c.category !== "Checkout");

    return [
      {
        type: GET_DASHBOARD_CATEGORIES_FULFILLED,
        payload: links
      }
    ];
  },
  processError: response => FetchErrorHandler(response)
};

export const EpicGetDashboardCategories: Epic<any, any> = EpicUtils.Create(request);
