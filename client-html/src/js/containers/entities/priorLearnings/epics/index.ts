import { combineEpics } from "redux-observable";
import { EpicGetPriorLearning } from "./EpicGetPriorLearning";
import { EpicUpdatePriorLearning } from "./EpicUpdatePriorLearning";
import { EpicDeletePriorLearning } from "./EpicDeletePriorLearning";

export const EpicPriorLearning = combineEpics(
  EpicGetPriorLearning,
  EpicUpdatePriorLearning,
  EpicDeletePriorLearning
);
