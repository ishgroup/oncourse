/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_MODULE_ITEM, UPDATE_MODULE_ITEM, UPDATE_MODULE_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Module } from "@api/model";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, any, { id: number; module: Module }> = {
  type: UPDATE_MODULE_ITEM,
  getData: ({ id, module }) => updateEntityItemById("Module", id, module),
  processData: (v, s, { id }) => {
    return [
      {
        type: UPDATE_MODULE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Module Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Module", listUpdate: true, savedID: id }
      },
      {
        type: GET_MODULE_ITEM,
        payload: id
      }
    ];
  },
  processError: (response, { module }) => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, module)];
  }
};

export const EpicUpdateModuleItem: Epic<any, any> = EpicUtils.Create(request);
