import { Invoice, AbstractInvoiceLine } from "@api/model";

export interface InvoiceWithTotalLine extends Invoice {
  invoiceLines?: InvoiceLineWithTotal[];
}

export interface InvoiceLineWithTotal extends AbstractInvoiceLine {
  total?: number;
}