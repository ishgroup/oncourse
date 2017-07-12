import {VoucherPayment} from "./VoucherPayment";

export class Amount {
  owing?: number;
  total?: number;
  discount?: number;
  payNow?: number;
  voucherPayments?: VoucherPayment[];
  minPayNow?: number;
  isEditable?: boolean;
}

