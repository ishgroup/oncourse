/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { CertificateRevokeRequest } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CERTIFICATE_ITEM, REVOKE_CERTIFICATE_ITEM } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import CertificateService from "../services/CertificateService";

const request: EpicUtils.Request<any, { ids: number[]; reason: string }> = {
  type: REVOKE_CERTIFICATE_ITEM,
  getData: ({ ids, reason }) => {
    const revokeRequest: CertificateRevokeRequest = {
      ids,
      revokeReason: reason
    };
    return CertificateService.revokeCertificate(revokeRequest);
  },
  processData: (v, s, { ids }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Certificate revoked successfully" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Certificate", listUpdate: true, savedID: ids[0] }
      },
      {
        type: GET_CERTIFICATE_ITEM,
        payload: ids[0]
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to revoke certificate")
};

export const EpicRevokeCertificate: Epic<any, any> = EpicUtils.Create(request);
