import {combineEpics} from "redux-observable";
import {EpicGetHistory} from "./EpicGetHistory";

export const EpicHistory = combineEpics(
  EpicGetHistory,
);
