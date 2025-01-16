import { combineEpics } from "redux-observable";
import { EpicDeleteConcessionType } from "./EpicDeleteConcessionType";
import { EpicGetConcessionTypes } from "./EpicGetConcessionTypes";
import { EpicUpdateConcessionTypes } from "./EpicUpdateConcessionTypes";

export const EpicConcessionTypes = combineEpics(
  EpicGetConcessionTypes,
  EpicUpdateConcessionTypes,
  EpicDeleteConcessionType
);
