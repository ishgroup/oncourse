import {VoucherPayment} from "./../checkout/VoucherPayment";

export class Amount {
  owing?: number;
  total?: number;
  discount?: number;
  payNow?: number;
  minPayNow?: number;
  isEditable?: boolean;
  voucherPayments?: VoucherPayment[];
  payNowVisibility?: boolean = true;
}

