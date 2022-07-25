/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ArticleProduct } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const UPDATE_ARTICLE_PRODUCT_ITEM = _toRequestType("put/articleProduct");

export const updateArticleProduct = (id: string, articleProduct: ArticleProduct) => ({
  type: UPDATE_ARTICLE_PRODUCT_ITEM,
  payload: { id, articleProduct }
});
