import { combineEpics } from "redux-observable";
import { EpicUpdatePaymentIn } from "./EpicUpdatePaymentIn";
import { EpicReversePaymentIn } from "./EpicReversePaymentIn";
import { EpicGetCustomValues } from "./EpicGetCustomValues";

export const EpicPaymentIn = combineEpics(
  EpicUpdatePaymentIn,
  EpicReversePaymentIn,
  EpicGetCustomValues
);
