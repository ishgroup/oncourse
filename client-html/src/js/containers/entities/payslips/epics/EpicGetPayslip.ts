/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PAYSLIP_ITEM, GET_PAYSLIP_ITEM_FULFILLED } from "../actions/index";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Payslip } from "@api/model";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_PAYSLIP_ITEM,
  getData: (id: number) => getEntityItemById("Payslip", id),
  processData: (payslip: Payslip) => {
    return [
      {
        type: GET_PAYSLIP_ITEM_FULFILLED,
        payload: { payslip }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: payslip, name: payslip.tutorFullName }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, payslip)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetPayslip: Epic<any, any> = EpicUtils.Create(request);
