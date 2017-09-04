import {combineEpics} from "redux-observable";
import {EpicLogin} from "./epics/EpicLogin";
import {EpicLogout} from "./epics/EpicLogout";
import {EpicGetMenu} from "./containers/menus/epics/EpicGetMenu";

export const EpicRoot = combineEpics(
  EpicLogin,
  EpicLogout,
  EpicGetMenu,
);
