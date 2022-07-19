import { combineEpics } from "redux-observable";
import { EpicGetCorporatePass } from "./EpicGetCorporatePass";
import { EpicUpdateCorporatePass } from "./EpicUpdateCorporatePass";

export const EpicCorporatePasses = combineEpics(
  EpicGetCorporatePass,
  EpicUpdateCorporatePass
);
