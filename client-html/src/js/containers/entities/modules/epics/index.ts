import { combineEpics } from "redux-observable";
import { EpicGetModule } from "./EpicGetModule";
import { EpicUpdateModuleItem } from "./EpicUpdateModuleItem";

export const EpicModule = combineEpics(
  EpicGetModule,
  EpicUpdateModuleItem
);