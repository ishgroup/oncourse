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
  CHECKOUT_GET_DISCOUNT_PROMO,
  CHECKOUT_SET_PROMO,
  checkoutUpdateSummaryClassesDiscounts
} from "../../actions/checkoutSummary";
import { CHECKOUT_SELECTION_FORM_NAME as checkoutDiscountForm } from "../../components/CheckoutSelection";
import { isPromotionalCodeExist } from "../../utils";

const checkoutPromocodeColumns = "id,"
  + "name,"
  + "code,"
  + "discountType,"
  + "rounding,"
  + "discountDollar,"
  + "discountPercent,"
  + "validFrom,"
  + "validTo";

const defaultDiscountColunmsMap = ({ values }): CheckoutDiscount => ({
  id: Number(values[0]),
  name: values[1],
  code: values[2],
  discountType: values[3],
  rounding: values[4],
  discountValue: values[5],
  discountPercent: values[6],
  validFrom: values[7],
  validTo: values[8],
  type: "discount"
});

const request: EpicUtils.Request = {
  type: CHECKOUT_GET_DISCOUNT_PROMO,
  getData: ({ code }, { checkout }) => (isPromotionalCodeExist(code, checkout)
      ? Promise.reject({ data: { message: "This code was already added." } })
      : EntityService.getPlainRecords(
          "Discount",
          checkoutPromocodeColumns,
          `code is "${code}" and availableFor not is ONLINE_ONLY and (((validTo >= today) or (validTo == null)) and ((validFrom <= today) or (validFrom == null))) `,
          100,
          0,
          "",
          true
        )),
  processData: discounts => {
    const discountItem = discounts.rows.length ? discounts.rows.map(d => defaultDiscountColunmsMap(d))[0] : null;
    const clearFieldAction = [];
    if (discountItem) {
      clearFieldAction.push(change(checkoutDiscountForm, "promocodes", ""));
    }

    return [
      {
        type: CHECKOUT_SET_PROMO,
        payload: { discountItem }
      },
      checkoutUpdateSummaryClassesDiscounts(),
      ...clearFieldAction
    ];
  },
  processError: response => [change(checkoutDiscountForm, "promocodes", ""),
    ...FetchErrorHandler(response, response && response.data?.message ? response.data.message : "Failed to get discounts by code")]
};

export const EpicCheckoutGetPromoCode: Epic<any, any> = EpicUtils.Create(request);
