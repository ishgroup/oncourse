/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Certificate } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CERTIFICATE_ITEM, UPDATE_CERTIFICATE_ITEM, UPDATE_CERTIFICATE_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; certificate: Certificate }> = {
  type: UPDATE_CERTIFICATE_ITEM,
  getData: ({ id, certificate }) => updateEntityItemById("Certificate", id, certificate),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_CERTIFICATE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Certificate Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Certificate", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_CERTIFICATE_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { certificate }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, certificate)]
};

export const EpicUpdateCertificateItem: Epic<any, any> = EpicUtils.Create(request);
