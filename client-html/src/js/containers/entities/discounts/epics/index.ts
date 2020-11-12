import { combineEpics } from "redux-observable";
import { EpicGetDiscounts } from "./EpicGetDiscounts";
import { EpicGetDiscount } from "./EpicGetDiscount";
import { EpicGetMembershipsProducts } from "./EpicGetMembershipsProducts";
import { EpicGetDiscountContactRelationTypes } from "./EpicGetContactRelationTypes";
import { EpicGetDiscountCosAccount } from "./EpicGetDiscountCosAccounts";
import { EpicUpdateDiscount } from "./EpicUpdateDiscount";
import { EpicCreateDiscount } from "./EpicCreateDiscount";
import { EpicDeleteDiscount } from "./EpicDeleteDiscount";

export const EpicDiscounts = combineEpics(
  EpicGetDiscounts,
  EpicGetDiscount,
  EpicGetMembershipsProducts,
  EpicGetDiscountContactRelationTypes,
  EpicGetDiscountCosAccount,
  EpicUpdateDiscount,
  EpicCreateDiscount,
  EpicDeleteDiscount
);
