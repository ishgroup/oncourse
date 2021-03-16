/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_CERTIFICATES_REVOKE_STATUS, SET_CERTIFICATES_REVOKE_STATUS } from "../actions/index";

const request: EpicUtils.Request<DataResponse, number[]> = {
  type: GET_CERTIFICATES_REVOKE_STATUS,
  hideLoadIndicator: true,
  getData: ids => EntityService.getPlainRecords("Certificate", "revokedOn", `id in (${ids.toString()})`),
  processData: ({ rows }) => {
    const hasRevoked = rows.some(r => r.values[0]);

    return [
      {
        type: SET_CERTIFICATES_REVOKE_STATUS,
        payload: { hasRevoked }
      }
    ];
  }
};

export const EpicCheckRevokeStatus: Epic<any, any> = EpicUtils.Create(request);
