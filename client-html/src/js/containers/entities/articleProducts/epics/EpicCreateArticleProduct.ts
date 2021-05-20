/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_ARTICLE_PRODUCT_ITEM } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { ArticleProduct } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import ArticleProductService from "../service/ArticleProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: ArticleProduct;

const request: EpicUtils.Request = {
  type: CREATE_ARTICLE_PRODUCT_ITEM,
  getData: payload => {
    savedItem = payload.articleProduct;
    return ArticleProductService.createArticleProduct(payload.articleProduct);
  },
  processData: () => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Product Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ArticleProduct" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Product Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateArticleProduct: Epic<any, any> = EpicUtils.Create(request);
