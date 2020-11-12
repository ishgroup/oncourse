import { combineEpics } from "redux-observable";
import { EpicGetCorporatePass } from "./EpicGetCorporatePass";
import { EpicUpdateCorporatePass } from "./EpicUpdateCorporatePass";
import { EpicCreateCorporatePass } from "./EpicCreateCorporatePass";
import { EpicDeleteCorporatePass } from "./EpicDeleteCorporatePass";

export const EpicCorporatePasses = combineEpics(
  EpicGetCorporatePass,
  EpicUpdateCorporatePass,
  EpicCreateCorporatePass,
  EpicDeleteCorporatePass
);
