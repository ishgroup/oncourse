import { combineEpics } from "redux-observable";
import { EpicCancelSale } from "./EpicCancelSale";
import { EpicGetSaleDetails } from "./EpicGetSaleDetails";
import { EpicGetSaleMenuTags } from "./EpicGetSaleMenuTags";
import { EpicGetSales } from "./EpicGetSales";
import { EpicSetToDelivered } from "./EpicSetToDelivered";

export const EpicSales = combineEpics(
  EpicGetSales,
  EpicCancelSale,
  EpicGetSaleDetails,
  EpicSetToDelivered,
  EpicGetSaleMenuTags
);