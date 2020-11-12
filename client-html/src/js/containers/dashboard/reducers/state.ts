/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
 StatisticData, CategoryItem, SearchGroup, Script
} from "@api/model";

export interface DashboardState {
  statistics: {
    updating: boolean;
    data: StatisticData;
  };
  searchResults: {
    updating: boolean;
    results: SearchGroup[];
  };
  categories: CategoryItem[];
  scripts: Script[];
  favoriteScripts: number[];
  upgradePlanLink?: string;
  userSearch?: string;
  blogPosts?: any;
}
