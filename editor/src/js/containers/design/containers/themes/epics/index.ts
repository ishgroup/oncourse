import {combineEpics} from "redux-observable";
import {EpicGetThemes} from "./EpicGetThemes";
import {EpicSaveTheme} from "./EpicSaveTheme";
import {EpicDeleteTheme} from "./EpicDeleteTheme";

export const EpicThemes = combineEpics(
  EpicGetThemes,
  EpicSaveTheme,
  EpicDeleteTheme,
);
