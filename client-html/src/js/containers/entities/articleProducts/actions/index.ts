/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ArticleProduct } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const GET_ARTICLE_PRODUCT_ITEM = _toRequestType("get/articleProduct");

export const UPDATE_ARTICLE_PRODUCT_ITEM = _toRequestType("put/articleProduct");

export const CREATE_ARTICLE_PRODUCT_ITEM = _toRequestType("post/articleProduct");

export const getArticleProduct = (id: string) => ({
  type: GET_ARTICLE_PRODUCT_ITEM,
  payload: id
});

export const updateArticleProduct = (id: string, articleProduct: ArticleProduct) => ({
  type: UPDATE_ARTICLE_PRODUCT_ITEM,
  payload: { id, articleProduct }
});

export const createArticleProduct = (articleProduct: ArticleProduct) => ({
  type: CREATE_ARTICLE_PRODUCT_ITEM,
  payload: { articleProduct }
});
