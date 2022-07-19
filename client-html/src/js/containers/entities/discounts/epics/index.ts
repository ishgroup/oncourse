import { combineEpics } from "redux-observable";
import { EpicGetDiscount } from "./EpicGetDiscount";
import { EpicGetDiscountCosAccount } from "./EpicGetDiscountCosAccounts";
import { EpicUpdateDiscount } from "./EpicUpdateDiscount";
import { EpicGetContactRelations } from "./EpicGetContactRelations";

export const EpicDiscounts = combineEpics(
  EpicGetDiscount,
  EpicGetDiscountCosAccount,
  EpicUpdateDiscount,
  EpicGetContactRelations
);