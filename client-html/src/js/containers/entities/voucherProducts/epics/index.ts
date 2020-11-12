import { combineEpics } from "redux-observable";
import { EpicGetVoucherProduct } from "./EpicGetVoucherProduct";
import { EpicUpdateVoucherProductItem } from "./EpicUpdateVoucherProductItem";
import { EpicCreateVoucherProduct } from "./EpicCreateVoucherProduct";
import { EpicGetVoucherProductCourses } from "./EpicGetVoucherProductCourses";
import { EpicGetMinMaxFee } from "./EpicGetMinMaxFee";
import { EpicGetPlainVoucherProducts } from "./EpicGetPlainVoucherProducts";

export const EpicVoucherProduct = combineEpics(
  EpicCreateVoucherProduct,
  EpicGetVoucherProduct,
  EpicUpdateVoucherProductItem,
  EpicGetVoucherProduct,
  EpicGetVoucherProductCourses,
  EpicGetMinMaxFee,
  EpicGetPlainVoucherProducts
);
