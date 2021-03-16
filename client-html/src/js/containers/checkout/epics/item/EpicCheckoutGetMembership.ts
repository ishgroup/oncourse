/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { CheckoutItem } from "../../../../model/checkout";
import { getEntityItemById } from "../../../entities/common/entityItemsService";
import { CHECKOUT_GET_ITEM_MEMBERSHIP, CHECKOUT_GET_ITEM_MEMBERSHIP_FULFILLED } from "../../actions/chekoutItem";
import { CHECKOUT_ITEM_EDIT_VIEW_FORM } from "../../components/items/components/CkecoutItemViewForm";

const request: EpicUtils.Request<any, CheckoutItem> = {
  type: CHECKOUT_GET_ITEM_MEMBERSHIP,
  getData: ({ id }) => getEntityItemById("MembershipProduct", id),
  processData: (memberShipProduct: any, s, item) => {
    memberShipProduct.validTo = item.validTo;
    memberShipProduct.expiryType = item.expiryType;

    return [
      {
        type: CHECKOUT_GET_ITEM_MEMBERSHIP_FULFILLED,
        payload: { editRecord: memberShipProduct }
      },
      initialize(CHECKOUT_ITEM_EDIT_VIEW_FORM, memberShipProduct)
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(CHECKOUT_ITEM_EDIT_VIEW_FORM, null)]
};

export const EpicCheckoutGetMemberShip: Epic<any, any> = EpicUtils.Create(request);
