import {combineEpics} from "redux-observable";
import {EpicGetRedirectSettings} from "./EpicGetRedirectSettings";
import {EpicSetRedirectSettings} from "./EpicSetRedirectSettings";

export const EpicRedirectSettings = combineEpics(
  EpicGetRedirectSettings,
  EpicSetRedirectSettings,
);
