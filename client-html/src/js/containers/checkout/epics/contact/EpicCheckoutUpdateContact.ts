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
import { clearActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import { updateEntityItemById } from "../../../entities/common/entityItemsService";
import {
  CHECKOUT_GET_CONTACT,
  CHECKOUT_UPDATE_CONTACT,
  CHECKOUT_UPDATE_CONTACT_FULFILLED
} from "../../actions/checkoutContact";
import { CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME } from "../../components/contact/CheckoutContactEditView";
import { getModifiedData } from "../../../../common/utils/common";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { updateEntityDocuments } from "../../../../common/components/form/documents/actions";

const request: EpicUtils.Request<any, { id: number; contact: Contact & { notes, documents }; message?: string }> = {
  type: CHECKOUT_UPDATE_CONTACT,
  getData: async ({ id, contact }, s) => {
    const documents = [...contact.documents];
    await updateEntityItemById("Contact", id, contact);
    await processNotesAsyncQueue(s.actionsQueue.queuedActions);
    return documents;
  },
  processData: (documents, s, { id, message }) => {
    const modifiedDocs = getModifiedData(s.form[LIST_EDIT_VIEW_FORM_NAME]?.initial.documents, documents);
    
    return [
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
      ...modifiedDocs ? [updateEntityDocuments("Contact", id, modifiedDocs.map(d => d.id))] : [],
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
    ];
  },
  processError: (response, { contact }) => [
      ...FetchErrorHandler(response, "Contact was not updated"),
      initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, contact)
    ]
};

export const EpicCheckoutUpdateContact: Epic<any, any> = EpicUtils.Create(request);