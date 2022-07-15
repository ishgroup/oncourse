import { combineEpics } from "redux-observable";
import { EpicGetDiscount } from "./EpicGetDiscount";
import { EpicGetDiscountCosAccount } from "./EpicGetDiscountCosAccounts";
import { EpicUpdateDiscount } from "./EpicUpdateDiscount";
import { EpicDeleteDiscount } from "./EpicDeleteDiscount";
import { EpicGetContactRelations } from "./EpicGetContactRelations";

export const EpicDiscounts = combineEpics(
  EpicGetDiscount,
  EpicGetDiscountCosAccount,
  EpicUpdateDiscount,
  EpicDeleteDiscount,
  EpicGetContactRelations
);
