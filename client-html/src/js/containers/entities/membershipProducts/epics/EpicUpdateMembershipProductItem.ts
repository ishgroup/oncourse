/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { MembershipProduct, Note } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {
  UPDATE_MEMBERSHIP_PRODUCT_ITEM,
  UPDATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED
} from "../actions";
import { clearActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import membershipProductService from "../services/MembershipProductService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { processCustomFields } from "../../customFieldTypes/utils";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import { getEntityRecord } from "../../common/actions";

const request: EpicUtils.Request<any, { id: number; membershipProduct: MembershipProduct & { notes: Note[] } }> = {
  type: UPDATE_MEMBERSHIP_PRODUCT_ITEM,
  getData: ({ id, membershipProduct }) => {
    delete membershipProduct.notes;
    processCustomFields(membershipProduct);
    return membershipProductService.updateMembershipProduct(id, membershipProduct);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
    ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
      {
        type: UPDATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Membership Product Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "MembershipProduct", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [
        getEntityRecord(id, "MembershipProduct")
      ] : []
    ],
  processError: (response, { membershipProduct }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, membershipProduct)]
};

export const EpicUpdateMembershipProductItem: Epic<any, any> = EpicUtils.Create(request);