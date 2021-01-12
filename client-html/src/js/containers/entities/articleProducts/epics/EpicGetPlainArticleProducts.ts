/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { ArticleProduct } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PLAIN_ARTICLE_PRODUCT_ITEMS, GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";

export const defaultArticleProductMap = ({ id, values }) => ({
  id: Number(id),
  code: values[0],
  name: values[1],
  price: values[2]
});

const request: EpicUtils.Request<any, any, { offset?: number, columns?: string, ascending?: boolean, pageSize?: number }> = {
  type: GET_PLAIN_ARTICLE_PRODUCT_ITEMS,
  hideLoadIndicator: true,
  getData({
 offset, columns, ascending, pageSize
}, { articleProducts: { search } }) {
    return EntityService.getPlainRecords(
      "ArticleProduct",
      columns || "sku,name,price_with_tax",
      search,
      pageSize || 100,
      offset,
      "",
      ascending
    );
  },
  processData({ rows, offset, pageSize }, s, { columns }) {
    const items: ArticleProduct[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultArticleProductMap);
    return [
      {
        type: GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetPlainArticleProducts: Epic<any, any> = EpicUtils.Create(request);
