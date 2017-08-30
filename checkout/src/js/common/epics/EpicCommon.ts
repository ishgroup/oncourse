import {combineEpics} from "redux-observable";
import {EpicGetPreferences} from "./PreferencesEpic";

export const EpicCommon = combineEpics(
  EpicGetPreferences,
);