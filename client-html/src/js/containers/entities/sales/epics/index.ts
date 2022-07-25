import { combineEpics } from "redux-observable";
import { EpicGetSales } from "./EpicGetSales";
import { EpicUpdateSale } from "./EpicUpdateSale";
import { EpicCancelSale } from "./EpicCancelSale";
import { EpicGetSaleDetails } from "./EpicGetSaleDetails";
import { EpicSetToDelivered } from "./EpicSetToDelivered";
import { EpicGetSaleMenuTags } from "./EpicGetSaleMenuTags";

export const EpicSales = combineEpics(
  EpicGetSales,
  EpicUpdateSale,
  EpicCancelSale,
  EpicGetSaleDetails,
  EpicSetToDelivered,
  EpicGetSaleMenuTags
);