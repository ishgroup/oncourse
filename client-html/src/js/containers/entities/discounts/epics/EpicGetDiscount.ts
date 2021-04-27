/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { Discount } from "@api/model";
import { GET_DISCOUNT_ITEM, GET_DISCOUNT_ITEM_FULFILLED } from "../actions";
import { getEntityItemById } from "../../common/entityItemsService";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { initialize } from "redux-form";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<Discount, number> = {
  type: GET_DISCOUNT_ITEM,
  getData: (id: number) => getEntityItemById("Discount", id),
  processData: (discount: Discount) => {
    discount.discountMemberships.forEach(el => {
      el.contactRelations = el.contactRelations.sort();
    });
    return [
      {
        type: GET_DISCOUNT_ITEM_FULFILLED
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: discount, name: discount.name }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, discount)
    ];
  }
};

export const EpicGetDiscount: Epic<any, any> = EpicUtils.Create(request);
