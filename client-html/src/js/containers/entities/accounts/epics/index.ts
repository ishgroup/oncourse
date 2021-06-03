import { combineEpics } from "redux-observable";
import { EpicGetAccount } from "./EpicGetAccount";
import { EpicUpdateAccountItem } from "./EpicUpdateAccountItem";
import { EpicCreateAccount } from "./EpicCreateAccount";
import { EpicDeleteAccount } from "./EpicDeleteAccount";

export const EpicAccounts = combineEpics(
  EpicGetAccount,
  EpicUpdateAccountItem,
  EpicCreateAccount,
  EpicDeleteAccount
);
