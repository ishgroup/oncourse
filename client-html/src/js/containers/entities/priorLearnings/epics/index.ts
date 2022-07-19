import { combineEpics } from "redux-observable";
import { EpicGetPriorLearning } from "./EpicGetPriorLearning";
import { EpicUpdatePriorLearning } from "./EpicUpdatePriorLearning";

export const EpicPriorLearning = combineEpics(
  EpicGetPriorLearning,
  EpicUpdatePriorLearning
);