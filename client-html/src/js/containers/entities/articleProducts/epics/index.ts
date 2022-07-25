import { combineEpics } from "redux-observable";
import { EpicUpdateArticleProductItem } from "./EpicUpdateArticleProductItem";

export const EpicArticleProduct = combineEpics(
  EpicUpdateArticleProductItem
);
