/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_DEFAULT_INVOICE_TERMS, GET_DEFAULT_INVOICE_TERMS_FULFILLED } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import UserPreferenceService from "../../../../common/services/UserPreferenceService";
import { ACCOUNT_INVOICE_TERMS } from "../../../../constants/Config";

const request: EpicUtils.Request = {
  type: GET_DEFAULT_INVOICE_TERMS,
  getData: () => UserPreferenceService.getUserPreferencesByKeys([ACCOUNT_INVOICE_TERMS]),
  processData: (response: { [key: string]: string }) => {
    return [
      {
        type: GET_DEFAULT_INVOICE_TERMS_FULFILLED,
        payload: { defaultTerms: response[ACCOUNT_INVOICE_TERMS] }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get default invoice terms")
};

export const EpicGetDefaultInvoiceTerms: Epic<any, any> = EpicUtils.Create(request);
