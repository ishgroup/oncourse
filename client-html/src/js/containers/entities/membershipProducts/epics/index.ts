import { combineEpics } from "redux-observable";
import { EpicGetMembershipProduct } from "./EpicGetMembershipProduct";
import { EpicCreateMembershipProduct } from "./EpicCreateMembershipProduct";
import { EpicUpdateMembershipProductItem } from "./EpicUpdateMembershipProductItem";
import { EpicGetMembershipProductContactRelationTypes } from "./EpicGetMembershipProductContactRelationTypes";
import { EpicGetMembershipProductDiscounts } from "./EpicGetMembershipProductDiscounts";
import { EpicGetPlainMembershipProducts } from "./EpicGetPlainMembershipProducts";

export const EpicMembershipProduct = combineEpics(
  EpicGetMembershipProduct,
  EpicCreateMembershipProduct,
  EpicUpdateMembershipProductItem,
  EpicGetMembershipProductContactRelationTypes,
  EpicGetMembershipProductDiscounts,
  EpicGetPlainMembershipProducts
);
