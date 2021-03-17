/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetSite } from "./EpicGetSite";
import { EpicUpdateSite } from "./EpicUpdateSite";
import { EpicCreateSite } from "./EpicCreateSite";
import { EpicDeleteSite } from "./EpicDeleteSite";
import { EpicGetAdminSites } from "./EpicGetAdminSites";
import { EpicGetVirtualSites } from "./EpicGetVirtualSites";

export const EpicSite = combineEpics(
  EpicGetSite,
  EpicUpdateSite,
  EpicCreateSite,
  EpicDeleteSite,
  EpicGetAdminSites,
  EpicGetVirtualSites
);
