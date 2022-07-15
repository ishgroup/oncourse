import { combineEpics } from "redux-observable";
import { EpicGetCorporatePass } from "./EpicGetCorporatePass";
import { EpicUpdateCorporatePass } from "./EpicUpdateCorporatePass";
import { EpicDeleteCorporatePass } from "./EpicDeleteCorporatePass";

export const EpicCorporatePasses = combineEpics(
  EpicGetCorporatePass,
  EpicUpdateCorporatePass,
  EpicDeleteCorporatePass
);
