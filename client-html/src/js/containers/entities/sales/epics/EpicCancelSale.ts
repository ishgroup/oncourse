/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { ProductItemCancel } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CANCEL_SALE, CANCEL_SALE_FULFILLED, GET_SALE } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import SaleService from "../services/SaleService";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";

const request: EpicUtils.Request<any, { productItemCancel: ProductItemCancel }> = {
  type: CANCEL_SALE,
  getData: ({ productItemCancel }) => SaleService.cancelSale(productItemCancel),
  processData: (v, s, { productItemCancel: { id } }) => [
      {
        type: CANCEL_SALE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record cancelled" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: id }
      },
      {
        type: GET_SALE,
        payload: { id }
      }
    ],
  processError: response => FetchErrorHandler(response, "Sale Record was not cancelled")
};

export const EpicCancelSale: Epic<any, any> = EpicUtils.Create(request);
