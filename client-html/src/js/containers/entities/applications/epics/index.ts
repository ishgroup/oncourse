import { combineEpics } from "redux-observable";
import { EpicGetApplication } from "./EpicGetApplication";
import { EpicUpdateApplicationItem } from "./EpicUpdateApplicationItem";
import { EpicDeleteApplication } from "./EpicDeleteApplication";

export const EpicApplication = combineEpics(
  EpicGetApplication,
  EpicUpdateApplicationItem,
  EpicDeleteApplication
);
