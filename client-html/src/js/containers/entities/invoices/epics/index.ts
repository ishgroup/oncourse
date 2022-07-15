import { combineEpics } from "redux-observable";
import { EpicGetInvoice } from "./EpicGetInvoice";
import { EpicUpdateInvoiceItem } from "./EpicUpdateInvoiceItem";
import { EpicGetInvoiceLineCourse } from "./EpicGetInvoiceLineCourse";
import { EpicGetInvoiceLineEnrolments } from "./EpicGetInvoiceLineEnrolments";
import { EpicGetContraInvoices } from "./EpicGetContraInvoices";
import { EpicPostContraInvoices } from "./EpicPostContraInvoices";
import { EpicDuplicateAndReverseInvoice } from "./EpicDuplicateAndReverseInvoice";
import { EpicGetAmountOwing } from "./EpicGetAmountOwing";
import { EpicGetDefaultInvoiceTerms } from "./EpicGetDefaultInvoiceTerms";
import { EpicDeleteQuote } from "./EpicDeleteQuote";
import { EpicDuplicateQuote } from "./EpicDuplicateQuote";

export const EpicInvoice = combineEpics(
  EpicGetInvoice,
  EpicUpdateInvoiceItem,
  EpicGetInvoiceLineCourse,
  EpicGetInvoiceLineEnrolments,
  EpicGetContraInvoices,
  EpicPostContraInvoices,
  EpicGetContraInvoices,
  EpicDuplicateAndReverseInvoice,
  EpicGetAmountOwing,
  EpicGetDefaultInvoiceTerms,
  EpicDeleteQuote,
  EpicDuplicateQuote,
);
