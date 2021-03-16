/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Site } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SITE_ITEM, UPDATE_SITE_ITEM, UPDATE_SITE_ITEM_FULFILLED } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; site: Site & { notes: any }; message?: string }> = {
  type: UPDATE_SITE_ITEM,
  getData: ({ id, site }) => {
    delete site.notes;
    return updateEntityItemById("Site", id, site);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id, message }) => [
    {
      type: UPDATE_SITE_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: message || "Site was updated" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Site", listUpdate: true, savedID: id }
    },
    ...s.list.fullScreenEditView ? [{
      type: GET_SITE_ITEM,
      payload: id
    }] : []
  ],
  processError: (response, { site }) => [...FetchErrorHandler(response, "Site was not updated"), initialize(LIST_EDIT_VIEW_FORM_NAME, site)]
};

export const EpicUpdateSite: Epic<any, any> = EpicUtils.Create(request);
