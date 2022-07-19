import { combineEpics } from "redux-observable";
import { EpicGetAccount } from "./EpicGetAccount";
import { EpicUpdateAccountItem } from "./EpicUpdateAccountItem";

export const EpicAccounts = combineEpics(
  EpicGetAccount,
  EpicUpdateAccountItem
);
