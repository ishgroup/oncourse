import { combineEpics } from "redux-observable";
import { EpicGetVoucherProduct } from "./EpicGetVoucherProduct";
import { EpicUpdateVoucherProductItem } from "./EpicUpdateVoucherProductItem";
import { EpicGetMinMaxFee } from "./EpicGetMinMaxFee";

export const EpicVoucherProduct = combineEpics(
  EpicGetVoucherProduct,
  EpicUpdateVoucherProductItem,
  EpicGetVoucherProduct,
  EpicGetMinMaxFee
);
