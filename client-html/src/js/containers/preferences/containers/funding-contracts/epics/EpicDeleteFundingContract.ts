/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { DELETE_FUNDING_CONTACT_FULFILLED, DELETE_FUNDING_CONTACT_REQUEST } from "../actions";
import FundingContractService from "../services/FundingContractService";

const request: EpicUtils.Request = {
  type: DELETE_FUNDING_CONTACT_REQUEST,
  getData: payload => FundingContractService.deleteFundingContract(payload.id),
  retrieveData: () => FundingContractService.getFundingContracts(),
  processData: (items: any) => {
    return [
      {
        type: DELETE_FUNDING_CONTACT_FULFILLED,
        payload: { fundingContracts: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Item was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Item was not deleted");
  }
};

export const EpicDeleteFundingContract: Epic<any, any> = EpicUtils.Create(request);
