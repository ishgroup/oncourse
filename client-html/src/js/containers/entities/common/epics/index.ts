import { combineEpics } from "redux-observable";
import { EpicCreateEntityRecord } from "./EpicCreateEntityRecord";
import { EpicDeleteEntityRecord } from "./EpicDeleteEntityRecord";
import { EpicGetEntityRecord } from "./EpicGetEntityRecord";

export const EpicListEntityRecord = combineEpics(
  EpicCreateEntityRecord,
  EpicDeleteEntityRecord,
  EpicGetEntityRecord
);

