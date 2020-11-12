/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ArticleProductState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH,
  GET_PLAIN_ARTICLE_PRODUCT_ITEMS,
  GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED,
  SET_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH
} from "../actions";

const initial: ArticleProductState = {
  items: [],
  search: "",
  loading: false,
  rowsCount: 5000
};

export const articleProductsReducer = (state: ArticleProductState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PLAIN_ARTICLE_PRODUCT_ITEMS: {
      return {
        ...state,
        loading: true
      };
    }

    case SET_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_PLAIN_ARTICLE_PRODUCT_ITEMS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED: {
      const { items, offset, pageSize } = action.payload;

      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    default:
      return state;
  }
};
