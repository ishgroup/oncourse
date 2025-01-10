import { combineEpics } from "redux-observable";
import { EpicBulkDeleteEntityRecords } from "./EpicBulkDeleteEntityRecords";
import { EpicCreateEntityRecord } from "./EpicCreateEntityRecord";
import { EpicDeleteEntityRecord } from "./EpicDeleteEntityRecord";
import { EpicGetEntityRecord } from "./EpicGetEntityRecord";
import { EpicUpdateEntityRecord } from "./EpicUpdateEntityRecord";

export const EpicListEntityRecord = combineEpics(
  EpicCreateEntityRecord,
  EpicDeleteEntityRecord,
  EpicUpdateEntityRecord,
  EpicGetEntityRecord,
  EpicBulkDeleteEntityRecords
);