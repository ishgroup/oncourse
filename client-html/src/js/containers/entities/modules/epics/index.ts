import { combineEpics } from "redux-observable";
import { EpicGetModule } from "./EpicGetModule";
import { EpicUpdateModuleItem } from "./EpicUpdateModuleItem";
import { EpicCreateModule } from "./EpicCreateModule";
import { EpicDeleteModule } from "./EpicDeleteModule";
import { EpicGetModules } from "./EpicGetModules";

export const EpicModule = combineEpics(
  EpicGetModule,
  EpicUpdateModuleItem,
  EpicCreateModule,
  EpicDeleteModule,
  EpicGetModules
);
