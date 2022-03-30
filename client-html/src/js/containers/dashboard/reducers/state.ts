/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
 StatisticData, SearchGroup
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
  scripts: {
    name: string;
    description: string;
    id: number;
  }[];
  upgradePlanLink?: string;
  userSearch?: string;
  blogPosts?: any;
}
