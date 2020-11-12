import { Invoice, InvoiceInvoiceLine } from "@api/model";

export interface InvoiceWithTotalLine extends Invoice {
  invoiceLines?: InvoiceLineWithTotal[];
}

export interface InvoiceLineWithTotal extends InvoiceInvoiceLine {
  total?: number;
}
