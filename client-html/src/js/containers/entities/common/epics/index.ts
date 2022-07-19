import { combineEpics } from "redux-observable";
import { EpicCreateEntityRecord } from "./EpicCreateEntityRecord";
import { EpicDeleteEntityRecord } from "./EpicDeleteEntityRecord";

export const EpicListEntityRecord = combineEpics(
  EpicCreateEntityRecord,
  EpicDeleteEntityRecord
);

