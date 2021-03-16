/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import ContactsService from "../services/ContactsService";
import { GET_MERGE_CONTACTS, GET_MERGE_CONTACTS_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, { contactA: string; contactB: string }> = {
  type: GET_MERGE_CONTACTS,
  getData: ({ contactA, contactB }) => ContactsService.getMergeData(contactA, contactB),
  processData: (mergeData, s, { contactA, contactB }) => [
    {
      type: GET_MERGE_CONTACTS_FULFILLED
    },
    initialize("MergeContactsForm", {
      mergeData,
      mergeRequest: {
        contactA,
        contactB,
        data: Object.assign({}, ...mergeData.mergeLines.filter(l => l.a === l.b).map(l => ({ [l.key]: "A" })))
      }
    })
  ],
  processError: response => FetchErrorHandler(response, "Failed to get contacts")
};
export const EpicGetMergeContacts: Epic<any, any> = EpicUtils.Create(request);
