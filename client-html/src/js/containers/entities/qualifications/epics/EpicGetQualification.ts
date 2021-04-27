/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_QUALIFICATION_ITEM, GET_QUALIFICATION_ITEM_FULFILLED } from "../actions/index";
import { Qualification } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_QUALIFICATION_ITEM,
  getData: (id: number) => getEntityItemById("Qualification", id),
  processData: (qualification: Qualification) => {
    return [
      {
        type: GET_QUALIFICATION_ITEM_FULFILLED,
        payload: { qualification }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: qualification, name: qualification.title }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, qualification)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetQualification: Epic<any, any> = EpicUtils.Create(request);
