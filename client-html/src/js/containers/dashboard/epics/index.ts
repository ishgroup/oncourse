/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetDashboardScripts } from "./EpicGetDashboardScripts";
import { EpicGetDashboardStatistics } from "./EpicGetDashboardStatistics";
import { EpicGetDashboardCategories } from "./EpicGetDashboardCategories";
import { EpicGetDashboardSearch } from "./EpicGetDashboardSearch";
import { EpicGetFavoriteScripts } from "./EpicGetFavoriteScripts";
import { EpicSetDashboardFavorites } from "./EpicSetDashboardFavorites";
import { EpicGetDashboardBlogPosts } from "./EpicGetDashboardBlogPosts";
import { EpicSetFavoriteScripts } from "./EpicSetFavoriteScripts";

export const EpicDashboard = combineEpics(
  EpicGetDashboardStatistics,
  EpicGetDashboardCategories,
  EpicGetDashboardSearch,
  EpicSetDashboardFavorites,
  EpicGetDashboardBlogPosts,
  EpicGetDashboardScripts,
  EpicSetFavoriteScripts,
  EpicGetFavoriteScripts
);
