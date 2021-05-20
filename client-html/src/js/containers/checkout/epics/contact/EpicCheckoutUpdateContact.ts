/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Contact } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { updateEntityItemById } from "../../../entities/common/entityItemsService";
import {
  CHECKOUT_GET_CONTACT,
  CHECKOUT_UPDATE_CONTACT,
  CHECKOUT_UPDATE_CONTACT_FULFILLED
} from "../../actions/checkoutContact";
import { CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME } from "../../components/contact/CheckoutContactEditView";

const request: EpicUtils.Request<any, { id: number; contact: Contact & { notes: any }; message?: string }> = {
  type: CHECKOUT_UPDATE_CONTACT,
  getData: ({ id, contact }) => {
    delete contact.notes;
    return updateEntityItemById("Contact", id, contact);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id, message }) => [
    {
      type: CHECKOUT_UPDATE_CONTACT_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: message || "Contact was updated" }
    },
    {
      type: CHECKOUT_GET_CONTACT,
      payload: id
    }
  ],
  processError: (response, { contact }) => [
      ...FetchErrorHandler(response, "Contact was not updated"),
      initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, contact)
    ]
};

export const EpicCheckoutUpdateContact: Epic<any, any> = EpicUtils.Create(request);
