import {combineEpics} from "redux-observable";
import {EpicGetCheckoutSettings} from "./EpicGetCheckoutSettings";
import {EpicSetCheckoutSettings} from "./EpicSetCheckoutSettings";

export const EpicCheckoutSettings = combineEpics(
  EpicGetCheckoutSettings,
  EpicSetCheckoutSettings,
);
