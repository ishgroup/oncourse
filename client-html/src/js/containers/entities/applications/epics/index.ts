import { combineEpics } from "redux-observable";
import { EpicUpdateApplicationItem } from "./EpicUpdateApplicationItem";

export const EpicApplication = combineEpics(
  EpicUpdateApplicationItem
);
