import { combineEpics } from "redux-observable";
import { EpicGetQualification } from "./EpicGetQualification";
import { EpicUpdateQualification } from "./EpicUpdateQualification";
import { EpicDeleteQualification } from "./EpicDeleteQualification";
import { EpicCreateQualification } from "./EpicCreateQualification";
import { EpicGetPlainQualifications } from "./EpicGetPlainQualifications";

export const EpicQualification = combineEpics(
  EpicGetQualification,
  EpicUpdateQualification,
  EpicDeleteQualification,
  EpicCreateQualification,
  EpicGetPlainQualifications
);
