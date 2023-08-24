import { combineEpics } from "redux-observable";
import { EpicDuplicateAndReverseInvoice } from "./EpicDuplicateAndReverseInvoice";
import { EpicDuplicateQuote } from "./EpicDuplicateQuote";
import { EpicGetAmountOwing } from "./EpicGetAmountOwing";
import { EpicGetContraInvoices } from "./EpicGetContraInvoices";
import { EpicGetDefaultInvoiceTerms } from "./EpicGetDefaultInvoiceTerms";
import { EpicGetInvoiceLineCourse } from "./EpicGetInvoiceLineCourse";
import { EpicGetInvoiceLineEnrolments } from "./EpicGetInvoiceLineEnrolments";
import { EpicPostContraInvoices } from "./EpicPostContraInvoices";

export const EpicInvoice = combineEpics(
  EpicGetInvoiceLineCourse,
  EpicGetInvoiceLineEnrolments,
  EpicGetContraInvoices,
  EpicPostContraInvoices,
  EpicGetContraInvoices,
  EpicDuplicateAndReverseInvoice,
  EpicGetAmountOwing,
  EpicGetDefaultInvoiceTerms,
  EpicDuplicateQuote,
);