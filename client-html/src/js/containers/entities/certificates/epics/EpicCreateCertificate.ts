/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import CertificateService from "../services/CertificateService";
import { CREATE_CERTIFICATE_ITEM, CREATE_CERTIFICATE_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Certificate } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Certificate;

const request: EpicUtils.Request = {
  type: CREATE_CERTIFICATE_ITEM,
  getData: payload => {
    savedItem = payload.certificate;
    return CertificateService.createCertificate(payload.certificate);
  },
  processData: () => {
    return [
      {
        type: CREATE_CERTIFICATE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Certificate Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Certificate" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Certificate Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateCertificate: Epic<any, any> = EpicUtils.Create(request);
