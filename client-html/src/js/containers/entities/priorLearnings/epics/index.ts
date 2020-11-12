import { combineEpics } from "redux-observable";
import { EpicGetPriorLearning } from "./EpicGetPriorLearning";
import { EpicUpdatePriorLearning } from "./EpicUpdatePriorLearning";
import { EpicDeletePriorLearning } from "./EpicDeletePriorLearning";
import { EpicCreatePriorLearning } from "./EpicCreatePriorLearning";

export const EpicPriorLearning = combineEpics(
  EpicGetPriorLearning,
  EpicUpdatePriorLearning,
  EpicCreatePriorLearning,
  EpicDeletePriorLearning
);
