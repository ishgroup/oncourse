/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_MEMBERSHIP_PRODUCT_DISCOUNTS, GET_MEMBERSHIP_PRODUCT_DISCOUNTS_FULFILLED } from "../actions";
import { DataResponse } from "@api/model";

const request: EpicUtils.Request<any, any, string> = {
  type: GET_MEMBERSHIP_PRODUCT_DISCOUNTS,
  getData: payload => {
    return EntityService.getPlainRecords(
      "Discount",
      "name",
      `${payload} and (validTo == null or validTo >= today)`
    );
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: GET_MEMBERSHIP_PRODUCT_DISCOUNTS_FULFILLED,
        payload: {
          discountItems: response.rows
            .map(({ id, values }) => ({
              discountId: Number(id),
              discountName: values[0]
            }))
            .sort((a, b) => {
              if (a.discountId < b.discountId) {
                return -1;
              }
              if (a.discountId > b.discountId) {
                return 1;
              }
              return 0;
            }),
          discountsPending: false
        }
      }
    ];
  }
};

export const EpicGetMembershipProductDiscounts: Epic<any, any> = EpicUtils.Create(request);
