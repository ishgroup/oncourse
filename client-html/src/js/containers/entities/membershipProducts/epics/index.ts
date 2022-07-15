import { combineEpics } from "redux-observable";
import { EpicGetMembershipProduct } from "./EpicGetMembershipProduct";
import { EpicUpdateMembershipProductItem } from "./EpicUpdateMembershipProductItem";

export const EpicMembershipProduct = combineEpics(
  EpicGetMembershipProduct,
  EpicUpdateMembershipProductItem
);
