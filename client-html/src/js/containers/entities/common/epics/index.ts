import { combineEpics } from "redux-observable";
import { EpicCreateEntityRecord } from "./EpicCreateEntityRecord";

export const EpicListEntityRecord = combineEpics(
  EpicCreateEntityRecord,
);

