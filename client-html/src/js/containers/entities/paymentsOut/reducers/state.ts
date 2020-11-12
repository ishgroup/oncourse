import { PaymentInvoice, PaymentMethod, PaymentOut } from "@api/model";

export interface PaymentInvoiceModel extends PaymentInvoice {
  payable?: boolean;
  outstanding?: number;
}

export interface PaymentOutModel extends PaymentOut {
  invoices?: PaymentInvoiceModel[];
}

export interface PaymentOutState {
  paymentOutMethods?: PaymentMethod[];
  refundablePayments?: any;
  customValues?: any;
  addPaymentOutValues?: PaymentOutModel;
}
