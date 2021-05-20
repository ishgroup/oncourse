/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_SALES, GET_SALES_FULFILLED } from "../actions";
import { DataResponse, SaleType } from "@api/model";

const withType = (type: string) => {
  switch (type) {
    case "ArticleProduct":
      return SaleType.Product;
    case "MembershipProduct":
      return SaleType.Membership;
    case "VoucherProduct":
      return SaleType.Voucher;
    default: {
      const message = "Unrecognized Entity type";

      console.error(message);

      throw Error(message);
    }
  }
};

const comparator = (a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1);

const request: EpicUtils.Request<any, { search: string; entities: SaleType[] }> = {
  type: GET_SALES,
  hideLoadIndicator: true,
  getData: payload => {
    const def = {
      columns: "name,sku,type",
      search: payload.search
    };

    const requestDetails = {
      Product: {
        entity: "ArticleProduct",
        ...def
      },
      Membership: {
        entity: "MembershipProduct",
        ...def
      },
      Voucher: {
        entity: "VoucherProduct",
        ...def
      }
    };

    return payload.entities
      .map(entity => {
        return EntityService.getPlainRecords(
          requestDetails[entity]["entity"],
          requestDetails[entity]["columns"],
          requestDetails[entity]["search"]
        );
      })
      .reduce((chain, task) => {
        return chain.then(result => task.then(current => [...result, current]));
      }, Promise.resolve([]));
  },

  processData: (response: DataResponse[]) => {
    const items = response
      .map(value => {
        const saleType = withType(value.entity);
        const group = value.rows.map(({ id, values }) => ({
          id: Number(id),
          name: values[0],
          code: values[1],
          type: saleType,
          active: true
        }));
        group.sort(comparator);
        return group;
      })
      .reduce((previousValue, currentValue) => previousValue.concat(currentValue));
    return [
      {
        type: GET_SALES_FULFILLED,
        payload: { items }
      }
    ];
  }
};

export const EpicGetSales: Epic<any, any> = EpicUtils.Create(request);
