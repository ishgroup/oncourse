import { combineEpics } from "redux-observable";
import { EpicGetAccount } from "./EpicGetAccount";
import { EpicUpdateAccountItem } from "./EpicUpdateAccountItem";
import { EpicCreateAccount } from "./EpicCreateAccount";
import { EpicDeleteAccount } from "./EpicDeleteAccount";
import { EpicGetPlainAccounts } from "./EpicGetPlainAccounts";
import { EpicGetIncomeAccounts, EpicGetLiabilityAccounts } from "./EpicGetEnabledAccounts";

export const EpicAccounts = combineEpics(
  EpicGetAccount,
  EpicUpdateAccountItem,
  EpicCreateAccount,
  EpicDeleteAccount,
  EpicGetPlainAccounts,
  EpicGetIncomeAccounts,
  EpicGetLiabilityAccounts
);
