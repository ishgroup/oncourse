import { combineEpics } from "redux-observable";
import { EpicGetArticleProduct } from "./EpicGetArticleProduct";
import { EpicUpdateArticleProductItem } from "./EpicUpdateArticleProductItem";
import { EpicCreateArticleProduct } from "./EpicCreateArticleProduct";
import { EpicGetPlainArticleProducts } from "./EpicGetPlainArticleProducts";

export const EpicArticleProduct = combineEpics(
  EpicGetArticleProduct,
  EpicUpdateArticleProductItem,
  EpicCreateArticleProduct,
  EpicGetPlainArticleProducts
);
