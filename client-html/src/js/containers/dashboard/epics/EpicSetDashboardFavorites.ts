/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Category } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { SET_DASHBOARD_FAVORITES, SET_DASHBOARD_FAVORITES_FULFILLED } from "../actions";
import UserPreferenceService from "../../../common/services/UserPreferenceService";

const request: EpicUtils.Request = {
  type: SET_DASHBOARD_FAVORITES,
  getData: (categories: Category[]) => UserPreferenceService.setFavoriteCategories(categories),
  processData: () => [
      {
        type: SET_DASHBOARD_FAVORITES_FULFILLED
      }
    ]
};

export const EpicSetDashboardFavorites: Epic<any, any> = EpicUtils.Create(request);
