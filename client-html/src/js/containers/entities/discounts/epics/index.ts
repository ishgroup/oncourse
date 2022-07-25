import { combineEpics } from "redux-observable";
import { EpicGetDiscountCosAccount } from "./EpicGetDiscountCosAccounts";
import { EpicUpdateDiscount } from "./EpicUpdateDiscount";
import { EpicGetContactRelations } from "./EpicGetContactRelations";

export const EpicDiscounts = combineEpics(
  EpicGetDiscountCosAccount,
  EpicUpdateDiscount,
  EpicGetContactRelations
);