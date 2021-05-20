/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Account, AccountType, DataResponse } from "@api/model";
import { GET_PLAIN_ACCOUNTS, GET_PLAIN_ACCOUNTS_FULFILLED } from "../actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { AccountExtended } from "../../../../model/entities/Account";

const request: EpicUtils.Request = {
  type: GET_PLAIN_ACCOUNTS,
  getData: () => EntityService.getPlainRecords("Account", "id,description,accountCode,type,tax.id", "isEnabled==true"),
  processData: (records: DataResponse) => {
    const items: AccountExtended[] = [];
    for (const row of records.rows) {
      items.push({
        id: Number(row.values[0]),
        description: row.values[1],
        accountCode: row.values[2],
        type: row.values[3] as AccountType,
        taxId: row.values[4]
      });
    }

    items.sort((a, b) => (a.description[0] > b.description[0] ? 1 : -1));

    return [
      {
        type: GET_PLAIN_ACCOUNTS_FULFILLED,
        payload: { items }
      }
    ];
  }
};

export const EpicGetPlainAccounts: Epic<any, any> = EpicUtils.Create(request);
