/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EnrolmentsState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import { SET_ENROLMENT_INVOICE_LINES, SET_ENROLMENT_TRANSFERED } from "../actions";

const initial: EnrolmentsState = {
  invoiceLines: [],
  isTransfered: false
};

export const enrolmentsReducer = (state: EnrolmentsState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case SET_ENROLMENT_INVOICE_LINES: {
      return {
        ...state,
        ...action.payload
      };
    }

    case SET_ENROLMENT_TRANSFERED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
