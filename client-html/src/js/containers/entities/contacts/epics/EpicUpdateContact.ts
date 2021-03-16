/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Contact } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_CONTACT, UPDATE_CONTACT, UPDATE_CONTACT_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; contact: Contact & { notes: any }; message?: string }> = {
  type: UPDATE_CONTACT,
  getData: ({ id, contact }) => {
    delete contact.notes;
    processCustomFields(contact);
    return updateEntityItemById("Contact", id, contact);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id, message }) => [
    {
      type: UPDATE_CONTACT_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: message || "Contact was updated" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Contact", listUpdate: true, savedID: id }
    },
    ...s.list.fullScreenEditView ? [{
      type: GET_CONTACT,
      payload: id
    }] : []
  ],
  processError: (response, { contact }) => [...FetchErrorHandler(response, "Contact was not updated"), initialize(LIST_EDIT_VIEW_FORM_NAME, contact)]
};

export const EpicUpdateContact: Epic<any, any> = EpicUtils.Create(request);
