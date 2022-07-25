import { combineEpics } from "redux-observable";
import { EpicUpdateAccountItem } from "./EpicUpdateAccountItem";

export const EpicAccounts = combineEpics(
  EpicUpdateAccountItem
);
