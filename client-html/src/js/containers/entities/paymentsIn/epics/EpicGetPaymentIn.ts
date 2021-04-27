/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { PaymentIn } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PAYMENT_IN_ITEM, GET_PAYMENT_IN_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_PAYMENT_IN_ITEM,
  getData: (id: number) => getEntityItemById("PaymentIn", id),

  processData: (paymentIn: PaymentIn) => [
      {
        type: GET_PAYMENT_IN_ITEM_FULFILLED,
        payload: { paymentIn }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: paymentIn, name: paymentIn.payerName }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, paymentIn)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetPaymentIn: Epic<any, any> = EpicUtils.Create(request);
