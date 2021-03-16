/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SALE, SET_SALE_DELIVERED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { ProductItem } from "@api/model";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import SaleService from "../services/SaleService";

const request: EpicUtils.Request<any, { id: number }> = {
  type: SET_SALE_DELIVERED,
  getData: ({ id }) =>
    SaleService.getSale(id).then((productItem: ProductItem) => {
      productItem.status = "Delivered";

      return updateEntityItemById("Sale", productItem.id, productItem);
    }),
  processData: (v, s, { id }) => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: id }
      },
      {
        type: GET_SALE,
        payload: { id }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Sale Record was not updated")
};

export const EpicSetToDelivered: Epic<any, any> = EpicUtils.Create(request);
