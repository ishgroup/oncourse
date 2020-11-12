import { combineEpics } from "redux-observable";
import { EpicGetInvoice } from "./EpicGetInvoice";
import { EpicUpdateInvoiceItem } from "./EpicUpdateInvoiceItem";
import { EpicCreateInvoice } from "./EpicCreateInvoice";
import { EpicGetInvoiceLineCourse } from "./EpicGetInvoiceLineCourse";
import { EpicGetInvoiceLineEnrolments } from "./EpicGetInvoiceLineEnrolments";
import { EpicGetContraInvoices } from "./EpicGetContraInvoices";
import { EpicPostContraInvoices } from "./EpicPostContraInvoices";
import { EpicDuplicateAndReverseInvoice } from "./EpicDuplicateAndReverseInvoice";
import { EpicGetAmountOwing } from "./EpicGetAmountOwing";
import { EpicGetDefaultInvoiceTerms } from "./EpicGetDefaultInvoiceTerms";

export const EpicInvoice = combineEpics(
  EpicGetInvoice,
  EpicUpdateInvoiceItem,
  EpicCreateInvoice,
  EpicGetInvoiceLineCourse,
  EpicGetInvoiceLineEnrolments,
  EpicGetContraInvoices,
  EpicPostContraInvoices,
  EpicGetContraInvoices,
  EpicDuplicateAndReverseInvoice,
  EpicGetAmountOwing,
  EpicGetDefaultInvoiceTerms
);
