import {combineEpics} from "redux-observable";
import {EpicGetThemes} from "./EpicGetThemes";

export const EpicThemes = combineEpics(
  EpicGetThemes,
);
