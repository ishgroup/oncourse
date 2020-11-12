import { combineEpics } from "redux-observable";
import { EpicGetRecepientsMessageData } from "./EpicGetRecepientsMessageData";
import { EpicUpdateTableModel } from "./EpicUpdateTableModel";
import { EpicPostCustomFilter } from "./EpicPostCustomFilter";
import { EpicGetSearchResults } from "./EpicGetSearchResults";
import { EpicGetFilters } from "./EpicGetFilters";
import { EpicGetEntities } from "./EpicGetEntities";
import { EpicDeleteCustomFilter } from "./EpicDeleteCustomFilter";
import { EpicGetListNestedEditRecord } from "./EpicGetListNestedEditRecord";
import { EpicUpdateListNestedRecord } from "./EpicUpdateListNestedRecord";
import { EpicGetPlainEntities } from "./EpicGetPlainEntities";
import { EpicBulkChangeRecords } from "./EpicBulkChangeRecords";

export const EpicList = combineEpics(
  EpicDeleteCustomFilter,
  EpicGetEntities,
  EpicGetFilters,
  EpicGetSearchResults,
  EpicGetRecepientsMessageData,
  EpicPostCustomFilter,
  EpicUpdateTableModel,
  EpicGetListNestedEditRecord,
  EpicUpdateListNestedRecord,
  EpicGetPlainEntities,
  EpicBulkChangeRecords
);
