/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { combineEpics } from "redux-observable";
import { EpicBulkChangeRecords } from "./EpicBulkChangeRecords";
import { EpicDeleteCustomFilter } from "./EpicDeleteCustomFilter";
import { EpicFindRelatedByFilter } from "./EpicFindRelatedByFilter";
import { EpicGetEntities } from "./EpicGetEntities";
import { EpicGetEntitiesResolve } from "./EpicGetEntitiesResolve";
import { EpicGetFilters } from "./EpicGetFilters";
import { EpicGetPlainEntities } from "./EpicGetPlainEntities";
import { EpicGetRecepientsMessageData } from "./EpicGetRecepientsMessageData";
import { EpicGetSearchResults } from "./EpicGetSearchResults";
import { EpicPostCustomFilter } from "./EpicPostCustomFilter";
import { EpicUpdateTableModel } from "./EpicUpdateTableModel";

export const EpicList = combineEpics(
  EpicFindRelatedByFilter,
  EpicGetEntitiesResolve,
  EpicDeleteCustomFilter,
  EpicGetEntities,
  EpicGetFilters,
  EpicGetSearchResults,
  EpicGetRecepientsMessageData,
  EpicPostCustomFilter,
  EpicUpdateTableModel,
  EpicGetPlainEntities,
  EpicBulkChangeRecords
);