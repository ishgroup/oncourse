/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import FundingContractService from "../services/FundingContractService";
import { GET_FUNDING_CONTACTS_FULFILLED, GET_FUNDING_CONTACTS_REQUEST } from "../actions";

const request: EpicUtils.Request = {
  type: GET_FUNDING_CONTACTS_REQUEST,
  getData: () => FundingContractService.getFundingContracts(),
  processData: fundingContracts => {
    return [
      {
        type: GET_FUNDING_CONTACTS_FULFILLED,
        payload: { fundingContracts }
      }
    ];
  }
};

export const EpicGetFundingContracts: Epic<any, any> = EpicUtils.Create(request);
