import { combineEpics } from "redux-observable";
import { EpicGetArticleProduct } from "./EpicGetArticleProduct";
import { EpicUpdateArticleProductItem } from "./EpicUpdateArticleProductItem";

export const EpicArticleProduct = combineEpics(
  EpicGetArticleProduct,
  EpicUpdateArticleProductItem
);
