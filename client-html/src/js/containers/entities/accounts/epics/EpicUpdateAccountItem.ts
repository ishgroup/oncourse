/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Account } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ACCOUNT_ITEM, UPDATE_ACCOUNT_ITEM } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; account: Account }> = {
  type: UPDATE_ACCOUNT_ITEM,
  getData: ({ id, account }) => updateEntityItemById("Account", id, account),
  processData: (v, s, { id }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Account Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Account", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_ACCOUNT_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { account }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, account)]
};

export const EpicUpdateAccountItem: Epic<any, any> = EpicUtils.Create(request);
