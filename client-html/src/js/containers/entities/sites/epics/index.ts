/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetAdminSites } from "./EpicGetAdminSites";
import { EpicGetSite } from "./EpicGetSite";
import { EpicGetVirtualSites } from "./EpicGetVirtualSites";

export const EpicSite = combineEpics(
  EpicGetSite,
  EpicGetAdminSites,
  EpicGetVirtualSites
);