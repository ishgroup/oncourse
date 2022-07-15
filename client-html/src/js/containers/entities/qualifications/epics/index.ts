import { combineEpics } from "redux-observable";
import { EpicGetQualification } from "./EpicGetQualification";
import { EpicUpdateQualification } from "./EpicUpdateQualification";
import { EpicDeleteQualification } from "./EpicDeleteQualification";

export const EpicQualification = combineEpics(
  EpicGetQualification,
  EpicUpdateQualification,
  EpicDeleteQualification
);
