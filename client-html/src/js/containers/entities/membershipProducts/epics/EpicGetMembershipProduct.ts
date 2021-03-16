/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_MEMBERSHIP_PRODUCT_ITEM, GET_MEMBERSHIP_PRODUCT_ITEM_FULFILLED } from "../actions/index";
import { MembershipProduct } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import membershipProductService from "../services/MembershipProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_MEMBERSHIP_PRODUCT_ITEM,
  getData: (id: number) => membershipProductService.getMembershipProduct(id),
  processData: (membershipProduct: MembershipProduct) => {
    return [
      {
        type: GET_MEMBERSHIP_PRODUCT_ITEM_FULFILLED,
        payload: { membershipProduct }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: membershipProduct, name: membershipProduct.name }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, membershipProduct)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetMembershipProduct: Epic<any, any> = EpicUtils.Create(request);
