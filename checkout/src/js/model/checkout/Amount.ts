import {VoucherPayment} from "./../checkout/VoucherPayment";

export class Amount {

  /**
   * outstanding amount
   */
  owing?: number;

  /**
   * total price excluding discounts
   */
  total?: number;

  /**
   * total price including discounts (discounted price)
   */
  subTotal?: number;

  /**
   * total discount amount
   */
  discount?: number;

  /**
   * total amount allocated for payment/payments (equalse subTotal if payment plan invoices not presented)
   */
  payNow?: number;

  /**
   * min amount for cc payment (can be less than subTotal if payment plan invoices precented)
   */
  minPayNow?: number;

  /**
   * true if owing greate that zero
   */
  isEditable?: boolean;

  /**
   * voucher payments list - appears if redeem any kind of vouchers
   */
  voucherPayments?: VoucherPayment[];
}

