/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_OUTCOME_ITEM } from "../actions";
import { Outcome } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getEntityItemById } from "../../common/entityItemsService";

const request: EpicUtils.Request = {
  type: GET_OUTCOME_ITEM,
  getData: (id: number) => getEntityItemById("Outcome", id),
  processData: (outcome: Outcome) => [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: outcome, name: outcome.studentName }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, outcome)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetOutcome: Epic<any, any> = EpicUtils.Create(request);
