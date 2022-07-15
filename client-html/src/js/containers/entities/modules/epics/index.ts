import { combineEpics } from "redux-observable";
import { EpicGetModule } from "./EpicGetModule";
import { EpicUpdateModuleItem } from "./EpicUpdateModuleItem";
import { EpicDeleteModule } from "./EpicDeleteModule";

export const EpicModule = combineEpics(
  EpicGetModule,
  EpicUpdateModuleItem,
  EpicDeleteModule
);
