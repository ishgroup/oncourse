/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ARTICLE_PRODUCT_ITEM, UPDATE_ARTICLE_PRODUCT_ITEM } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { ArticleProduct } from "@api/model";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import ArticleProductService from "../service/ArticleProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, any, { id: number; articleProduct: ArticleProduct }> = {
  type: UPDATE_ARTICLE_PRODUCT_ITEM,
  getData: ({ id, articleProduct }) => ArticleProductService.updateArticleProduct(id, articleProduct),
  processData: (v, s, { id }) => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Product Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ArticleProduct", listUpdate: true, savedID: id }
      },
      {
        type: GET_ARTICLE_PRODUCT_ITEM,
        payload: id
      }
    ];
  },
  processError: (response, { articleProduct }) => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, articleProduct)];
  }
};

export const EpicUpdateArticleProductItem: Epic<any, any> = EpicUtils.Create(request);
