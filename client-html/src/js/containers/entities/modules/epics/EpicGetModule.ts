/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_MODULE_ITEM, GET_MODULE_ITEM_FULFILLED } from "../actions";
import { Module } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_MODULE_ITEM,
  getData: (id: number) => getEntityItemById("Module", id),
  processData: (module: Module) => {
    return [
      {
        type: GET_MODULE_ITEM_FULFILLED,
        payload: { module }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: module, name: module.title }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, module)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetModule: Epic<any, any> = EpicUtils.Create(request);
