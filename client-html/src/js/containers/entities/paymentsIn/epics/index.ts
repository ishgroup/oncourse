import { combineEpics } from "redux-observable";
import { EpicGetCustomValues } from "./EpicGetCustomValues";
import { EpicReversePaymentIn } from "./EpicReversePaymentIn";

export const EpicPaymentIn = combineEpics(
  EpicReversePaymentIn,
  EpicGetCustomValues
);
