/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import BankingService from "../services/BankingService";
import { GET_BANKING_ITEM, GET_BANKING_ITEM_FULFILLED } from "../actions";
import { Banking } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { format } from "date-fns";
import { EEE_D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_BANKING_ITEM,
  getData: (id: number) => BankingService.getBanking(id),
  processData: (banking: Banking) => {
    return [
      {
        type: GET_BANKING_ITEM_FULFILLED,
        payload: { banking }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: {
          editRecord: banking,
          name: `${format(new Date(banking.settlementDate), EEE_D_MMM_YYYY)}${
            banking.adminSite ? " for " + banking.adminSite : ""
          }`
        }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, banking)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetBanking: Epic<any, any> = EpicUtils.Create(request);
