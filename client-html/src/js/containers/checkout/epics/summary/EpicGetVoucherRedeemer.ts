/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import { showConfirm } from "../../../../common/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap, stubFunction } from "../../../../common/utils/common";
import { CheckoutDiscount } from "../../../../model/checkout";
import { getContactFullName } from "../../../entities/contacts/utils";
import { addContact } from "../../actions";
import { CHECKOUT_GET_VOUCHER_REDEEMER, CHECKOUT_SET_PROMO } from "../../actions/checkoutSummary";
import { CHECKOUT_CONTACT_COLUMNS } from "../../constants";
import store from "../../../../constants/Store";

const request: EpicUtils.Request<DataResponse, { id: number, vouchersItem: CheckoutDiscount }> = {
  type: CHECKOUT_GET_VOUCHER_REDEEMER,
  getData: ({ id }) => EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${id}`),
  processData: (res, s, { vouchersItem }) => {
    const contact = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0];
    const contactName = getContactFullName(contact);

    return [
      showConfirm({
        onConfirm: () => {
          store.dispatch(addContact(contact as any, true, false));
          store.dispatch({
            type: CHECKOUT_SET_PROMO,
            payload: { vouchersItem }
          });
        },
        title: null,
        confirmMessage: `The voucher you have chosen can only be redeemed by ${contactName}. Switch the payer to ${contactName} now?`,
        cancelButtonText: "Switch"
      })
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get voucher linked contact")
};

export const EpicGetVoucherRedeemer: Epic<any, any> = EpicUtils.Create(request);
