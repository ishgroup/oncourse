/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Payslip } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { UPDATE_PAYSLIP_ITEM, UPDATE_PAYSLIP_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getEntityRecord } from "../../common/actions";

const request: EpicUtils.Request<any, { id: number; payslip: Payslip }> = {
  type: UPDATE_PAYSLIP_ITEM,
  getData: ({ id, payslip }) => updateEntityItemById("Payslip", id, payslip),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_PAYSLIP_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [
        getEntityRecord(id, "Payslip")
      ] : []
    ],
  processError: (response, { payslip }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, payslip)]
};

export const EpicUpdatePayslipItem: Epic<any, any> = EpicUtils.Create(request);