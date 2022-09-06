import { combineEpics } from "redux-observable";
import { EpicGetMinMaxFee } from "./EpicGetMinMaxFee";

export const EpicVoucherProduct = combineEpics(
  EpicGetMinMaxFee
);