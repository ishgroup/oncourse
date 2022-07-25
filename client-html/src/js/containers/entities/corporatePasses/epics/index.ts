import { combineEpics } from "redux-observable";
import { EpicUpdateCorporatePass } from "./EpicUpdateCorporatePass";

export const EpicCorporatePasses = combineEpics(
  EpicUpdateCorporatePass
);