import {combineEpics} from "redux-observable";
import {EpicLogin, EpicLoggedIn} from "./EpicLogin";
import {EpicLogout} from "./EpicLogout";
import {EpicGetUser} from "./EpicGetUser";

export const EpicAuth = combineEpics(
  EpicLogin,
  EpicLoggedIn,
  EpicGetUser,
  EpicLogout,
);
