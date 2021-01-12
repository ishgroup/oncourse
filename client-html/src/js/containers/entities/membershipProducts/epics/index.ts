import { combineEpics } from "redux-observable";
import { EpicGetMembershipProduct } from "./EpicGetMembershipProduct";
import { EpicCreateMembershipProduct } from "./EpicCreateMembershipProduct";
import { EpicUpdateMembershipProductItem } from "./EpicUpdateMembershipProductItem";

export const EpicMembershipProduct = combineEpics(
  EpicGetMembershipProduct,
  EpicCreateMembershipProduct,
  EpicUpdateMembershipProductItem
);
