import { combineEpics } from "redux-observable";
import { EpicUpdatePriorLearning } from "./EpicUpdatePriorLearning";

export const EpicPriorLearning = combineEpics(
  EpicUpdatePriorLearning
);