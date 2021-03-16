/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { ProductItem } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SALE, UPDATE_SALE, UPDATE_SALE_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";

const request: EpicUtils.Request<any, { id: string; productItem: ProductItem }> = {
  type: UPDATE_SALE,
  getData: ({ id, productItem }) => updateEntityItemById("Sale", Number(id), productItem),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_SALE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_SALE,
        payload: { id }
      }] : []
    ],
  processError: response => FetchErrorHandler(response, "Sale Record was not updated")
};

export const EpicUpdateSale: Epic<any, any> = EpicUtils.Create(request);
