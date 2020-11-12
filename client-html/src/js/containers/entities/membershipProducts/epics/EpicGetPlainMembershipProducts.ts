/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { MembershipProduct } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PLAIN_MEMBERSHIP_PRODUCTS, GET_PLAIN_MEMBERSHIP_PRODUCTS_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultMembershipProductMap = ({ id, values }) => ({
  id: Number(id),
  code: values[0],
  name: values[1],
  price: values[2]
});

const request: EpicUtils.Request<any, any, { offset?: number, columns?: string, ascending?: boolean, pageSize?: number }> = {
  type: GET_PLAIN_MEMBERSHIP_PRODUCTS,
  hideLoadIndicator: true,
  getData({
 offset, columns, ascending, pageSize
}, { membershipProducts: { search } }) {
    return EntityService.getPlainRecords(
      "MembershipProduct",
      columns || "sku,name,price_with_tax",
      search,
      pageSize || 100,
      offset,
      "",
      ascending
    );
  },
  processData({ rows, offset, pageSize }, s, { columns }) {
    const items: MembershipProduct[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultMembershipProductMap);
    return [
      {
        type: GET_PLAIN_MEMBERSHIP_PRODUCTS_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetPlainMembershipProducts: Epic<any, any> = EpicUtils.Create(request);
