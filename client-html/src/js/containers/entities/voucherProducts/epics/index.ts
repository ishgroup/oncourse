import { combineEpics } from "redux-observable";
import { EpicGetVoucherProduct } from "./EpicGetVoucherProduct";
import { EpicUpdateVoucherProductItem } from "./EpicUpdateVoucherProductItem";
import { EpicCreateVoucherProduct } from "./EpicCreateVoucherProduct";
import { EpicGetMinMaxFee } from "./EpicGetMinMaxFee";

export const EpicVoucherProduct = combineEpics(
  EpicCreateVoucherProduct,
  EpicGetVoucherProduct,
  EpicUpdateVoucherProductItem,
  EpicGetVoucherProduct,
  EpicGetMinMaxFee
);
