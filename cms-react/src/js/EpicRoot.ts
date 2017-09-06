import {combineEpics} from "redux-observable";
import {EpicAuth} from "./containers/auth/epics";
import {EpicMenu} from "./containers/menus/epics";

export const EpicRoot = combineEpics(
  EpicAuth,
  EpicMenu,
);
