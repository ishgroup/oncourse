import { combineEpics } from "redux-observable";
import { EpicGetPaymentIn } from "./EpicGetPaymentIn";
import { EpicUpdatePaymentIn } from "./EpicUpdatePaymentIn";
import { EpicReversePaymentIn } from "./EpicReversePaymentIn";
import { EpicGetCustomValues } from "./EpicGetCustomValues";

export const EpicPaymentIn = combineEpics(
  EpicGetPaymentIn,
  EpicUpdatePaymentIn,
  EpicReversePaymentIn,
  EpicGetCustomValues
);
