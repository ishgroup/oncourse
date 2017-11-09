import {combineEpics} from "redux-observable";
import {EpicGetThemes} from "./EpicGetThemes";
import {EpicSaveTheme} from "./EpicSaveTheme";
import {EpicDeleteTheme} from "./EpicDeleteTheme";
import {EpicAddTheme} from "./EpicAddTheme";

export const EpicThemes = combineEpics(
  EpicGetThemes,
  EpicSaveTheme,
  EpicDeleteTheme,
  EpicAddTheme,
);
