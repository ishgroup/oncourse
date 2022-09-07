import { combineEpics } from "redux-observable";
import { EpicReversePaymentIn } from "./EpicReversePaymentIn";
import { EpicGetCustomValues } from "./EpicGetCustomValues";

export const EpicPaymentIn = combineEpics(
  EpicReversePaymentIn,
  EpicGetCustomValues
);
