/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_DISCOUNT_ITEM, DELETE_DISCOUNT_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import DiscountService from "../services/DiscountService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_DISCOUNT_ITEM,
  getData: (id: number) => DiscountService.removeDiscount(id),
  processData: () => {
    return [
      {
        type: DELETE_DISCOUNT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Discount record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Discount", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Discount record was not deleted")
};

export const EpicDeleteDiscount: Epic<any, any> = EpicUtils.Create(request);
