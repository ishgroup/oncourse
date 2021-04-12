/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../common/actions/CommonPlainRecordsActions";
import {
 CheckoutDiscount, CheckoutItem, CheckoutSummary, CheckoutSummaryListItem
} from "../../../model/checkout";

export const CHECKOUT_GET_DISCOUNT_PROMO = "checkout/get/promocode";
export const CHECKOUT_GET_VOUCHER_PROMO = "checkout/get/voucherPromo";
export const CHECKOUT_SET_PROMO = "checkout/set/promo";
export const CHECKOUT_UPDATE_PROMO = "checkout/update/promo";

export const CHECKOUT_REMOVE_VOUCHER_PROMO = "checkout/remove/voucher/promocode";
export const CHECKOUT_REMOVE_PROMOTIONAL_CODE = "checkout/remove/promotional/code";

export const CHECKOUT_GET_PREVIOUS_CREDIT = _toRequestType("checkout/get/previous/credit");
export const CHECKOUT_SET_PREVIOUS_CREDIT = "checkout/set/previous/credit";

export const CHECKOUT_GET_PREVIOUS_OWING = _toRequestType("checkout/get/previous/owing");
export const CHECKOUT_SET_PREVIOUS_OWING = "checkout/set/previous/owing";

export const CHECKOUT_TOGGLE_PREVIOUS_INVOICES = "checkout/toggle/previous/invoices";
export const CHECKOUT_UNCHECK_ALL_PREVIOUS_INVOICES = "checkout/uncheck/all/previous/invoices";

export const CHECKOUT_SET_DEFAULT_PAYER = "checkout/set/default/payer";

export const CHECKOUT_CHANGE_SUMMARY_ITEM_QUANTITY = "checkout/change/summary/item/quantiy";
export const CHECKOUT_CHANGE_SUMMARY_ITEM_FIELD = "checkout/change/summary/item/field";

export const CHECKOUT_UPDATE_SUMMARY_PRICES = "checkout/update/summary/price";
export const CHECKOUT_TRIGGER_UPDATE_SUMMARY_PRICES = "checkout/trigger/update/summary/price";
export const CHECKOUT_UPDATE_SUMMARY_PRICES_FULFILLED = FULFILLED(CHECKOUT_UPDATE_SUMMARY_PRICES);

export const CHECKOUT_GET_VOUCHER_REDEEMER = "checkout/get/voucher/redeemer";
export const CHECKOUT_UNCHECK_SUMMARY_ITEMS = "checkout/uncheck/summary/item";
export const CHECKOUT_UPDATE_SUMMARY_ITEM = "checkout/update/summary/item";
export const CHECKOUT_UPDATE_SUMMARY_ITEMS = "checkout/update/summary/items";
export const CHECKOUT_UPDATE_SUMMARY_LIST_ITEMS = "checkout/update/summary/listItems";
export const CHECKOUT_UPDATE_SUMMARY_FIELD = "checkout/update/summary/field";
export const CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS = "checkout/update/summary/classes/discounts";
export const CHECKOUT_SET_DISABLE_DISCOUNTS = "set/checkout/disable/summary/classes/discounts";

export const checkoutGetVoucherRedeemer = (id: number, vouchersItem) => ({
  type: CHECKOUT_GET_VOUCHER_REDEEMER,
  payload: { id, vouchersItem }
});

export const checkoutGetPromoCode = code => ({
  type: CHECKOUT_GET_DISCOUNT_PROMO,
  payload: { code }
});

export const checkoutGetVoucherPromo = code => ({
  type: CHECKOUT_GET_VOUCHER_PROMO,
  payload: { code }
});

export const checkoutSetPreviousCredit = (items: any[]) => ({
  type: CHECKOUT_SET_PREVIOUS_CREDIT,
  payload: { items }
});

export const checkoutTogglePreviousInvoice = (itemIndex: number, type: string) => ({
  type: CHECKOUT_TOGGLE_PREVIOUS_INVOICES,
  payload: { itemIndex, type }
});

export const checkoutUncheckAllPreviousInvoice = (type: string, value: boolean) => ({
  type: CHECKOUT_UNCHECK_ALL_PREVIOUS_INVOICES,
  payload: { type, value }
});

export const checkoutSetPreviousOwing = (items: any[]) => ({
  type: CHECKOUT_SET_PREVIOUS_OWING,
  payload: { items }
});

export const checkoutSetDefaultPayer = (payerIndex: number) => ({
  type: CHECKOUT_SET_DEFAULT_PAYER,
  payload: { payerIndex }
});

export const checkoutRemoveVoucher = (voucherIndex: number) => ({
  type: CHECKOUT_REMOVE_VOUCHER_PROMO,
  payload: { voucherIndex }
});

export const checkoutRemoveDiscount = (discountIndex: number) => ({
  type: CHECKOUT_REMOVE_PROMOTIONAL_CODE,
  payload: { discountIndex }
});

export const changeSummaryItemQuantity = (listIndex: number, itemIndex: number, quantity: number) => ({
  type: CHECKOUT_CHANGE_SUMMARY_ITEM_QUANTITY,
  payload: { listIndex, itemIndex, quantity }
});

export const checkoutChangeSummaryItemField = (listIndex: number, itemIndex: number, value: any, field: string) => ({
  type: CHECKOUT_CHANGE_SUMMARY_ITEM_FIELD,
  payload: {
   listIndex, itemIndex, value, field
  }
});

export const checkoutUpdateSummaryItem = (listIndex: number, item: CheckoutItem) => ({
  type: CHECKOUT_UPDATE_SUMMARY_ITEM,
  payload: { listIndex, item }
});

export const checkoutUpdateSummaryItems = (items: {listIndex: number, itemIndex: number, item: CheckoutItem}[]) => ({
  type: CHECKOUT_UPDATE_SUMMARY_ITEMS,
  payload: { items }
});

export const checkoutUpdateSummaryListItems = (items: {listIndex: number, item: CheckoutSummaryListItem}[]) => ({
  type: CHECKOUT_UPDATE_SUMMARY_LIST_ITEMS,
  payload: { items }
});

export const checkoutUpdateSummaryClassesDiscounts = (forcePricesUpdate?: boolean) => ({
  type: CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS,
  payload: forcePricesUpdate
});

export const checkoutUpdateSummaryField = (field: keyof CheckoutSummary, value: any) => ({
  type: CHECKOUT_UPDATE_SUMMARY_FIELD,
  payload: { field, value }
});

export const checkoutUpdateSummaryPrices = () => ({
  type: CHECKOUT_TRIGGER_UPDATE_SUMMARY_PRICES
});

export const checkoutSetDisableDiscounts = (disable: boolean) => ({
  type: CHECKOUT_SET_DISABLE_DISCOUNTS,
  payload: disable
});

interface ItemToUncheck {
  nodeId: number;
  itemId: number;
}

export const checkoutUncheckSummaryItems = (items: ItemToUncheck[]) => ({
  type: CHECKOUT_UNCHECK_SUMMARY_ITEMS,
  payload: items
});

export const checkoutGetPreviousOwing = id => ({
  type: CHECKOUT_GET_PREVIOUS_OWING,
  payload: id
});

export const checkoutGetPreviousCredit = id => ({
  type: CHECKOUT_GET_PREVIOUS_CREDIT,
  payload: id
});

export const checkoutUpdatePromo = ({ discountItem, vouchersItem }: { discountItem?: CheckoutDiscount, vouchersItem?: CheckoutDiscount }) => ({
  type: CHECKOUT_UPDATE_PROMO,
  payload: { discountItem, vouchersItem }
});

