/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export interface BatchPaymentInvoice {
  id: string;
  amountOwing: number;
  dateDue: string;
  checked: boolean;
  invoiceNumber: string;
}

export interface BatchPaymentContact {
  id: number;
  hasStoredCard: boolean;
  index: number;
  name: string,
  checked: boolean;
  total: number;
  processed: boolean;
  processing: boolean;
  error: boolean;
  items: BatchPaymentInvoice[];
}
