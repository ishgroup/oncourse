/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCheckSiteName } from "./EpicCheckSiteName";
import { EpicCreateCollege } from "./EpicCreateCollege";
import { EpicGetSites } from "./EpicGetSites";
import { EpicGetCollegeKey } from "./EpicGetCollegeKey";
import { EpicUpdateCollegeSites } from "./EpicUpdateCollegeSites";

export const EpicRoot = combineEpics(
  EpicUpdateCollegeSites,
  EpicCheckSiteName,
  EpicCreateCollege,
  EpicGetSites,
  EpicGetCollegeKey
);
