/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { change } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { CheckoutDiscount } from "../../../../model/checkout";
import {
  CHECKOUT_GET_VOUCHER_PROMO,
  CHECKOUT_SET_PROMO,
  checkoutGetVoucherRedeemer,
  checkoutSetDefaultPayer
} from "../../actions/checkoutSummary";
import { FORM } from "../../components/CheckoutSelection";
import { isPromotionalCodeExist } from "../../utils";

const chekcoutVoucherCodeColumns = "product.name,"
  + "code,"
  + "expiryDate,"
  + "redemptionValue,"
  + "valueOnPurchase,"
  + "product.maxCoursesRedemption,"
  + "redeemedCourseCount,"
  + "createdOn,"
  + "product.id,"
  + "redeemableBy.id,"
  + "status";

const defaultVoucherColunmsMap = ({ id, values }): CheckoutDiscount => ({
  id: Number(id),
  type: "voucher",
  name: values[0],
  code: values[1],
  expiryDate: values[2],
  value: parseFloat(values[3]),
  purchaseValue: parseFloat(values[4]),
  maxCoursesRedemption: values[5] ? parseFloat(values[5]) : values[5],
  redeemedCourses: parseFloat(values[6]),
  purchaseDate: values[7],
  productId: parseFloat(values[8]),
  courseIds: [],
  appliedValue: 0,
  availableValue: parseFloat(values[3]),
  redeemableById: Number(values[9]),
  statusValue: values[10]
});

const request: EpicUtils.Request = {
  type: CHECKOUT_GET_VOUCHER_PROMO,
  getData: ({ code }, { checkout }) => (isPromotionalCodeExist(code, checkout)
    ? Promise.reject({ message: "This code was already added." })
    : EntityService.getPlainRecords(
      "Voucher",
      chekcoutVoucherCodeColumns,
      `code is "${code}"`,
      null,
      0,
      "",
      true
    ).then(vouchers => {
    const vouchersItem = vouchers.rows.map(v => defaultVoucherColunmsMap(v as any))[0];

    if (!vouchersItem || !["Redeemed", "Expired", "Active"].includes(vouchersItem.statusValue)) {
      throw { message: "The code you have entered was incorrect or not available" };
    }

    if (vouchersItem.statusValue === "Redeemed") {
      throw { message: "The voucher code you have entered has already been redeemed" };
    }

    if (vouchersItem.statusValue === "Expired") {
      throw { message: "The voucher code you have entered has expired and cannot be used" };
    }

    if (vouchersItem.redeemableById && checkout.summary.vouchers.some(sv => sv.redeemableById)) {
      throw { message: "Can only have one payer with redeemable voucher." };
    }

    return EntityService.getPlainRecords(
      "VoucherPaymentIn",
      "createdOn,paymentIn.amount",
      `voucher.id is ${vouchersItem.id}`
    ).then(res => {
      vouchersItem.history = res.rows.map(({ values }) => ({
        createdOn: values[0],
        amount: parseFloat(values[1])
      }));

      return typeof vouchersItem.maxCoursesRedemption === "number"
        ? EntityService.getPlainRecords(
        "VoucherProductCourse",
          "course.id",
          `voucherProduct.id is ${vouchersItem.productId}`
        ).then(productCourse => {
          vouchersItem.courseIds = productCourse.rows.map(({ values }) => Number(values[0]));
          return vouchersItem;
          })
        : vouchersItem;
    });
  })),
  processData: (vouchersItem, { checkout }) => {
    const actions = [];

    if (vouchersItem) {
      actions.push(change(FORM, "vouchers", ""));
      if (vouchersItem.redeemableById) {
        const payerIndex = checkout.summary.list.findIndex(li => li.contact.id === vouchersItem.redeemableById);

        if (payerIndex === -1) {
          actions.push(checkoutGetVoucherRedeemer(vouchersItem.redeemableById, vouchersItem));
          return actions;
        }

        actions.push(checkoutSetDefaultPayer(payerIndex));
      }
    }

    return [
      {
        type: CHECKOUT_SET_PROMO,
        payload: { vouchersItem }
      },
      ...actions
    ];
  },
  processError: response => FetchErrorHandler(response, response.message ? response.message : "")
};

export const EpicCheckoutGetVoucherToRedeem: Epic<any, any> = EpicUtils.Create(request);
