import { Course, CourseClass } from "@api/model";

export interface ContraInvoice {
  id: number;
  includeInPayment: boolean;
  dueDate: string;
  invoiceNumber: number;
  owing: number;
  toBePaid: number;
}

export interface ContraInvoiceFormData {
  id: number;
  contactId: number;
  contactName: string;
  amountTotal: number;
  amountLeft: number;
  contraInvoices: ContraInvoice[];
}

export interface InvoicesState {
  selectedLineCourse: Course;
  selectedLineCourseClasses: CourseClass[];
  selectedLineEnrolments: string[];
  selectedInvoiceAmountOwing: number;
  selectedContact: any;
  contraInvoices: ContraInvoice[];
  defaultTerms: number;
}
