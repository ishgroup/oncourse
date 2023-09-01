/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../common/actions/IshAction";
import {
  CANCEL_ENROLMENT,
  SET_ENROLMENT_INVOICE_LINES,
  SET_ENROLMENTS_DIALOG,
  SET_ENROLMENTS_PROCESSING
} from "../actions";
import { EnrolmentsState } from "./state";

const initial: EnrolmentsState = {
  invoiceLines: [],
  dialogOpened: null,
  processing: false
};

export const enrolmentsReducer = (state: EnrolmentsState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case SET_ENROLMENTS_PROCESSING:
    case SET_ENROLMENTS_DIALOG:
    case SET_ENROLMENT_INVOICE_LINES: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CANCEL_ENROLMENT:
      return {
        ...state,
        processing: true
      };

    default:
      return state;
  }
};
