/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCreateClassCost } from "./EpicCreateClassCost";
import { EpicDeleteClassCost } from "./EpicDeleteClassCost";
import { EpicGetCourseClassCosts } from "./EpicGetCourseClassCosts";
import { EpicUpdateClassCost } from "./EpicUpdateClassCost";

export const EpicCourseClassBudget = combineEpics(
  EpicGetCourseClassCosts,
  EpicCreateClassCost,
  EpicUpdateClassCost,
  EpicDeleteClassCost
);
