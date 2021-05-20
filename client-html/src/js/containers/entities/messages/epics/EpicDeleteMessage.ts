/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import MessageService from "../services/MessageService";
import { DELETE_MESSAGE_ITEM, DELETE_MESSAGE_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_MESSAGE_ITEM,
  getData: (id: number) => MessageService.removeMessage(id),
  processData: () => [
      {
        type: DELETE_MESSAGE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Message record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Message", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: response => FetchErrorHandler(response, "Message record was not deleted")
};

export const EpicDeleteMessage: Epic<any, any> = EpicUtils.Create(request);
