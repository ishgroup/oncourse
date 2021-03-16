/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Contact } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import ContactsService from "../services/ContactsService";
import { DELETE_CONTACT } from "../actions";

const request: EpicUtils.Request<any, number> = {
  type: DELETE_CONTACT,
  getData: contact => ContactsService.deleteContact(contact),
  processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contact was deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Contact" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: (response, contact) => [
    ...FetchErrorHandler(response, "Contact was not deleted"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, contact)
  ]
};

export const EpicDeleteContact: Epic<any, any> = EpicUtils.Create(request);
