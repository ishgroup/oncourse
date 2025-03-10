/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CheckoutMembershipProduct } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import MembershipProductService
  from "../../../../containers/entities/membershipProducts/services/MembershipProductService";
import { CHECKOUT_GET_ITEM_MEMBERSHIP, CHECKOUT_GET_ITEM_MEMBERSHIP_FULFILLED } from "../../actions/chekoutItem";
import { CHECKOUT_ITEM_EDIT_VIEW_FORM } from "../../components/items/components/CkecoutItemViewForm";

const request: EpicUtils.Request<CheckoutMembershipProduct, { id: number }> = {
  type: CHECKOUT_GET_ITEM_MEMBERSHIP,
  getData: ({ id }, s) => MembershipProductService.getCheckoutModel(id, s.checkout.summary.list.find(c => c.payer)?.contact.id),
  processData: memberShipProduct => {
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
