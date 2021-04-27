/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import ContactsService from "../services/ContactsService";
import { POST_MERGE_CONTACTS, POST_MERGE_CONTACTS_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { MergeRequest } from "@api/model";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { openContactLink } from "../utils";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";

const request: EpicUtils.Request<any, MergeRequest> = {
  type: POST_MERGE_CONTACTS,
  getData: request => ContactsService.merge(request),
  processData: contactId => {
    setTimeout(() => openContactLink(contactId), 2000);

    return [
      {
        type: POST_MERGE_CONTACTS_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contacts merged successfully" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Contact", listUpdate: true, savedID: contactId }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to merge contacts")
};
export const EpicPostMergeContacts: Epic<any, any> = EpicUtils.Create(request);
