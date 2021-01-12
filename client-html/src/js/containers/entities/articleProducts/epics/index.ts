import { combineEpics } from "redux-observable";
import { EpicGetArticleProduct } from "./EpicGetArticleProduct";
import { EpicUpdateArticleProductItem } from "./EpicUpdateArticleProductItem";
import { EpicCreateArticleProduct } from "./EpicCreateArticleProduct";

export const EpicArticleProduct = combineEpics(
  EpicGetArticleProduct,
  EpicUpdateArticleProductItem,
  EpicCreateArticleProduct
);
