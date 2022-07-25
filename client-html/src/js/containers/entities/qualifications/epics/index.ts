import { combineEpics } from "redux-observable";
import { EpicUpdateQualification } from "./EpicUpdateQualification";

export const EpicQualification = combineEpics(
  EpicUpdateQualification
);