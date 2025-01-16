import { combineEpics } from "redux-observable";
import { EpicGetContactRelations } from "./EpicGetContactRelations";
import { EpicGetDiscountCosAccount } from "./EpicGetDiscountCosAccounts";

export const EpicDiscounts = combineEpics(
  EpicGetDiscountCosAccount,
  EpicGetContactRelations
);