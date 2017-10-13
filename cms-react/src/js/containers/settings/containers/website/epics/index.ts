import {combineEpics} from "redux-observable";
import {EpicGetWebsiteSettings} from "./EpicGetCheckoutSettings";
import {EpicSetWebsiteSettings} from "./EpicSetCheckoutSettings";

export const EpicWebsiteSettings = combineEpics(
  EpicGetWebsiteSettings,
  EpicSetWebsiteSettings,
);
