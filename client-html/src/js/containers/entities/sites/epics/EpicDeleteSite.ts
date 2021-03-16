/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_SITE_ITEM, DELETE_SITE_ITEM_FULFILLED } from "../actions/index";
import SiteService from "../services/SiteService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { initialize } from "redux-form";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_SITE_ITEM,
  getData: (id: number) => SiteService.removeSite(id),
  processData: () => {
    return [
      {
        type: DELETE_SITE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Site deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Site", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Site was not deleted")
};

export const EpicDeleteSite: Epic<any, any> = EpicUtils.Create(request);
