import {combineEpics} from "redux-observable";
import {EpicGetMenu} from "./EpicGetMenu";
import {EpicSaveMenu} from "./EpicSaveMenu";

export const EpicMenu = combineEpics(
  EpicGetMenu,
  EpicSaveMenu,
);
