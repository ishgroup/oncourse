/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import {
  GET_QUICK_SEARCH_CONCESSION_TYPES,
  GET_QUICK_SEARCH_CONCESSION_TYPES_FULFILLED,
  GET_QUICK_SEARCH_CORPORATE_PASSES,
  GET_QUICK_SEARCH_CORPORATE_PASSES_FULFILLED
} from "../actions";
import { DataResponse } from "@api/model";

const quickSearchConcessionTypeRequest: EpicUtils.Request<any, any, string> = {
  type: GET_QUICK_SEARCH_CONCESSION_TYPES,
  getData: payload => {
    return EntityService.getPlainRecords("ConcessionType", "name,isEnabled", payload);
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: GET_QUICK_SEARCH_CONCESSION_TYPES_FULFILLED,
        payload: {
          items: response.rows.map(({ id, values }) => ({ id, name: values[0], allowOnWeb: values[1] === "true" })),
          pending: false
        }
      }
    ];
  }
};

const quickSearchCorporatePassRequest: EpicUtils.Request<any, any, string> = {
  type: GET_QUICK_SEARCH_CORPORATE_PASSES,
  getData: payload => {
    return EntityService.getPlainRecords(
      "CorporatePass",
      "contact.fullName",
      `${payload ? `${payload} and ` : ""}(expiryDate is null or expiryDate >= today)`
    );
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: GET_QUICK_SEARCH_CORPORATE_PASSES_FULFILLED,
        payload: {
          items: response.rows.map(({ id, values }) => ({ id, contactFullName: values[0] })),
          pending: false
        }
      }
    ];
  }
};

export const EpicQuickSearchConcessionType: Epic<any, any> = EpicUtils.Create(quickSearchConcessionTypeRequest);

export const EpicQuickSearchCorporatePasses: Epic<any, any> = EpicUtils.Create(quickSearchCorporatePassRequest);
