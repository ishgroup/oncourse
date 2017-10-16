import {combineEpics} from "redux-observable";
import {EpicGetWebsiteSettings} from "./EpicGetWebsiteSettings";
import {EpicSetWebsiteSettings} from "./EpicSetWebsiteSettings";

export const EpicWebsiteSettings = combineEpics(
  EpicGetWebsiteSettings,
  EpicSetWebsiteSettings,
);
