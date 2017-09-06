import {combineEpics} from "redux-observable";
import {EpicLogin} from "./EpicLogin";
import {EpicLogout} from "./EpicLogout";

export const EpicAuth = combineEpics(
  EpicLogin,
  EpicLogout,
);
