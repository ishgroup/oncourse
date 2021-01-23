/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import {
 Banking, Payment, PrintRequest, Report 
} from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import BankingService from "../services/BankingService";
import { CREATE_BANKING_ITEM, CREATE_BANKING_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { doPrintRequest } from "../../../../common/components/list-view/components/share/actions";
import { BankingReport, PaymentInType, PaymentOutType } from "../consts";
import { State } from "../../../../reducers/state";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, State, any> = {
  type: CREATE_BANKING_ITEM,
  getData: payload => BankingService.createBanking(payload.banking),
  processData: (value, state, payload) => {
    // const reportId = (state.share.pdfReports as Report[]).find(v => v.name === BankingReport).id;

    const paymentInIds = payload.banking.payments
      .filter((p: Payment) => p.paymentTypeName === PaymentInType)
      .map((p: Payment) => p.paymentId);

    const paymentOutIds = payload.banking.payments
      .filter((p: Payment) => p.paymentTypeName === PaymentOutType)
      .map((p: Payment) => p.paymentId);

    const filters = {};
    if (paymentInIds && paymentInIds.length > 0) {
      filters["PaymentIn"] = `id in (${paymentInIds.join(",")})`;
    }
    if (paymentOutIds && paymentOutIds.length > 0) {
      filters["PaymentOut"] = `id in (${paymentOutIds.join(",")})`;
    }

    const actions = [
      {
        type: CREATE_BANKING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Banking Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Banking", listUpdate: false }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];

    return actions;
  },
  processError: data => [...FetchErrorHandler(data, "Banking Record was not created")]
};

export const EpicCreateBanking: Epic<any, any> = EpicUtils.Create(request);
