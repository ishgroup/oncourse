/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_VOUCHER_PRODUCT_ITEM, GET_VOUCHER_PRODUCT_ITEM_FULFILLED } from "../actions/index";
import { VoucherProduct } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import voucherProductService from "../services/VoucherProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_VOUCHER_PRODUCT_ITEM,
  getData: (id: number) => voucherProductService.getVoucherProduct(id),
  processData: (voucherProduct: VoucherProduct) => {
    return [
      {
        type: GET_VOUCHER_PRODUCT_ITEM_FULFILLED,
        payload: { voucherProduct }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: voucherProduct, name: voucherProduct.name }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, voucherProduct)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetVoucherProduct: Epic<any, any> = EpicUtils.Create(request);
