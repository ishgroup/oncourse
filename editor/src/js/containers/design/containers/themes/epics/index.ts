import {combineEpics} from "redux-observable";
import {EpicGetThemes} from "./EpicGetThemes";
import {EpicSaveTheme} from "./EpicSaveTheme";
import {EpicDeleteTheme} from "./EpicDeleteTheme";
import {EpicAddTheme} from "./EpicAddTheme";
import {EpicGetLayouts} from "./EpicGetLayouts";

export const EpicThemes = combineEpics(
  EpicGetThemes,
  EpicGetLayouts,
  EpicSaveTheme,
  EpicDeleteTheme,
  EpicAddTheme,
);
