import {combineEpics} from "redux-observable";
import {EpicLogin} from "./EpicLogin";
import {EpicLogout} from "./EpicLogout";
import {EpicGetUser} from "./EpicGetUser";

export const EpicAuth = combineEpics(
  EpicLogin,
  EpicGetUser,
  EpicLogout,
);
