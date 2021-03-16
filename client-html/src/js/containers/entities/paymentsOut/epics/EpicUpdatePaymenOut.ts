/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { PaymentOut } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PAYMENT_OUT_ITEM, UPDATE_PAYMENT_OUT_ITEM, UPDATE_PAYMENT_OUT_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { PaymentOutModel } from "../reducers/state";

const request: EpicUtils.Request<any, { id: number; paymentOut: PaymentOutModel }> = {
  type: UPDATE_PAYMENT_OUT_ITEM,
  getData: ({ id, paymentOut }) => updateEntityItemById("PaymentOut", id, paymentOut),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_PAYMENT_OUT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "PaymentOut Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PaymentOut", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_PAYMENT_OUT_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { paymentOut }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, paymentOut)]
};

export const EpicUpdatePaymentOut: Epic<any, any> = EpicUtils.Create(request);
