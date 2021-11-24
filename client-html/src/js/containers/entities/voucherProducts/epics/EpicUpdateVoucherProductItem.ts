/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Note, VoucherProduct } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {
  GET_VOUCHER_PRODUCT_ITEM,
  UPDATE_VOUCHER_PRODUCT_ITEM,
  UPDATE_VOUCHER_PRODUCT_ITEM_FULFILLED
} from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import voucherProductService from "../services/VoucherProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { processCustomFields } from "../../customFieldTypes/utils";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";

const request: EpicUtils.Request<any, { id: number; voucherProduct: VoucherProduct & { notes: Note[] } }> = {
  type: UPDATE_VOUCHER_PRODUCT_ITEM,
  getData: ({ id, voucherProduct }) => {
    delete voucherProduct.notes;
    processCustomFields(voucherProduct);
    return voucherProductService.updateVoucherProduct(id, voucherProduct);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_VOUCHER_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Voucher Product Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "VoucherProduct", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_VOUCHER_PRODUCT_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { voucherProduct }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, voucherProduct)]
};

export const EpicUpdateVoucherProductItem: Epic<any, any> = EpicUtils.Create(request);
