/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GET_ON_DEMAND_SCRIPTS_FULFILLED } from "../../../common/actions";
import { IAction } from "../../../common/actions/IshAction";
import {
  GET_BLOG_POSTS_FULFILLED,
  GET_DASHBOARD_SEARCH,
  GET_DASHBOARD_SEARCH_FULFILLED,
  GET_DASHBOARD_STATISTIC,
  GET_DASHBOARD_STATISTIC_FULFILLED
} from "../actions";
import { DashboardState } from "./state";

class DashboardStateClass implements DashboardState {
  statistics = {
    updating: false,
    data: null
  };

  searchResults = {
    updating: false,
    results: []
  };

  categories = [];

  scripts = [];

  blogPosts = [];
}

export const dashboardReducer = (state: DashboardState = new DashboardStateClass(), action: IAction<any>): any => {
  switch (action.type) {
    case GET_DASHBOARD_SEARCH_FULFILLED:
    case GET_DASHBOARD_STATISTIC_FULFILLED:
    case GET_ON_DEMAND_SCRIPTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_DASHBOARD_SEARCH: {
      return {
        ...state,
        userSearch: action.payload.search,
        searchResults: {
          updating: true,
          results: state.searchResults.results
        }
      };
    }

    case GET_DASHBOARD_STATISTIC: {
      return {
        ...state,
        statistics: {
          updating: true,
          data: state.statistics.data
        }
      };
    }

    case GET_BLOG_POSTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
