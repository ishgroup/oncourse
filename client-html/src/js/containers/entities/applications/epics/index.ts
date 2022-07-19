import { combineEpics } from "redux-observable";
import { EpicGetApplication } from "./EpicGetApplication";
import { EpicUpdateApplicationItem } from "./EpicUpdateApplicationItem";

export const EpicApplication = combineEpics(
  EpicGetApplication,
  EpicUpdateApplicationItem
);
