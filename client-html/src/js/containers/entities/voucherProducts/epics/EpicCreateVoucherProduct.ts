/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_VOUCHER_PRODUCT_ITEM, CREATE_VOUCHER_PRODUCT_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { VoucherProduct } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import voucherProductService from "../services/VoucherProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: VoucherProduct;

const request: EpicUtils.Request = {
  type: CREATE_VOUCHER_PRODUCT_ITEM,
  getData: payload => {
    savedItem = payload.voucherProduct;
    return voucherProductService.createVoucherProduct(payload.voucherProduct);
  },
  processData: () => {
    return [
      {
        type: CREATE_VOUCHER_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Voucher Product Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "VoucherProduct" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Voucher Product Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateVoucherProduct: Epic<any, any> = EpicUtils.Create(request);
