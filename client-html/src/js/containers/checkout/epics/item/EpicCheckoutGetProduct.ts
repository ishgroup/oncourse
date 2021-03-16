/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../../entities/common/entityItemsService";
import { CHECKOUT_GET_ITEM_PRODUCT, CHECKOUT_GET_ITEM_PRODUCT_FULFILLED } from "../../actions/chekoutItem";
import { CHECKOUT_ITEM_EDIT_VIEW_FORM } from "../../components/items/components/CkecoutItemViewForm";
import { ArticleProduct } from "@api/model";

const request: EpicUtils.Request = {
  type: CHECKOUT_GET_ITEM_PRODUCT,
  getData: (id: number) => getEntityItemById("ArticleProduct", id),
  processData: (memberShipProduct: ArticleProduct) => {
    return [
      {
        type: CHECKOUT_GET_ITEM_PRODUCT_FULFILLED,
        payload: { editRecord: memberShipProduct }
      },
      initialize(CHECKOUT_ITEM_EDIT_VIEW_FORM, memberShipProduct)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(CHECKOUT_ITEM_EDIT_VIEW_FORM, null)];
  }
};

export const EpicCheckoutGetProduct: Epic<any, any> = EpicUtils.Create(request);
