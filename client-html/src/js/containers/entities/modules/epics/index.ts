import { combineEpics } from "redux-observable";
import { EpicUpdateModuleItem } from "./EpicUpdateModuleItem";

export const EpicModule = combineEpics(
  EpicUpdateModuleItem
);