/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CERTIFICATE_ITEM, GET_CERTIFICATE_ITEM_FULFILLED } from "../actions/index";
import { Certificate } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_CERTIFICATE_ITEM,
  getData: (id: number) => getEntityItemById("Certificate", id),
  processData: (certificate: Certificate) => {
    return [
      {
        type: GET_CERTIFICATE_ITEM_FULFILLED,
        payload: { certificate }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: certificate, name: certificate.studentName }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, certificate)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetCertificate: Epic<any, any> = EpicUtils.Create(request);
