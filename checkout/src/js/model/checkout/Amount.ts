import { VoucherPayment } from './VoucherPayment';

export class Amount {
  /**
   * total price excluding discounts
   */
  total?: number;

  /**
   * total discount amount
   */
  discount?: number;

  /**
   * total price including discounts (discounted price)
   */
  subTotal?: number;

  /**
   * min amount that user need to pay for invoice/invoices (can be less than subTotal if payment plan invoices precented)
   */
  minPayNow?: number;

  /**
   * actual amount allocated for invoice/invoices (equalse subTotal if payment plan invoices not presented)
   */
  payNow?: number;

  /**
   * true if minPayNow less that subTotal
   */
  isEditable?: boolean;

  /**
   * voucher payments list - appears if redeem any kind of vouchers
   */
  voucherPayments?: VoucherPayment[];

  /**
   * credit amount that will be used to pay for current invoice/invoices (students credit notes)
   */
  credit?: number;

  /**
   * amount paid by card
   */
  ccPayment?: number;

  /**
   * outstanding owing amount
   */
  owing?: number;
}
