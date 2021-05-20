/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CORPORATE_PASS_ITEM, GET_CORPORATE_PASS_ITEM_FULFILLED } from "../actions/index";
import { CorporatePass } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { corporatePassNameCondition } from "../CorporatePasses";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_CORPORATE_PASS_ITEM,
  getData: (id: number) => getEntityItemById("CorporatePass", id),
  processData: (corporatePass: CorporatePass) => {
    return [
      {
        type: GET_CORPORATE_PASS_ITEM_FULFILLED,
        payload: { corporatePass }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: corporatePass, name: corporatePassNameCondition(corporatePass) }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, corporatePass)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetCorporatePass: Epic<any, any> = EpicUtils.Create(request);
