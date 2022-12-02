import { combineEpics } from "redux-observable";
import { EpicGetInvoiceLineCourse } from "./EpicGetInvoiceLineCourse";
import { EpicGetInvoiceLineEnrolments } from "./EpicGetInvoiceLineEnrolments";
import { EpicGetContraInvoices } from "./EpicGetContraInvoices";
import { EpicPostContraInvoices } from "./EpicPostContraInvoices";
import { EpicDuplicateAndReverseInvoice } from "./EpicDuplicateAndReverseInvoice";
import { EpicGetAmountOwing } from "./EpicGetAmountOwing";
import { EpicGetDefaultInvoiceTerms } from "./EpicGetDefaultInvoiceTerms";
import { EpicDuplicateQuote } from "./EpicDuplicateQuote";

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