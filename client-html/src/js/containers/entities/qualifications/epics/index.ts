import { combineEpics } from "redux-observable";
import { EpicGetQualification } from "./EpicGetQualification";
import { EpicUpdateQualification } from "./EpicUpdateQualification";

export const EpicQualification = combineEpics(
  EpicGetQualification,
  EpicUpdateQualification
);
