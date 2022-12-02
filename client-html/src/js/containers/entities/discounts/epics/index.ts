import { combineEpics } from "redux-observable";
import { EpicGetDiscountCosAccount } from "./EpicGetDiscountCosAccounts";
import { EpicGetContactRelations } from "./EpicGetContactRelations";

export const EpicDiscounts = combineEpics(
  EpicGetDiscountCosAccount,
  EpicGetContactRelations
);