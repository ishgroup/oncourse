/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_DISCOUNTS, GET_DISCOUNTS_FULFILLED } from "../actions";
import { DataResponse, Discount } from "@api/model";

const request: EpicUtils.Request<any,string> = {
  type: GET_DISCOUNTS,
  getData: payload => {
    return EntityService.getPlainRecords(
      "Discount",
      "name,discountType,discountDollar,discountPercent",
      `${payload ? `${payload} and ` : ""}(validTo is null OR validTo > today)`
    );
  },
  processData: (response: DataResponse) => {
    const items = response.rows.map(({ id, values }) => ({
      id: Number(id),
      name: values[0],
      discountType: values[1],
      discountValue: values[2] ? Number(values[2]) : values[2],
      discountPercent: values[3] ? Number(values[3]) : values[3]
    }));

    items.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

    return [
      {
        type: GET_DISCOUNTS_FULFILLED,
        payload: { items, pending: false }
      }
    ];
  }
};

export const EpicGetDiscounts: Epic<any, any> = EpicUtils.Create(request);
