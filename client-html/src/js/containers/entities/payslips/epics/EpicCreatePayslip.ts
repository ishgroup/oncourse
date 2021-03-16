/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import PayslipService from "../services/PayslipService";
import { CREATE_PAYSLIP_ITEM, CREATE_PAYSLIP_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Payslip } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Payslip;

const request: EpicUtils.Request = {
  type: CREATE_PAYSLIP_ITEM,
  getData: payload => {
    savedItem = payload.payslip;
    return PayslipService.createPayslip(payload.payslip);
  },
  processData: () => {
    return [
      {
        type: CREATE_PAYSLIP_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Payslip Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreatePayslip: Epic<any, any> = EpicUtils.Create(request);
