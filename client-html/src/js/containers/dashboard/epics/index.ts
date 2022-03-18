/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetDashboardScripts } from "./EpicGetDashboardScripts";
import { EpicGetDashboardStatistics } from "./EpicGetDashboardStatistics";
import { EpicGetDashboardSearch } from "./EpicGetDashboardSearch";
import { EpicGetDashboardBlogPosts } from "./EpicGetDashboardBlogPosts";

export const EpicDashboard = combineEpics(
  EpicGetDashboardStatistics,
  EpicGetDashboardSearch,
  EpicGetDashboardBlogPosts,
  EpicGetDashboardScripts
);
