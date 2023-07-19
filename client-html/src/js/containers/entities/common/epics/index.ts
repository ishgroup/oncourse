import { combineEpics } from "redux-observable";
import { EpicCreateEntityRecord } from "./EpicCreateEntityRecord";
import { EpicDeleteEntityRecord } from "./EpicDeleteEntityRecord";
import { EpicGetEntityRecord } from "./EpicGetEntityRecord";
import { EpicUpdateEntityRecord } from "./EpicUpdateEntityRecord";
import { EpicBulkDeleteEntityRecords } from "./EpicBulkDeleteEntityRecords";

export const EpicListEntityRecord = combineEpics(
  EpicCreateEntityRecord,
  EpicDeleteEntityRecord,
  EpicUpdateEntityRecord,
  EpicGetEntityRecord,
  EpicBulkDeleteEntityRecords
);