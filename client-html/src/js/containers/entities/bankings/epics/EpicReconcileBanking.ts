/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { getEntityRecord } from "../../common/actions";
import { POST_RECONCILE_BANKING, POST_RECONCILE_BANKING_FULFILLED } from "../actions";
import BankingService from "../services/BankingService";

const request: EpicUtils.Request<any, { ids: number[] }> = {
  type: POST_RECONCILE_BANKING,
  getData: ({ ids }) => BankingService.reconcileBankings(ids),
  processData: (v, s, { ids }) => [
      {
        type: POST_RECONCILE_BANKING_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: `Reconcile banking deposit${ids.length > 1 ? "s" : ""} request successfully handled` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Banking", listUpdate: true }
      },
      getEntityRecord(ids[0], "Banking")
    ],
  processError: response => FetchErrorHandler(response)
};

export const EpicReconcileBanking: Epic<any, any> = EpicUtils.Create(request);
