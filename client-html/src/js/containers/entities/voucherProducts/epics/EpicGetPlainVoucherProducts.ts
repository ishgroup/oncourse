/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { VoucherProduct } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PLAIN_VOUCHER_PRODUCTS, GET_PLAIN_VOUCHER_PRODUCTS_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultVoucherProductMap = ({ id, values }) => ({
  id: Number(id),
  code: values[0],
  name: values[1],
  price: values[2]
});

const request: EpicUtils.Request<any, any, { offset?: number, columns?: string, ascending?: boolean, pageSize?: number }> = {
  type: GET_PLAIN_VOUCHER_PRODUCTS,
  hideLoadIndicator: true,
  getData({
 offset, columns, ascending, pageSize
}, { voucherProducts: { search } }) {
    return EntityService.getPlainRecords(
      "VoucherProduct",
      columns || "sku,name,priceExTax",
      search,
      pageSize || 100,
      offset,
      "",
      ascending
    );
  },
  processData({ rows, offset, pageSize }, s, { columns }) {
    const items: VoucherProduct[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultVoucherProductMap);
    return [
      {
        type: GET_PLAIN_VOUCHER_PRODUCTS_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetPlainVoucherProducts: Epic<any, any> = EpicUtils.Create(request);
