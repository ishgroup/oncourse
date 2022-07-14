import { combineEpics } from "redux-observable";
import { EpicGetAccount } from "./EpicGetAccount";
import { EpicUpdateAccountItem } from "./EpicUpdateAccountItem";
import { EpicDeleteAccount } from "./EpicDeleteAccount";

export const EpicAccounts = combineEpics(
  EpicGetAccount,
  EpicUpdateAccountItem,
  EpicDeleteAccount
);
