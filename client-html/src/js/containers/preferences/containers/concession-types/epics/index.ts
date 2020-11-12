import { combineEpics } from "redux-observable";
import { EpicGetConcessionTypes } from "./EpicGetConcessionTypes";
import { EpicUpdateConcessionTypes } from "./EpicUpdateConcessionTypes";
import { EpicDeleteConcessionType } from "./EpicDeleteConcessionType";

export const EpicConcessionTypes = combineEpics(
  EpicGetConcessionTypes,
  EpicUpdateConcessionTypes,
  EpicDeleteConcessionType
);
