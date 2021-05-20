/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_DISCOUNT_COS_ACCOUNTS, GET_DISCOUNT_COS_ACCOUNTS_FULFILLED } from "../actions";
import { DataResponse } from "@api/model";

const request: EpicUtils.Request<any, string> = {
  type: GET_DISCOUNT_COS_ACCOUNTS,
  getData: () => {
    return EntityService.getPlainRecords("Account", "description,accountCode", "isEnabled==true AND type=5");
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: GET_DISCOUNT_COS_ACCOUNTS_FULFILLED,
        payload: {
          cosAccounts: response.rows.map(({ id, values }) => ({
            id: Number(id),
            description: `${values[0]} ${values[1]}`
          }))
        }
      }
    ];
  }
};

export const EpicGetDiscountCosAccount: Epic<any, any> = EpicUtils.Create(request);
