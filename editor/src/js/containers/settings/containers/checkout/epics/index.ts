import {combineEpics} from "redux-observable";
import {EpicSetCheckoutSettings} from "./EpicSetCheckoutSettings";
import {EpicGetCheckoutSettings} from "./EpicGetCheckoutSettings";

export const EpicCheckoutSettings = combineEpics(
  EpicGetCheckoutSettings,
  EpicSetCheckoutSettings,
);
