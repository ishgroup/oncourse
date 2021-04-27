/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED, GET_ACTIVE_FUNDING_CONTRACTS_REQUEST } from "../actions";
import EntityService from "../../../common/services/EntityService";

const request: EpicUtils.Request = {
  type: GET_ACTIVE_FUNDING_CONTRACTS_REQUEST,
  getData: () => EntityService.getPlainRecords("FundingSource", "name,flavour", "active == true"),
  processData: (response: DataResponse, s, addNotFunded) => {
    const contracts: any[] = response.rows.map(({ id, values }) => ({
      id: Number(id),
      name: values[0],
      flavour: values[1]
    }));

    contracts.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

    if (addNotFunded) {
      contracts.splice(0, 0, {
        id: -1,
        name: "Fee for service (non-funded)"
      });
    }

    return [
      {
        type: GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED,
        payload: { contracts }
      }
    ];
  }
};

export const EpicGetActiveFundingContracts: Epic<any, any> = EpicUtils.Create(request);
