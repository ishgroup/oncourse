/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ArticleProduct } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_ARTICLE_PRODUCT_ITEM = _toRequestType("get/articleProduct");

export const UPDATE_ARTICLE_PRODUCT_ITEM = _toRequestType("put/articleProduct");

export const CREATE_ARTICLE_PRODUCT_ITEM = _toRequestType("post/articleProduct");

export const GET_PLAIN_ARTICLE_PRODUCT_ITEMS = _toRequestType("get/plain/articleProduct");
export const GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED = FULFILLED(GET_PLAIN_ARTICLE_PRODUCT_ITEMS);

export const SET_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH = _toRequestType("set/plain/articleProduct/search");
export const CLEAR_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH = _toRequestType("clear/plain/articleProduct/search");

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

export const getPlainArticleProducts = (offset?: number, columns?: string, ascending?: boolean, pageSize?: number) => ({
  type: GET_PLAIN_ARTICLE_PRODUCT_ITEMS,
  payload: {
 offset, columns, ascending, pageSize
}
});

export const setPlainArticleProductSearch = (search: string) => ({
  type: SET_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH,
  payload: { search }
});

export const clearPlainArticleProductSearch = () => ({
  type: CLEAR_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH,
  payload: { search: "", items: [] }
});
