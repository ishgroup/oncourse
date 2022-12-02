/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { PaymentInvoice, PaymentMethod, PaymentOut, PayType } from "@api/model";

export interface PaymentInvoiceModel extends PaymentInvoice {
  payable?: boolean;
  outstanding?: number;
}

export interface PaymentOutModel extends PaymentOut {
  invoices?: PaymentInvoiceModel[];
  selectedPaymentMethod?: PayType;
}

export interface PaymentOutState {
  paymentOutMethods?: PaymentMethod[];
  refundablePayments?: any;
}
