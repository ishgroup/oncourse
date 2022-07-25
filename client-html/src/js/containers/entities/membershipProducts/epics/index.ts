import { combineEpics } from "redux-observable";
import { EpicUpdateMembershipProductItem } from "./EpicUpdateMembershipProductItem";

export const EpicMembershipProduct = combineEpics(
  EpicUpdateMembershipProductItem
);