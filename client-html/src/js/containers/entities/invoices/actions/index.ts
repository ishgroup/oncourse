import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Course, CourseClass, Invoice } from "@api/model";
import { ContraInvoice, ContraInvoiceFormData } from "../reducers/state";

export const GET_INVOICE_ITEM = _toRequestType("get/invoice");
export const GET_INVOICE_ITEM_FULFILLED = FULFILLED(GET_INVOICE_ITEM);

export const GET_DEFAULT_INVOICE_TERMS = _toRequestType("get/invoice/contra");
export const GET_DEFAULT_INVOICE_TERMS_FULFILLED = FULFILLED(GET_DEFAULT_INVOICE_TERMS);

export const GET_CONTRA_INVOICES = _toRequestType("get/invoice/contra");
export const SET_CONTRA_INVOICES = "set/invoice/contra";

export const POST_CONTRA_INVOICES = _toRequestType("post/invoice/contra");
export const POST_CONTRA_INVOICES_FULFILLED = FULFILLED(POST_CONTRA_INVOICES);

export const GET_INVOICE_LINE_COURSE = _toRequestType("get/invoice/line/course");
export const SET_INVOICE_LINE_COURSE = "set/invoice/line/course";

export const GET_INVOICE_LINE_ENROLMENTS = _toRequestType("get/invoice/line/enrolments");
export const SET_INVOICE_LINE_ENROLMENTS = "set/invoice/line/enrolments";

export const DELETE_INVOICE_ITEM = _toRequestType("delete/invoice");

export const UPDATE_INVOICE_ITEM = _toRequestType("put/invoice");
export const UPDATE_INVOICE_ITEM_FULFILLED = FULFILLED(UPDATE_INVOICE_ITEM);

export const CREATE_INVOICE_ITEM = _toRequestType("post/invoice");
export const CREATE_INVOICE_ITEM_FULFILLED = FULFILLED(CREATE_INVOICE_ITEM);

export const DUPLICATE_AND_REVERSE_INVOICE_ITEM = _toRequestType("get/invoice/duplicate");

export const GET_AMOUNT_OWING = _toRequestType("get/invoice/amountOwing");
export const GET_AMOUNT_OWING_FULFILLED = FULFILLED(GET_AMOUNT_OWING);

export const SET_SELECTED_CONTACT = "set/invoice/selectedContact";

export const setSelectedContact = (selectedContact: any) => ({
  type: SET_SELECTED_CONTACT,
  payload: { selectedContact }
});

export const getDefaultInvoiceTerms = () => ({
  type: GET_DEFAULT_INVOICE_TERMS
});

export const getAmountOwing = (id: number) => ({
  type: GET_AMOUNT_OWING,
  payload: id
});

export const postContraInvoices = (id: number, invoicesToPay: number[]) => ({
  type: POST_CONTRA_INVOICES,
  payload: { id, invoicesToPay }
});

export const getContraInvoices = (invoiceToContra: ContraInvoiceFormData) => ({
  type: GET_CONTRA_INVOICES,
  payload: invoiceToContra
});

export const setContraInvoices = (contraInvoices: ContraInvoice[]) => ({
  type: SET_CONTRA_INVOICES,
  payload: { contraInvoices }
});

export const getInvoiceLineEnrolments = (courseClassId: number) => ({
  type: GET_INVOICE_LINE_ENROLMENTS,
  payload: courseClassId
});

export const getInvoiceLineCourse = (id: number) => ({
  type: GET_INVOICE_LINE_COURSE,
  payload: id
});

export const setInvoiceLineCourse = (selectedLineCourse: Course, selectedLineCourseClasses: CourseClass[]) => ({
  type: SET_INVOICE_LINE_COURSE,
  payload: { selectedLineCourse, selectedLineCourseClasses }
});

export const setInvoiceLineEnrolments = (selectedLineEnrolments: string[]) => ({
  type: SET_INVOICE_LINE_ENROLMENTS,
  payload: { selectedLineEnrolments }
});

export const getInvoice = (id: string) => ({
  type: GET_INVOICE_ITEM,
  payload: id
});

export const removeInvoice = (id: string) => ({
  type: DELETE_INVOICE_ITEM,
  payload: id
});

export const updateInvoice = (id: string, invoice: Invoice) => ({
  type: UPDATE_INVOICE_ITEM,
  payload: { id, invoice }
});

export const createInvoice = (invoice: Invoice) => ({
  type: CREATE_INVOICE_ITEM,
  payload: { invoice }
});

export const duplicateAndReverseInvoice = (id: number) => ({
  type: DUPLICATE_AND_REVERSE_INVOICE_ITEM,
  payload: id
});
