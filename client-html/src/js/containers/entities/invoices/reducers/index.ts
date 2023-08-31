import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_AMOUNT_OWING_FULFILLED,
  GET_DEFAULT_INVOICE_TERMS_FULFILLED,
  SET_CONTRA_INVOICES,
  SET_INVOICE_LINE_COURSE,
  SET_INVOICE_LINE_ENROLMENTS,
  SET_SELECTED_CONTACT
} from "../actions";
import { InvoicesState } from "./state";

const initial: InvoicesState = {
  selectedLineCourse: null,
  selectedLineCourseClasses: [],
  selectedLineEnrolments: null,
  selectedInvoiceAmountOwing: null,
  selectedContact: null,
  contraInvoices: null,
  defaultTerms: 0
};

export const invoicesReducer = (state: InvoicesState = initial, action: IAction<any>): InvoicesState => {
  switch (action.type) {
    case SET_SELECTED_CONTACT:
    case SET_CONTRA_INVOICES:
    case SET_INVOICE_LINE_ENROLMENTS:
    case SET_INVOICE_LINE_COURSE:
    case GET_AMOUNT_OWING_FULFILLED:
    case GET_DEFAULT_INVOICE_TERMS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
