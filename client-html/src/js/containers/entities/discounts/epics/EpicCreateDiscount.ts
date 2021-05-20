/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_DISCOUNT_ITEM, CREATE_DISCOUNT_ITEM_FULFILLED } from "../actions";
import { Discount } from "@api/model";
import DiscountService from "../services/DiscountService";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, Discount> = {
  type: CREATE_DISCOUNT_ITEM,
  getData: discount => {
    return DiscountService.createDiscount(discount);
  },
  processData: () => {
    return [
      {
        type: CREATE_DISCOUNT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Discount created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Discount" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: (response, discount) => [
    ...FetchErrorHandler(response, "Discount was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, discount)
  ]
};

export const EpicCreateDiscount: Epic<any, any> = EpicUtils.Create(request);
