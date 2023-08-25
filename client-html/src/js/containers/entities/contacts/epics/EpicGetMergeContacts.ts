/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_MERGE_CONTACTS, GET_MERGE_CONTACTS_FULFILLED } from "../actions";
import ContactsService from "../services/ContactsService";

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
        data: Object.assign(
          {},

          ...mergeData.mergeLines
            .filter(l => l.a === l.b)
            .map(l => ({ [l.key]: "A" })),

          ...mergeData.mergeLines
            .filter(l => l.a !== l.b && (!l.a || !l.b))
            .map(l => ({ [l.key]: (l.a && "A") || (l.b && "B") })))
      }
    })
  ],
  processError: response => FetchErrorHandler(response, "Failed to get contacts")
};

export const EpicGetMergeContacts: Epic<any, any> = EpicUtils.Create(request);