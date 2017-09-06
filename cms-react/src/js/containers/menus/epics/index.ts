import {combineEpics} from "redux-observable";
import {EpicGetMenu} from "./EpicGetMenu";

export const EpicMenu = combineEpics(
  EpicGetMenu,
);
