/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import {
  GET_INCOME_ACCOUNTS,
  GET_INCOME_ACCOUNTS_FULFILLED,
  GET_LIABILITY_ACCOUNTS,
  GET_LIABILITY_ACCOUNTS_FULFILLED
} from "../actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { Account, AccountType, DataResponse, Tax } from "@api/model";

const parseResponse = (records: DataResponse) => {
  const items: Account[] = [];
  for (const row of records.rows) {
    const taxId = row.values[4] ? Number(row.values[4]) : null;
    items.push({
      id: Number(row.values[0]),
      description: row.values[1],
      accountCode: row.values[2],
      type: row.values[3] as AccountType,
      tax: {
        id: taxId
      } as Tax
    });
  }
  items.sort((a, b) => (a.description[0] > b.description[0] ? 1 : -1));
  return items;
};

const makeRequest = (type: number) => {
  return EntityService.getPlainRecords(
    "Account",
    "id,description,accountCode,type,tax.id",
    `isEnabled==true AND type = ${type}`
  );
};

const incomeRequest: EpicUtils.Request = {
  type: GET_INCOME_ACCOUNTS,
  getData: () => makeRequest(4),
  processData: (records: DataResponse) => {
    return [
      {
        type: GET_INCOME_ACCOUNTS_FULFILLED,
        payload: { incomeItems: parseResponse(records) }
      }
    ];
  }
};

const liabilityRequest: EpicUtils.Request = {
  type: GET_LIABILITY_ACCOUNTS,
  getData: () => makeRequest(2),
  processData: (records: DataResponse) => {
    return [
      {
        type: GET_LIABILITY_ACCOUNTS_FULFILLED,
        payload: { liabilityItems: parseResponse(records) }
      }
    ];
  }
};

export const EpicGetIncomeAccounts: Epic<any, any> = EpicUtils.Create(incomeRequest);
export const EpicGetLiabilityAccounts: Epic<any, any> = EpicUtils.Create(liabilityRequest);
