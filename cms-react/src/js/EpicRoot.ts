import {combineEpics} from "redux-observable";
import {EpicLogin} from "./epics/EpicLogin";
import {EpicLogout} from "./epics/EpicLogout";

export const EpicRoot = combineEpics(
  EpicLogin,
  EpicLogout,
);
