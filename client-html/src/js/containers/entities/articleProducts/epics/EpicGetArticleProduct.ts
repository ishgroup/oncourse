/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ARTICLE_PRODUCT_ITEM } from "../actions/index";
import { ArticleProduct } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ArticleProductService from "../service/ArticleProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_ARTICLE_PRODUCT_ITEM,
  getData: (id: number) => ArticleProductService.getArticleProduct(id),
  processData: (articleProduct: ArticleProduct) => {
    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: articleProduct, name: articleProduct.name }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, articleProduct)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetArticleProduct: Epic<any, any> = EpicUtils.Create(request);
