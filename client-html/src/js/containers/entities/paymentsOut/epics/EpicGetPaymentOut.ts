/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { PaymentOut } from "@api/model";
import { initialize, reset } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PAYMENT_OUT_ITEM, GET_PAYMENT_OUT_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { PaymentOutModel } from "../reducers/state";

const request: EpicUtils.Request = {
  type: GET_PAYMENT_OUT_ITEM,
  getData: (id: number) => getEntityItemById("PaymentOut", id),
  processData: (paymentOut: PaymentOutModel) => [
      {
        type: GET_PAYMENT_OUT_ITEM_FULFILLED,
        payload: { paymentOut }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: paymentOut, name: paymentOut.payeeName }
      },
      reset(LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, paymentOut)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetPaymentOut: Epic<any, any> = EpicUtils.Create(request);
