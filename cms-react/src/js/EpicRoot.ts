import {combineEpics} from "redux-observable";
import {EpicLogin} from "./epics/EpicLogin";

export const EpicRoot = combineEpics(
  EpicLogin,
);
