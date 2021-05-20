/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { SAVE_FUNDING_CONTACTS_FULFILLED, SAVE_FUNDING_CONTACTS_REQUEST } from "../actions";
import FundingContractService from "../services/FundingContractService";

const request: EpicUtils.Request = {
  type: SAVE_FUNDING_CONTACTS_REQUEST,
  getData: ({ items, method }) =>
    method === "PATCH"
      ? FundingContractService.patchFundingContracts(items)
      : FundingContractService.updateFundingContracts(items),
  retrieveData: () => FundingContractService.getFundingContracts(),
  processData: (items: any) => {
    return [
      {
        type: SAVE_FUNDING_CONTACTS_FULFILLED,
        payload: { fundingContracts: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Form was successfully saved" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Form was not saved");
  }
};

export const EpicSaveFundingContracts: Epic<any, any> = EpicUtils.Create(request);
