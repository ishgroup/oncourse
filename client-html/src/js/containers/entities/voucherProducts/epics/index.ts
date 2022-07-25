import { combineEpics } from "redux-observable";
import { EpicUpdateVoucherProductItem } from "./EpicUpdateVoucherProductItem";
import { EpicGetMinMaxFee } from "./EpicGetMinMaxFee";

export const EpicVoucherProduct = combineEpics(
  EpicUpdateVoucherProductItem,
  EpicGetMinMaxFee
);