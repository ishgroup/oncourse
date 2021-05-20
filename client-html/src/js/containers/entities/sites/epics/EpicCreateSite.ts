/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_SITE_ITEM, CREATE_SITE_ITEM_FULFILLED } from "../actions/index";
import { Site } from "@api/model";
import SiteService from "../services/SiteService";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { site: Site }> = {
  type: CREATE_SITE_ITEM,
  getData: ({ site }) => {
    return SiteService.createSite(site);
  },
  processData: () => {
    return [
      {
        type: CREATE_SITE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Site created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Site" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: (response, { site }) => [
    ...FetchErrorHandler(response, "Site was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, site)
  ]
};

export const EpicCreateSite: Epic<any, any> = EpicUtils.Create(request);
