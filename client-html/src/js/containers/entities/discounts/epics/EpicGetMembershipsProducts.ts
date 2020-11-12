/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_DISCOUNT_MEMBERSHIPS, GET_DISCOUNT_MEMBERSHIPS_FULFILLED } from "../actions";
import { DataResponse } from "@api/model";

const request: EpicUtils.Request<any, any, string> = {
  type: GET_DISCOUNT_MEMBERSHIPS,
  getData: payload => {
    return EntityService.getPlainRecords("MembershipProduct", "name,sku", payload);
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: GET_DISCOUNT_MEMBERSHIPS_FULFILLED,
        payload: {
          membershipItems: response.rows
            .map(({ id, values }) => ({
              productId: Number(id),
              productName: values[0],
              productSku: values[1]
            }))
            .sort((a, b) => {
              if (a.productId < b.productId) {
                return -1;
              }
              if (a.productId > b.productId) {
                return 1;
              }
              return 0;
            }),
          membershipPending: false
        }
      }
    ];
  }
};

export const EpicGetMembershipsProducts: Epic<any, any> = EpicUtils.Create(request);
