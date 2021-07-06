import {VoucherPayment} from "./../checkout/VoucherPayment";

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
   * aclyal amount allocated for invoice/invoices (equalse subTotal if payment plan invoices not presented)
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
   * voucher product payments list - appears if redeem any kind of vouchers on purchase
   */
  voucherProductPayments?: VoucherPayment[];

  /**
   * credit amount that will be used to pay for current invoice/invoices (students credit notes)
   */
  credit?: number;

  /**
   * amount of CC payment
   */
  ccPayment?: number;

  /**
   * outstanding owing amount
   */
  owing?: number;
}

