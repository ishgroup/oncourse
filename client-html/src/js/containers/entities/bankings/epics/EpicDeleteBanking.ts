/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import BankingService from "../services/BankingService";
import { DELETE_BANKING_ITEM, DELETE_BANKING_ITEM_FULFILLED } from "../actions";
import { Banking } from "@api/model";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_BANKING_ITEM,
  getData: (id: number) => BankingService.removeBanking(id),
  processData: (banking: Banking) => {
    return [
      {
        type: DELETE_BANKING_ITEM_FULFILLED,
        payload: { banking }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Banking record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Banking", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Banking record was not deleted")
};

export const EpicDeleteBanking: Epic<any, any> = EpicUtils.Create(request);
